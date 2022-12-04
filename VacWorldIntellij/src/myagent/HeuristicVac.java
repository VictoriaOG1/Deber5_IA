
package myagent;


public class HeuristicVac {

    // Estimates the cost in terms of turning
    public static int turningCost(Vector2 i, Vector2 e, int d)
    {
        Vector2 cDirectionV = Vector2.directionToVector(d);
        Vector2 nDirectionV = Vector2.sub(e, i);

        double ag = Vector2
                .angle(cDirectionV, nDirectionV);

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

    //Manhattan distance between two points
    public static int manhattanDistance(Vector2 i, Vector2 e)
    {
        int dx = i.getX() - e.getX();
        int dy = i.getY() - e.getY();

        return Math.abs(dx) + Math.abs(dy);
    }


    // Heuristic to estimate the cost between the two given positions
    public static int getCost(Vector2 i, Vector2 e, int d)
    {
        int mCost = manhattanDistance(i, e) * , 2;
        int tCost = turningCost(i, e, d);

        return mCost + tCost;
    }
}
