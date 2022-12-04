package improve;

public class Heuristic {

    // Calculates ManhattanDistance
    public static int Mdistance(UnitVector init, UnitVector end) {
        int dx = init.getX() - end.getX();
        int dy = init.getY() - end.getY();

        return Math.abs(dx) + Math.abs(dy);
    }

    // Estimates the cost in terms of turnCost
    public static int turnC(UnitVector init, UnitVector end, int direction) {
        UnitVector currentD = UnitVector.dirToVec(direction);
        UnitVector newD = UnitVector.subtract(end, init);

        double angle = UnitVector.angle(currentD, newD);

        if (angle == 0.0) {
            return 0;
        } else if (angle <= Math.PI / 2) {
            return 1;
        } else if (angle > Math.PI / 2) {
            return 2;
        } else {
            return 0;
        }
    }

    // Estimates the cost given two positions
    public static int estimateC(UnitVector init, UnitVector end, int direction) {
        int moveCost = Mdistance(init, end) * 2;
        int turnCost = turnC(init, end, direction);

        return moveCost + turnCost;
    }

}
