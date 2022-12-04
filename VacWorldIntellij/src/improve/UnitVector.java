package improve;

import javax.security.auth.x500.X500Principal;

import vacworld.Direction;

public class UnitVector {
    public static final UnitVector NORTH = new UnitVector(0, -1);
    public static final UnitVector EAST = new UnitVector(1, 0);
    public static final UnitVector SOUTH = new UnitVector(0, 1);
    public static final UnitVector WEST = new UnitVector(-1, 0);

    public final int x;
    public final int y;

    public UnitVector() {
        this.x = 0;
        this.y = 0;
    }

    public UnitVector(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;
        if (!(obj instanceof UnitVector))
            return false;
        UnitVector auxO = (UnitVector) obj;
        return this.x == auxO.getX() && this.y == auxO.getY();
    }

    @Override
    public int hashCode() {
        return x ^ y;
    }

    @Override
    public String toString() {
        return String.format("Direction Vectors: (%d, %d)", x, y);
    }

    public static UnitVector subtract(UnitVector a, UnitVector b) {
        return new UnitVector(a.x - b.x, a.y - b.y);
    }

    public static int dotProduct(UnitVector a, UnitVector b) {
        return a.x * b.x + a.y * b.y;
    }

    public double magnitude() {
        return Math.sqrt(x * x + y * y);
    }

    public static double angle(UnitVector a, UnitVector b) {
        double dot = UnitVector.dotProduct(a, b);
        double mag = a.magnitude() * b.magnitude();
        return Math.acos(dot / mag);
    }

    public static UnitVector addition(UnitVector a, UnitVector b) {
        return new UnitVector(a.x + b.x, a.y + b.y);
    }

    public static UnitVector dirVector(int direction) {
        if (direction == Direction.NORTH) {
            return NORTH;
        } else if (direction == Direction.WEST) {
            return WEST;
        } else if (direction == Direction.SOUTH) {
            return SOUTH;
        } else if (direction == Direction.EAST) {
            return EAST;
        } else {
            return null;
        }
    }

    public static int vecDirection(UnitVector direction) {
        if (direction.equals(NORTH)) {
            return Direction.NORTH;
        } else if (direction.equals(WEST)) {
            return Direction.WEST;
        } else if (direction.equals(SOUTH)) {
            return Direction.SOUTH;
        } else if (direction.equals(EAST)) {
            return Direction.EAST;
        } else {
            return -1;
        }
    }

}