/**
 * Author:      Daniel Phang
 * Class:       CSE431 Intelligent Agents
 * Professor:   Jeff Heflin
 * Date:        September 19, 2013
 * Assignment:  Program #1
 * Environment: Java 1.6
 */

package myagent;

/**
 * Represent information about a particular location, such as whether it is
 * known to have an obstacle, is dirty, or has been explored by our agent. This
 * is used in conjunction with a HashMap to store information about positions.
 * Technically, we do not have to keep track of the dirtiness of a location,
 * because our agent always cleans a location when it reaches a dirty location.
 * But this is included for completeness.
 * 
 * @author Daniel Phang
 * 
 */
public class LocationInformation {
    private boolean obstacle;
    private boolean dirty;
    private boolean explored;

    /**
     * All squares are assumed to be initially clean and unexplored, although it
     * is possible to initialize a location as having an obstacle (for example,
     * if the agent sees the square in front).
     * 
     * @param obstacle
     */
    public LocationInformation(boolean obstacle) {
        this.dirty = false;
        this.explored = false;
        this.obstacle = obstacle;
    }

    /**
     * @return whether this location has an obstacle.
     */
    public boolean isObstacle() {
        return obstacle;
    }

    /**
     * @return whether this location is explored.
     */
    public boolean isExplored() {
        return explored;
    }

    /**
     * Set this location to be explored.
     * 
     * @param explored
     */
    public void setExplored(boolean explored) {
        this.explored = explored;
    }

    /**
     * 
     * @return whether this location is dirty.
     */
    public boolean isDirty() {
        return dirty;
    }

    /**
     * Set this location to be dirty.
     * 
     * @param dirty
     */
    public void setDirty(boolean dirty) {
        this.dirty = dirty;
    }

    /**
     * Set this location to be an obstacle.
     * 
     * @param obstacle
     */
    public void setObstacle(boolean obstacle) {
        this.obstacle = obstacle;
    }
}
