
package myagent;

import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map.Entry;
import java.util.PriorityQueue;
import java.util.Set;

import vacworld.GoForward;
import vacworld.ShutOff;
import vacworld.SuckDirt;
import vacworld.TurnLeft;
import vacworld.TurnRight;
import agent.Action;


public class Path {

    /* agent's internal state */
    InternalState vacuumState;

    /* actions that the agent may decide to take */
    LinkedList<Action> planVacuum;


    public Path(InternalState vacuumState) {
        this.vacuumState = vacuumState;
        this.planVacuum = new LinkedList<Action>();
    }

    /* next Action for agent to perform */

    public Action nextAction() {
        if (vacuumState.isTurnedOff()) {
            return null;
        }


    // dynamically change the plan.
        if (vacuumState.isObstacleSeen() || vacuumState.isDirtSeen()
                || vacuumState.isFeltBump()) {
            planVacuum.clear();
        }

    // If there's no plan, build a new plan
        if (planVacuum.isEmpty()) {
            buildPlanVacuum();
        }

        Action next = planVacuum.remove();

    // Update agent state according to what action are about to perform
        vacuumState.update(next);

        return next;
    }

    //build plan depend on the current action
    private void buildPlanVacuum() {
        final Vector2 position = vacuumState.getAgentPosition();
        if (vacuumState.isLocationDirty(position)) {
            planVacuum.add(new SuckDirt());
        } else {
            nPlan();
        }
        if (planVacuum.isEmpty()) {
            planVacuum.add(new ShutOff());
        }
    }


    private void nPlan() {
        // Find an unexplored location best path to move using turn cost and Manhattan distance
        final Vector2 unexplored = unexploredPosition();

        // done exploring all
        if (unexplored == null) {
            return;
        }

        // Use an A* search to find the best path
        LinkedList<Vector2> path = findPath(unexplored);

        if (!path.isEmpty()) {
            path.remove();
        }

        Vector2 current = vacuumState.getAgentPosition();
        Vector2 next;
        int currentDirection = vacuumState.getAgentDirection();
        int nextDirection;

        //build the correct sequence of actions in plan
        while (!path.isEmpty()) {
            next = path.remove();

            // direction between the two tiles, current and next are adjacent

            nextDirection = Vector2.vectorToDirection(Vector2
                    .sub(next, current));

            if (nextDirection != currentDirection) {

                int diff = nextDirection - currentDirection;
                if (diff > 3) {
                    diff = diff - 4;
                } else if (diff < 0) {
                    diff = diff + 4;
                }
                if (diff == 1) {
                    planVacuum.add(new TurnRight());
                } else if (diff == 2) {
                    planVacuum.add(new TurnRight());
                    planVacuum.add(new TurnRight());
                } else if (diff == 3) {
                    planVacuum.add(new TurnLeft());
                }
            }

            planVacuum.add(new GoForward());

            current = next;
            currentDirection = nextDirection;
        }
    }


    private Vector2 unexploredPosition() {

        final HashMap<Vector2, LocationInformation> map = vacuumState.getWorldMap();
        Entry<Vector2, LocationInformation> pair;

        // Lowest cost initialized to the maximum value possible
        int lowestCost = Integer.MAX_VALUE;
        int cost;
        Vector2 lowestCostPosition = null;

        // find the lowest cost position using heuristic
        Iterator<Entry<Vector2, LocationInformation>> it = map.entrySet()
                .iterator();
        Vector2 pos;

        while (it.hasNext()) {
            pair = it.next();
            pos = pair.getKey();
            if (!vacuumState.isLocationExplored(pos)
                    && !vacuumState.isLocationObstacle(pos)) {
                cost = HeuristicVac.getCost(vacuumState.getAgentPosition(), pos,
                        vacuumState.getAgentDirection());

                if (cost < lowestCost) {
                    lowestCost = cost;
                    lowestCostPosition = pair.getKey();
                }
            }

        }

        return lowestCostPosition;
    }

    //Find the shortest known path between the current position and the given goal position, uses A*.
    private LinkedList<Vector2> findPath(Vector2 goal) {
        //start position and direction
        Vector2 start = vacuumState.getAgentPosition();
        int currentDirection = vacuumState.getAgentDirection();

        // posibble G score
        int possibleG;

        HashMap<Vector2, Vector2> cameFrom = new HashMap<Vector2, Vector2>();

        final HashMap<Vector2, Integer> g = new HashMap<Vector2, Integer>();
        final HashMap<Vector2, Integer> f = new HashMap<Vector2, Integer>();

        HashMap<Vector2, LocationInformation> worldMap = vacuumState.getWorldMap();
        Iterator<Entry<Vector2, LocationInformation>> it = worldMap.entrySet()
                .iterator();
        while (it.hasNext()) {
            Vector2 pos = it.next().getKey();
            f.put(pos, 0);
            g.put(pos, 0);
        }

        // priority queue on f-scores.
        PriorityQueue<Vector2> open = new PriorityQueue<Vector2>(11,
                new Comparator<Vector2>() {
                    public int compare(Vector2 a, Vector2 b) {
                        return f.get(a) - f.get(b);
                    }
                });

        Set<Vector2> closed = new HashSet<Vector2>();
        g.put(start, 0);
        f.put(start,
                g.get(start)
                        + HeuristicVac
                                .getCost(start, goal, currentDirection));

        // Linked List to keep track of neighbors, Vector elements

        LinkedList<Vector2> neighbors;
        Vector2 neighbor;
        Vector2 current;

        // Add the start node to the open set
        open.add(start);

        while (!open.isEmpty()) {
            current = open.remove();

            if (current.equals(goal)) {
                return buildPath(cameFrom, goal);
            }

            open.remove(current);
            closed.add(current);

            neighbors = vacuumState.neighbors(current);
            Iterator<Vector2> it2 = neighbors.iterator();
            while (it2.hasNext()) {
                neighbor = it2.next();

                currentDirection = Vector2.vectorToDirection(Vector2.sub(
                        neighbor, current));

                possibleG = g.get(current)
                        + InternalState.adjacentCost(current, neighbor,
                                currentDirection);
                if (closed.contains(neighbor) && possibleG >= g.get(neighbor)) {
                    continue;
                }

                // Update the appropriate data structures with best path
                if (g.get(neighbor) == 0 || possibleG < g.get(neighbor)) {
                    cameFrom.put(neighbor, current);
                    g.put(neighbor, possibleG);

                    f.put(neighbor,
                            g.get(neighbor)
                                    + HeuristicVac.getCost(neighbor, goal,
                                            currentDirection));
                    if (!open.contains(neighbor)) {
                        open.add(neighbor);
                    }
                }
            }

        }
        return null;

    }

    // Recursively constructs the optimal path found using A* algorithm
    private static LinkedList<Vector2> buildPath(
            HashMap<Vector2, Vector2> cameFrom, Vector2 current) {
        LinkedList<Vector2> p;
        if (cameFrom.containsKey(current)) {
            p = buildPath(cameFrom, cameFrom.get(current));
            p.add(current);
            return p;
        } else {
            p = new LinkedList<Vector2>();
            p.add(current);
            return p;
        }
    }
}
