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

    private int agentDir;
    private UnitVector posAgent;
    private boolean obstacleSeen;
    private boolean feltBump;
    private boolean turnedOff;
    private boolean dirtSeen;

    public State() {
        vacMap = new HashMap<UnitVector, iLocation>();
        agentDir = Direction.NORTH;
        posAgent = new UnitVector(0, 0);
        vacMap.put(posAgent, new iLocation(false));
        obstacleSeen = false;
    }

    public int getAgentDirection() {
        return agentDir;
    }

    public UnitVector getAgentPosition() {
        return posAgent;
    }

    public HashMap<UnitVector, iLocation> getWorldMap() {
        return vacMap;
    }

    public void updateState(VacPercept p) {
        iLocation currentPos;
        if (!vacMap.containsKey(posAgent)) {
            currentPos = new iLocation(false);
            vacMap.put(posAgent, currentPos);
        }

        dirtSeen = p.seeDirt();
        feltBump = p.feelBump();

        currentPos = vacMap.get(posAgent);
        currentPos.setDirty(dirtSeen);
        currentPos.setExplored(true);

        UnitVector aroundPosition;
        boolean obstacle;
        for (int i = Direction.NORTH; i <= Direction.WEST; ++i) {
            aroundPosition = new UnitVector(posAgent.getX()
                    + Direction.DELTA_X[i],
                    posAgent.getY()
                            + Direction.DELTA_Y[i]);

            obstacle = false;

            if (i == agentDir) {// Front location
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
        this.posAgent = position;
    }

    private void updateDirty(UnitVector position, boolean dirty) {
        if (!vacMap.containsKey(position)) {
            vacMap.put(position, new iLocation(false));
        }

        iLocation currentPos = vacMap.get(position);
        currentPos.setDirty(dirty);
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
            updateDirty(posAgent, false);
        } else if (next instanceof GoForward && !isFeltBump()) {
            updatePosition(UnitVector.addition(posAgent,
                    UnitVector.dirToVec(agentDir)));
        } else if (next instanceof TurnLeft) {
            --agentDir;
            if (agentDir < Direction.NORTH) {
                agentDir = Direction.WEST;
            }
        } else if (next instanceof TurnRight) {
            ++agentDir;
            if (agentDir > Direction.WEST) {
                agentDir = Direction.NORTH;
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
