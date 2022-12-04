
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

    // Agent's internal state
    InterState vacuumState;

    // Actions that the agent may decide to take
    LinkedList<Action> planVacuum;

    public Path(InterState vacuumState) {
        this.vacuumState = vacuumState;
        this.planVacuum = new LinkedList<Action>();
    }

    // Next Action for agent to perform
    public Action nextAction() {
        if (vacuumState.isTOff()) {
            return null;
        }

        // Dynamically change the plan
        if (vacuumState.isObstSeen() || vacuumState.isDirtDetected()
                || vacuumState.isFbump()) {
            planVacuum.clear();
        }

        // If there's no plan, build a new plan
        if (planVacuum.isEmpty()) {
            buildPlanVacuum();
        }

        Action nextStep = planVacuum.remove();

        // Update agent state according to what action are about to perform
        vacuumState.updateIS(nextStep);

        return nextStep;
    }

    // Build plan depend on the curr action
    private void buildPlanVacuum() {
        final PosVector position = vacuumState.getAgentP();
        if (vacuumState.isLocDirty(position)) {
            planVacuum.add(new SuckDirt());
        } else {
            nPlan();
        }
        if (planVacuum.isEmpty()) {
            planVacuum.add(new ShutOff());
        }
    }

    // Builds a movement plan for the agent by adding actions
    private void nPlan() {
        // Find an unexplored location best path to move using
        // turn cost and Manhattan distance
        final PosVector unexplored = unexploredPosition();

        // Done exploring all
        if (unexplored == null) {
            return;
        }

        // Use an A* search to find the best path
        LinkedList<PosVector> path = findPath(unexplored);

        if (!path.isEmpty()) {
            path.remove();
        }

        PosVector curr = vacuumState.getAgentP();
        PosVector nextStep;
        int currentDirection = vacuumState.getAgentD();
        int nextDirection;

        // Build the correct sequence of actions in plan
        while (!path.isEmpty()) {
            nextStep = path.remove();

            // Direction between the two tiles, curr and nextStep are adjacent

            nextDirection = PosVector.vecToDir(PosVector
                    .subtraction(nextStep, curr));

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

            curr = nextStep;
            currentDirection = nextDirection;
        }
    }

    // Finds an unexplored position to explore
    private PosVector unexploredPosition() {

        final HashMap<PosVector, PositionData> map = vacuumState.getMap();
        Entry<PosVector, PositionData> pair;

        // Lowest cost initialized to the maximum value possible
        int lowestC = Integer.MAX_VALUE;
        int cost;
        PosVector lowestCostPosition = null;

        // Find the lowest cost position using heuristic
        Iterator<Entry<PosVector, PositionData>> it = map.entrySet()
                .iterator();
        PosVector pos;

        while (it.hasNext()) {
            pair = it.next();
            pos = pair.getKey();
            if (!vacuumState.isLocExplored(pos)
                    && !vacuumState.isLocObstacle(pos)) {
                cost = HeuristicVac.getCost(vacuumState.getAgentP(), pos,
                        vacuumState.getAgentD());

                if (cost < lowestC) {
                    lowestC = cost;
                    lowestCostPosition = pair.getKey();
                }
            }

        }

        return lowestCostPosition;
    }

    // Find the shortest known path between the curr position
    // and the given goal position, uses A*
    private LinkedList<PosVector> findPath(PosVector goal) {

        // Start position and direction
        PosVector start = vacuumState.getAgentP();
        int currentDirection = vacuumState.getAgentD();

        // Posibble G score
        int possibleG;

        HashMap<PosVector, PosVector> cameF = new HashMap<PosVector, PosVector>();

        final HashMap<PosVector, Integer> g = new HashMap<PosVector, Integer>();
        final HashMap<PosVector, Integer> f = new HashMap<PosVector, Integer>();

        HashMap<PosVector, PositionData> wMap = vacuumState.getMap();
        Iterator<Entry<PosVector, PositionData>> it = wMap.entrySet()
                .iterator();
        while (it.hasNext()) {
            PosVector pos = it.next().getKey();
            f.put(pos, 0);
            g.put(pos, 0);
        }

        // Priority queue on f-scores.
        PriorityQueue<PosVector> o = new PriorityQueue<PosVector>(11,
                new Comparator<PosVector>() {
                    public int compare(PosVector a, PosVector b) {
                        return f.get(a) - f.get(b);
                    }
                });

        Set<PosVector> closed = new HashSet<PosVector>();
        g.put(start, 0);
        f.put(start,
                g.get(start)
                        + HeuristicVac
                                .getCost(start, goal, currentDirection));

        // Linked List to keep track of neighbors, Vector elements

        LinkedList<PosVector> neighbors;
        PosVector neighbor;
        PosVector curr;

        // Add the start node to the o set
        o.add(start);

        while (!o.isEmpty()) {
            curr = o.remove();

            if (curr.equals(goal)) {
                return buildPath(cameF, goal);
            }

            o.remove(curr);
            closed.add(curr);

            neighbors = vacuumState.nextTo(curr);
            Iterator<PosVector> it2 = neighbors.iterator();
            while (it2.hasNext()) {
                neighbor = it2.next();

                currentDirection = PosVector.vecToDir(PosVector.subtraction(
                        neighbor, curr));

                possibleG = g.get(curr)
                        + InterState.adjCost(curr, neighbor,
                                currentDirection);
                if (closed.contains(neighbor) && possibleG >= g.get(neighbor)) {
                    continue;
                }

                // Update the appropriate data structures with best path
                if (g.get(neighbor) == 0 || possibleG < g.get(neighbor)) {
                    cameF.put(neighbor, curr);
                    g.put(neighbor, possibleG);

                    f.put(neighbor,
                            g.get(neighbor)
                                    + HeuristicVac.getCost(neighbor, goal,
                                            currentDirection));
                    if (!o.contains(neighbor)) {
                        o.add(neighbor);
                    }
                }
            }

        }
        return null;

    }

    // Recursively constructs the optimal path found using A* algorithm
    private static LinkedList<PosVector> buildPath(
            HashMap<PosVector, PosVector> cameF, PosVector curr) {
        LinkedList<PosVector> p;
        if (cameF.containsKey(curr)) {
            p = buildPath(cameF, cameF.get(curr));
            p.add(curr);
            return p;
        } else {
            p = new LinkedList<PosVector>();
            p.add(curr);
            return p;
        }
    }
}
