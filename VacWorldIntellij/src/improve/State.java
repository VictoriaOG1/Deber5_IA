package improve;

import java.util.HashMap;
import java.util.LinkedList;

import vacworld.Direction;
import vacworld.GoForward;
import vacworld.ShutOff;
import vacworld.SuckDirt;
import vacworld.TurnLeft;
import vacworld.TurnRight;
import vacworld.VacPercept;
import agent.Action;

public class State {
    private final HashMap<UnitVector, iLocation> vacMap;

    private int agentDirection;
    private UnitVector agentPosition;
    private boolean obstacleSeen;
    private boolean feltBump;
    private boolean dirtSeen;
    private boolean turnedOff;

    public State() {
        vacMap = new HashMap<UnitVector, iLocation>();
        agentDirection = Direction.NORTH;
        agentPosition = new UnitVector(0, 0);
        vacMap.put(agentPosition, new iLocation(false));
        obstacleSeen = false;
    }

    public int getAgentDirection() {
        return agentDirection;
    }

    public UnitVector getAgentPosition() {
        return agentPosition;
    }

    public HashMap<UnitVector, iLocation> getWorldMap() {
        return vacMap;
    }

    public void update(VacPercept p) {
        iLocation current;
        if (!vacMap.containsKey(agentPosition)) {
            current = new iLocation(false);
            vacMap.put(agentPosition, current);
        }

        dirtSeen = p.seeDirt();
        feltBump = p.feelBump();

        current = vacMap.get(agentPosition);
        current.setDirty(dirtSeen);
        current.setExplored(true);

        UnitVector aroundPosition;
        boolean obstacle;
        for (int i = Direction.NORTH; i <= Direction.WEST; ++i) {
            aroundPosition = new UnitVector(agentPosition.getX()
                    + Direction.DELTA_X[i],
                    agentPosition.getY()
                            + Direction.DELTA_Y[i]);

            obstacle = false;
            if (i == agentDirection) {
                obstacleSeen = p.seeObstacle();
                obstacle = obstacleSeen;
            }

            if (!vacMap.containsKey(aroundPosition)) {
                vacMap.put(aroundPosition, new iLocation(obstacle));
            }

            if (obstacle) {
                iLocation obstacleLoc = vacMap.get(aroundPosition);
                obstacleLoc.setObstacle(obstacle);
            }
        }
    }

    private void updatePosition(UnitVector position) {
        this.agentPosition = position;
    }

    private void updateDirty(UnitVector position, boolean dirty) {
        if (!vacMap.containsKey(position)) {
            vacMap.put(position, new iLocation(false));
        }

        iLocation current = vacMap.get(position);
        current.setDirty(dirty);
    }

    public boolean isLocationDirty(UnitVector position) {
        iLocation loc = vacMap.get(position);
        if (loc != null) {
            return loc.isDirty();
        } else {
            return true;
        }
    }

    public boolean isLocationObstacle(UnitVector position) {
        iLocation loc = vacMap.get(position);
        if (loc != null) {
            return loc.isObstacle();
        } else {
            return false;
        }
    }

    public boolean isLocationExplored(UnitVector position) {
        iLocation loc = vacMap.get(position);
        if (loc != null) {
            return loc.isExplored();
        } else {
            return false;
        }
    }

    public boolean isLocationSeen(UnitVector position) {
        return vacMap.containsKey(position);
    }

    public static int adjacentCost(UnitVector start, UnitVector end, int direction) {
        return Heuristic.estimateC(start, end, direction);
    }

    public LinkedList<UnitVector> neighbors(UnitVector position) {
        LinkedList<UnitVector> actualNeighbors = new LinkedList<UnitVector>();
        UnitVector neighbor;

        for (int i = Direction.NORTH; i <= Direction.WEST; ++i) {
            neighbor = new UnitVector(position.getX() + Direction.DELTA_X[i],
                    position.getY() + Direction.DELTA_Y[i]);

            if (vacMap.containsKey(neighbor) && !isLocationObstacle(neighbor)) {
                actualNeighbors.add(neighbor);
            }
        }

        return actualNeighbors;
    }

    public void update(Action next) {
        if (next instanceof SuckDirt) {
            updateDirty(agentPosition, false);
        } else if (next instanceof GoForward && !isFeltBump()) {
            updatePosition(UnitVector.addition(agentPosition,
                    UnitVector.dirVector(agentDirection)));
        } else if (next instanceof TurnLeft) {
            --agentDirection;
            if (agentDirection < Direction.NORTH) {
                agentDirection = Direction.WEST;
            }
        } else if (next instanceof TurnRight) {
            ++agentDirection;
            if (agentDirection > Direction.WEST) {
                agentDirection = Direction.NORTH;
            }
        } else if (next instanceof ShutOff) {
            turnedOff = true;
        }
    }

    public boolean isObstacleSeen() {
        return obstacleSeen;
    }

    public boolean isFeltBump() {
        return feltBump;
    }

    public boolean isTurnedOff() {
        return turnedOff;
    }

    public boolean isDirtSeen() {
        return dirtSeen;
    }

}
