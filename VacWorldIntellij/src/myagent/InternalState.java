/**
 * Author:      Daniel Phang
 * Class:       CSE431 Intelligent Agents
 * Professor:   Jeff Heflin
 * Date:        September 19, 2013
 * Assignment:  Program #1
 * Environment: Java 1.6
 */

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


// Representa el estado de la aspiradora, permite al agente recordar todas los ubicaciones que ha explorado

public class InternalState
{
    /* Keep track of the locations seen on the map. */
    private final HashMap<Vector2, LocationInformation> worldMap;

    /*
     * Other useful information such as direction, position and a few boolean
     * variables.
     */
    private int agentDirection;
    private Vector2 agentPosition;
    private boolean obstacleSeen;
    private boolean feltBump;
    private boolean dirtSeen;
    private boolean turnedOff;

    /**
     * This construct initializes the internal state by creating a world map to
     * keep track of new locations. The agent's location is also initialized to
     * (0, 0) which is a relative position (since it doesn't know where it is).
     */
    public InternalState() {
        worldMap = new HashMap<Vector2, LocationInformation>();
        agentDirection = Direction.NORTH; // Even though this may not be the
                                          // absolute
        // direction, we need to assume a starting
        // direction
        agentPosition = new Vector2(0, 0); // Similarly, we need to assume a
                                           // starting
                                           // position
        worldMap.put(agentPosition, new LocationInformation(false)); // Put its
                                                                     // starting
                                                                     // location
                                                                     // in the
                                                                     // map
        obstacleSeen = false;
    }

    /**
     * @return the agent's direction number.
     */
    public int getAgentDirection() {
        return agentDirection;
    }

    /**
     * @return the agent's position vector.
     */
    public Vector2 getAgentPosition() {
        return agentPosition;
    }

    /**
     * @return the world map.
     */
    public HashMap<Vector2, LocationInformation> getWorldMap() {
        return worldMap;
    }

    /**
     * Update the internal state with a VacPercept.
     * 
     * @param p
     *            a VacPercept object.
     */
    public void update(VacPercept p) {
        // Put information about current agent's position in the agent's current
        // knowledge of the world.
        LocationInformation current;
        if (!worldMap.containsKey(agentPosition)) {
            current = new LocationInformation(false);
            worldMap.put(agentPosition, current);
        }

        // Update the location to whether it is actually dirty or not
        dirtSeen = p.seeDirt();
        feltBump = p.feelBump();

        current = worldMap.get(agentPosition);
        current.setDirty(dirtSeen);
        current.setExplored(true);

        // Check each possible location adjacent to the agent.
        Vector2 aroundPosition;
        boolean obstacle;
        for (int i = Direction.NORTH; i <= Direction.WEST; ++i) {
            aroundPosition = new Vector2(agentPosition.getX()
                    + Direction.DELTA_X[i], agentPosition.getY()
                    + Direction.DELTA_Y[i]);

            obstacle = false; // Assume initially that the location to be check
                              // has no obstacle.

            // Front location
            if (i == agentDirection) {
                obstacleSeen = p.seeObstacle();
                obstacle = obstacleSeen;
            }

            if (!worldMap.containsKey(aroundPosition)) {
                worldMap.put(aroundPosition, new LocationInformation(obstacle));
            }

            // The agent might have seen this location (i.e it was adjacent)
            // without knowing whether there was an obstacle.
            // If it actually saw an obstacle, we need to update this
            // information.
            if (obstacle) {
                LocationInformation obstacleLoc = worldMap.get(aroundPosition);
                obstacleLoc.setObstacle(obstacle);
            }
        }
    }

    /**
     * Update the agent's position with a new position vector.
     * 
     * @param position
     */
    private void updatePosition(Vector2 position) {
        this.agentPosition = position;
    }

    /**
     * Update the internal state with whether a location is dirty.
     * 
     * @param position
     *            the position to update.
     * @param dirty
     *            whether the position is dirty.
     */
    private void updateDirty(Vector2 position, boolean dirty) {
        // Presumably the position already exists in our map (usually we first
        // update the agent with a Percept, like in the above method, which will
        // add the position to the map. But if not, we add it to the map.
        if (!worldMap.containsKey(position)) {
            worldMap.put(position, new LocationInformation(false));
        }

        LocationInformation current = worldMap.get(position);
        current.setDirty(dirty);
    }

