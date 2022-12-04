
package myagent;

import vacworld.Direction;

// Represents positions in map 
public class PosVector {
    // Unit direction vectors
    public static final PosVector NORTH = new PosVector(0, -1);
    public static final PosVector EAST = new PosVector(1, 0);
    public static final PosVector SOUTH = new PosVector(0, 1);
    public static final PosVector WEST = new PosVector(-1, 0);

    private final int x;
    private final int y;

    // Constructors
    public PosVector() {
        this.x = 0;
        this.y = 0;
    }

    public PosVector(int x, int y) {
        this.x = x;
        this.y = y;
    }

    // Getters
    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null)
            return false;
        if (!(o instanceof PosVector))
            return false;
        PosVector newO = (PosVector) o;
        return this.x == newO.getX() && this.y == newO.getY();
    }

    @Override
    public int hashCode() {
        return x ^ y;
    }

    @Override
    public String toString() {
        return String.format("Vector2: (%d, %d)", x, y);
    }

    // Converts a direction number to direction vector
    public static PosVector dirToVec(int direction) {
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

    // Addition of two PosVector objs
    public static PosVector addition(PosVector a, PosVector b) {
        return new PosVector(a.x + b.x, a.y + b.y);
    }

    // Subtraction of two PosVector objs
    public static PosVector subtraction(PosVector a, PosVector b) {
        return new PosVector(a.x - b.x, a.y - b.y);
    }

    // Dot product of two PosVector objs
    public static int dotP(PosVector a, PosVector b) {
        return a.x * b.x + a.y * b.y;
    }

    // Magnitude of two PosVector objs
    public double magnitude() {
        return Math.sqrt(x * x + y * y);
    }

    // Angle of two PosVector objs
    public static double angle(PosVector a, PosVector b) {
        double dot = PosVector.dotP(a, b);
        double magnitude = a.magnitude() * b.magnitude();
        return Math.acos(dot / magnitude);
    }

    // Converts a direction vector to direction number
    public static int vecToDir(PosVector direction) {
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
