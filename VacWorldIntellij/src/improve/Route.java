package improve;

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

public class Route {
    State state;
    LinkedList<Action> route;

    // Constructor
    public Route(State state) {
        this.state = state;
        this.route = new LinkedList<Action>();
    }

    // Decide the nextStep step
    public Action nextAction() {
        if (state.isTurnedOff()) {
            return null;
        }

        // If detected an obstacle, dirt, or bump change route
        if (state.isObstacleSeen() || state.isDirtSeen()
                || state.isFeltBump()) {
            route.clear();
        }

        if (route.isEmpty()) {
            buildRoute();
        }

        Action nextStep = route.remove();
        state.update(nextStep);

        return nextStep;
    }

    private void buildRoute() {
        final UnitVector position = state.getAgentPosition();
        if (state.isLocationDirty(position)) {
            route.add(new SuckDirt());
        } else {
            buildMovementRoute();
        }

        // If a route is empty, presumably there was nothing to do, done.
        if (route.isEmpty()) {
            route.add(new ShutOff());
        }
    }

    private void buildMovementRoute() {
        final UnitVector unexplored = findUnexploredPosition();

        if (unexplored == null) {
            return;
        }

        LinkedList<UnitVector> path = findPath(unexplored);

        if (!path.isEmpty()) {
            path.remove();
        }

        UnitVector current = state.getAgentPosition();
        UnitVector next;
        int currentDirection = state.getAgentDirection();
        int nextDirection;

        while (!path.isEmpty()) {
            next = path.remove();
            nextDirection = UnitVector.vecToDir(UnitVector
                    .subtract(next, current));

            if (nextDirection != currentDirection) {
                int diff = nextDirection - currentDirection;
                if (diff > 3) {
                    diff = diff - 4;
                } else if (diff < 0) {
                    diff = diff + 4;
                }
                if (diff == 1) {
                    route.add(new TurnRight());
                } else if (diff == 2) {
                    route.add(new TurnRight());
                    route.add(new TurnRight());
                } else if (diff == 3) {
                    route.add(new TurnLeft());
                }
            }

            route.add(new GoForward());

            current = next;
            currentDirection = nextDirection;
        }
    }

    private UnitVector findUnexploredPosition() {
        final HashMap<UnitVector, iLocation> map = state.getWorldMap();
        Entry<UnitVector, iLocation> pair;

        int lowestCost = Integer.MAX_VALUE;
        int cost;
        UnitVector lowestCostPosition = null;

        Iterator<Entry<UnitVector, iLocation>> it = map.entrySet()
                .iterator();
        UnitVector pos;

        while (it.hasNext()) {
            pair = it.next();
            pos = pair.getKey();
            if (!state.isLocationExplored(pos)
                    && !state.isLocationObstacle(pos)) {
                cost = Heuristic.estimateC(state.getAgentPosition(), pos,
                        state.getAgentDirection());

                if (cost < lowestCost) {
                    lowestCost = cost;
                    lowestCostPosition = pair.getKey();
                }
            }

        }

        return lowestCostPosition;
    }

    private LinkedList<UnitVector> findPath(UnitVector goal) {
        UnitVector start = state.getAgentPosition();
        int currentDirection = state.getAgentDirection();

        int tentativeG;

        HashMap<UnitVector, UnitVector> cameFrom = new HashMap<UnitVector, UnitVector>();

        final HashMap<UnitVector, Integer> g = new HashMap<UnitVector, Integer>();
        final HashMap<UnitVector, Integer> f = new HashMap<UnitVector, Integer>();

        HashMap<UnitVector, iLocation> worldMap = state.getWorldMap();
        Iterator<Entry<UnitVector, iLocation>> it = worldMap.entrySet()
                .iterator();
        while (it.hasNext()) {
            UnitVector pos = it.next().getKey();
            f.put(pos, 0);
            g.put(pos, 0);
        }

        PriorityQueue<UnitVector> open = new PriorityQueue<UnitVector>(11,
                new Comparator<UnitVector>() {
                    public int compare(UnitVector a, UnitVector b) {
                        return f.get(a) - f.get(b);
                    }
                });

        Set<UnitVector> closed = new HashSet<UnitVector>();
        g.put(start, 0);
        f.put(start,
                g.get(start)
                        + Heuristic
                                .estimateC(start, goal, currentDirection));

        LinkedList<UnitVector> neighbors;
        UnitVector neighbor;
        UnitVector current;

        open.add(start);

        while (!open.isEmpty()) {
            current = open.remove();
            if (current.equals(goal)) {
                return constructPath(cameFrom, goal);
            }

            open.remove(current);
            closed.add(current);

            neighbors = state.neighbors(current);
            Iterator<UnitVector> it2 = neighbors.iterator();
            while (it2.hasNext()) {
                neighbor = it2.next();

                currentDirection = UnitVector.vecToDir(UnitVector.subtract(
                        neighbor, current));

                tentativeG = g.get(current)
                        + State.adjacentCost(current, neighbor,
                                currentDirection);
                if (closed.contains(neighbor) && tentativeG >= g.get(neighbor)) {
                    continue;
                }

                if (g.get(neighbor) == 0 || tentativeG < g.get(neighbor)) {
                    cameFrom.put(neighbor, current);
                    g.put(neighbor, tentativeG);

                    f.put(neighbor,
                            g.get(neighbor)
                                    + Heuristic.estimateC(neighbor, goal,
                                            currentDirection));
                    if (!open.contains(neighbor)) {
                        open.add(neighbor);
                    }
                }
            }

        }

        return null;

    }

    private static LinkedList<UnitVector> constructPath(
            HashMap<UnitVector, UnitVector> cameFrom, UnitVector current) {
        LinkedList<UnitVector> p;
        if (cameFrom.containsKey(current)) {
            p = constructPath(cameFrom, cameFrom.get(current));
            p.add(current);
            return p;
        } else {
            p = new LinkedList<UnitVector>();
            p.add(current);
            return p;
        }
    }

}