    /**
     * Return whether a location is dirty. If it has not been seen yet, it is
     * assumed to be dirty.
     * 
     * @param position
     * @return
     */
    public boolean isLocationDirty(Vector2 position)
    {
        LocationInformation loc = worldMap.get(position);
        if (loc != null) {
            return loc.isDirty();
        } else {
            return true;
        }
    }

    /**
     * Return whether a location is an obstacle. If it has not been seen yet, it
     * is assumed to be clear.
     * 
     * @param position
     * @return
     */
    public boolean isLocationObstacle(Vector2 position) {
        LocationInformation loc = worldMap.get(position);
        if (loc != null) {
            return loc.isObstacle();
        } else {
            return false;
        }
    }

    /**
     * Return whether a location has been explored (all unseen/unindexed
     * locations are unexplored, and some seen/indexed locations are unexplored
     * if the robot hasn't moved there yet).
     * 
     * @param position
     * @return
     */
    public boolean isLocationExplored(Vector2 position) {
        LocationInformation loc = worldMap.get(position);
        if (loc != null) {
            return loc.isExplored();
        } else {
            return false;
        }
    }

    /**
     * Return whether a location has been seen (i.e. it exists in the worldMap
     * HashMap).
     * 
     * @param position
     * @return
     */
    public boolean isLocationSeen(Vector2 position) {
        return worldMap.containsKey(position);
    }

    /**
     * This represents the cost between two adjacent positions. Here we can use
     * the turn heuristic (and it gives an accurate cost measurement) because
     * the two positions here are assumed to be adjacent
     * 
     * @param start
     * @param end
     * @param direction
     *            the face direction (if this direction corresponds to how the
     *            two positions are actually oriented, the cost is lower
     *            (because the agent doesn't have to turn)
     * @return
     */
    public static int adjacentCost(Vector2 start, Vector2 end, int direction) {
        return HeuristicVac.getCost(start, end, direction); // This heuristic
                                                               // is the actual
                                                               // cost since we
                                                               // are comparing
                                                               // two adjacent
                                                               // squares.
    }

    /**
     * Returns all (possibly) explorable neighbors of a given position as a
     * linked list of Nodes. For example, if a location has been indexed (e.g.
     * it was to the side of an explored location, but not necessarily in front
     * of the agent), this will capture those locations.
     * 
     * @param position
     *            the position to check for explorable neighbors.
     * @return
     */
    public LinkedList<Vector2> neighbors(Vector2 position) {
        LinkedList<Vector2> actualNeighbors = new LinkedList<Vector2>();

        // Iterate through all potential neighbors, but only add them to our
        // linked list if they
        // aren't known to be obstacles
        Vector2 neighbor;
        for (int i = Direction.NORTH; i <= Direction.WEST; ++i) {
            neighbor = new Vector2(position.getX() + Direction.DELTA_X[i],
                    position.getY() + Direction.DELTA_Y[i]);

            if (worldMap.containsKey(neighbor) && !isLocationObstacle(neighbor)) {
                actualNeighbors.add(neighbor);
            }
        }

        return actualNeighbors;
    }

    /**
     * Updates the internal state according to what action was given. For
     * example, if a suck dirt action is applied, we should tell the internal
     * state that now the current square is no longer dirty.
     * 
     * @param next
     */
    public void update(Action next) {
        if (next instanceof SuckDirt) { // If the pending action was to suck
                                        // dirt, update the current location's
                                        // dirtiness.
            updateDirty(agentPosition, false);
        } else if (next instanceof GoForward && !isFeltBump()) { // Change the
                                                                 // internal
                                                                 // position if
                                                                 // the agent
                                                                 // went
                                                                 // forward. For
                                                                 // completeness,
                                                                 // we only do
                                                                 // this if it
                                                                 // didn't feel
                                                                 // a bump
                                                                 // (though for
                                                                 // this agent,
                                                                 // it will
                                                                 // never feel a
                                                                 // bump).
            updatePosition(Vector2.add(agentPosition,
                    Vector2.directionToVector(agentDirection)));
        } else if (next instanceof TurnLeft) { // For turns, change the
                                               // direction appropriately.
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

    /**
     * 
     * @return whether an obstacle was recently seen.
     */
    public boolean isObstacleSeen() {
        return obstacleSeen;
    }

    /**
     * 
     * @return whether a bump was felt.
     */
    public boolean isFeltBump() {
        return feltBump;
    }

    /**
     * 
     * @return whether the agent is turned off.
     */
    public boolean isTurnedOff() {
        return turnedOff;
    }

    /**
     * 
     * @return whether dirt was just seen.
     */
    public boolean isDirtSeen() {
        return dirtSeen;
    }
}
