package improve;

public class iLocation {
    private boolean obstacle;
    private boolean dirty;
    private boolean explored;

    public iLocation(boolean obstacle) {
        this.dirty = false;
        this.explored = false;
        this.obstacle = obstacle;
    }

    public void setObstacle(boolean obstacle) {
        this.obstacle = obstacle;
    }

    public void setExplored(boolean explored) {
        this.explored = explored;
    }

    public void setDirty(boolean dirty) {
        this.dirty = dirty;
    }

    public boolean isObstacle() {
        return obstacle;
    }

    public boolean isExplored() {
        return explored;
    }

    public boolean isDirty() {
        return dirty;
    }

}
