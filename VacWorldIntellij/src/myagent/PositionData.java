
package myagent;

public class PositionData {
    private boolean obstacle;
    private boolean dirt;
    private boolean explored;

    // All squares are assumed clean and unexplored, initially
    public PositionData(boolean obst) {
        this.dirt = false;
        this.explored = false;
        this.obstacle = obst;
    }

    // Setters
    public void setExplored(boolean explored) {
        this.explored = explored;
    }

    public void setObstacle(boolean obstacle) {
        this.obstacle = obstacle;
    }

    public void setDirt(boolean dirt) {
        this.dirt = dirt;
    }

    // Getters
    public boolean getObstacle() {
        return obstacle;
    }

    public boolean getExplored() {
        return explored;
    }

    public boolean getDirt() {
        return dirt;
    }

}
