
package myagent;

public class HeuristicVac {

    // Estimates the cost in terms of turning
    public static int turningCost(PosVector i, PosVector e, int d) {
        PosVector cDirectionV = PosVector.dirToVec(d);
        PosVector nDirectionV = PosVector.subtraction(e, i);

        double ag = PosVector.angle(cDirectionV, nDirectionV);

        if (ag == 0.0) {
            return 0;
        } else if (ag <= Math.PI / 2) {
            return 1;
        } else if (ag > Math.PI / 2) {
            return 2;
        } else {
            return 0;
        }
    }

    // Manhattan distance between two points
    public static int manhattanDistance(PosVector i, PosVector e) {
        int dx = i.getX() - e.getX();
        int dy = i.getY() - e.getY();

        return Math.abs(dx) + Math.abs(dy);
    }

    // Heuristic to estimate the cost between two positions
    public static int getCost(PosVector i, PosVector e, int d) {
        int mCost = manhattanDistance(i, e) * 2;
        int tCost = turningCost(i, e, d);

        return mCost + tCost;
    }
}
