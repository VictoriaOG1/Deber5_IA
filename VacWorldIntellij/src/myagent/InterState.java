
package myagent;

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

public class InterState {
    // Locations on the map
    private final HashMap<PosVector, PositionData> wMap;

    private int agentD; // direction
    private PosVector agentP; // position
    private boolean obstSeen; // obstacle
    private boolean Fbump;
    private boolean dirtDetected;
    private boolean tOff; // turned off

    // Constructor
    public InterState() {
        wMap = new HashMap<PosVector, PositionData>();
        agentD = Direction.NORTH;
        agentP = new PosVector(0, 0);
        wMap.put(agentP, new PositionData(false));
        obstSeen = false;
    }

    public int getAgentD() {
        return agentD;
    }

    public PosVector getAgentP() {
        return agentP;
    }

    // Map
    public HashMap<PosVector, PositionData> getMap() {
        return wMap;
    }

    // Update InterState with VacPercept
    public void updateS(VacPercept p) {
        PositionData cu;
        if (!wMap.containsKey(agentP)) {
            cu = new PositionData(false);
            wMap.put(agentP, cu);
        }

        dirtDetected = p.seeDirt();
        Fbump = p.feelBump();

        cu = wMap.get(agentP);
        cu.setDirt(dirtDetected);
        cu.setExplored(true);

        // Check each possible location to the agent
        PosVector aPosition;
        boolean obstacle;
        for (int i = Direction.NORTH; i <= Direction.WEST; ++i) {
            aPosition = new PosVector(agentP.getX()
                    + Direction.DELTA_X[i],
                    agentP.getY()
                            + Direction.DELTA_Y[i]);

            obstacle = false; // Assume the location has no obstacle

            if (i == agentD) {
                obstSeen = p.seeObstacle();
                obstacle = obstSeen;
            }

            if (!wMap.containsKey(aPosition)) {
                wMap.put(aPosition, new PositionData(obstacle));
            }

            // If there is an obstacle, udpate the data
            if (obstacle) {
                PositionData obstacleLoc = wMap.get(aPosition);
                obstacleLoc.setObstacle(obstacle);
            }
        }
    }

    // Update agent position
    private void updateP(PosVector pos) {
        this.agentP = pos;
    }

    // Update InterState with the dirt location
    private void updateDirt(PosVector pos, boolean dirt) {
        if (!wMap.containsKey(pos)) {
            wMap.put(pos, new PositionData(false));
        }

        PositionData cu = wMap.get(pos);
        cu.setDirt(dirt);
    }

    // Get the location if is dirt
    public boolean isLocDirty(PosVector pos) {
        PositionData loc = wMap.get(pos);
        if (loc != null) {
            return loc.getDirt();
        } else {
            return true;
        }
    }

    // Get the location if is an obstacle
    public boolean isLocObstacle(PosVector pos) {
        PositionData loc = wMap.get(pos);
        if (loc != null) {
            return loc.getObstacle();
        } else {
            return false;
        }
    }

    // Get the location if has been explored
    public boolean isLocExplored(PosVector pos) {
        PositionData loc = wMap.get(pos);
        if (loc != null) {
            return loc.getExplored();
        } else {
            return false;
        }
    }

    // Get the location if has been seen
    public boolean isLocSeen(PosVector pos) {
        return wMap.containsKey(pos);
    }

    // Cost between two positions
    public static int adjCost(PosVector i, PosVector e, int d) {
        return HeuristicVac.getCost(i, e, d);
    }

    // Get all explorable locations
    public LinkedList<PosVector> nextTo(PosVector pos) {
        LinkedList<PosVector> actualNeighbors = new LinkedList<PosVector>();

        PosVector neighbor;
        for (int i = Direction.NORTH; i <= Direction.WEST; ++i) {
            neighbor = new PosVector(pos.getX() + Direction.DELTA_X[i],
                    pos.getY() + Direction.DELTA_Y[i]);

            if (wMap.containsKey(neighbor) && !isLocObstacle(neighbor)) {
                actualNeighbors.add(neighbor);
            }
        }

        return actualNeighbors;
    }

    // Update InterState according the action
    public void updateIS(Action next) {
        if (next instanceof SuckDirt) {
            updateDirt(agentP, false);
        } else if (next instanceof GoForward && !isFbump()) {
            updateP(PosVector.addition(agentP,
                    PosVector.dirToVec(agentD)));
        } else if (next instanceof TurnLeft) {
            --agentD;
            if (agentD < Direction.NORTH) {
                agentD = Direction.WEST;
            }
        } else if (next instanceof TurnRight) {
            ++agentD;
            if (agentD > Direction.WEST) {
                agentD = Direction.NORTH;
            }
        } else if (next instanceof ShutOff) {
            tOff = true;
        }
    }

    public boolean isObstSeen() {
        return obstSeen; // obstacle seen
    }

    public boolean isFbump() {
        return Fbump; // bump felt
    }

    public boolean isTOff() {
        return tOff; // turned off
    }

    public boolean isDirtDetected() {
        return dirtDetected; // dirt detected
    }
}
