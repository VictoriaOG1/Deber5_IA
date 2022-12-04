package improve;

public class Heuristic {

    // ManhattanDistance
    public static int Mdistance(UnitVector init, UnitVector end) {
        int dx = init.getX() - end.getX();
        int dy = init.getY() - end.getY();

        return Math.abs(dx) + Math.abs(dy);
    }

    // turnCost
    public static int turnC(UnitVector init, UnitVector end, int direction) {
        UnitVector currentDirectionVector = UnitVector.dirVector(direction);
        UnitVector newDirectionVector = UnitVector.subtract(end, init);

        double angle = UnitVector
                .angle(currentDirectionVector, newDirectionVector);

        if (angle == 0.0) {
            return 0;
        } else if (angle <= Math.PI / 2) {
            return 1;
        } else if (angle > Math.PI / 2) {
            return 2;
        } else { // Covers NaN case
            return 0;
        }
    }

    // estimatesCost
    public static int estimateC(UnitVector init, UnitVector end, int direction) {
        int moveCost = Mdistance(init, end) * 2;
        int turnCost = turnC(init, end, direction);

        return moveCost + turnCost;
    }

}
