/**
 * Author:      Daniel Phang
 * Class:       CSE431 Intelligent Agents
 * Professor:   Jeff Heflin
 * Date:        September 19, 2013
 * Assignment:  Program #1
 * Environment: Java 1.6
 */

package myagent;

import vacworld.Direction;


public class Vector2 {
    /*
     * Unit direction vectors. Based on the orientation described in the
     * Direction class.
     */
    public static final Vector2 NORTH = new Vector2(0, -1);
    public static final Vector2 EAST = new Vector2(1, 0);
    public static final Vector2 SOUTH = new Vector2(0, 1);
    public static final Vector2 WEST = new Vector2(-1, 0);

    private final int x;
    private final int y;

    /**
     * Construct that initializes x and y to 0 (i.e the zero vector).
     */
    public Vector2() {
        this.x = 0;
        this.y = 0;
    }

    /**
     * Construct that initializes x and y to the specified values.
     * 
     * @param x
     * @param y
     */
    public Vector2(int x, int y) {
        this.x = x;
        this.y = y;
    }

    /**
     * @return x
     */
    public int getX() {
        return x;
    }

    /**
     * @return y
     */
    public int getY() {
        return y;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null)
            return false;
        if (!(o instanceof Vector2))
            return false;
        Vector2 newO = (Vector2) o;
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

    /**
     * Converts a direction number to the appropriate direction vector.
     * 
     * @param direction
     *            a direction number (based on the Direction class).
     * @return the corresponding direction vector.
     */
    public static Vector2 directionToVector(int direction) {
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

    /**
     * Subtracts two Vector2 objects and returns a new Vector2.
     * 
     * @param a
     * @param b
     * @return the vector subtraction of a - b.
     */
    public static Vector2 sub(Vector2 a, Vector2 b) {
        return new Vector2(a.x - b.x, a.y - b.y);
    }

    /**
     * Computes the dot product of two Vector2 objects and returns a new
     * Vector2.
     * 
     * @param a
     * @param b
     * @return the dot product of a and b.
     */
    public static int dot(Vector2 a, Vector2 b) {
        return a.x * b.x + a.y * b.y;
    }

    /**
     * Computes the magnitude of a Vector2 object.
     * 
     * @return the magnitude of this Vector2 object.
     */
    public double mag() {
        return Math.sqrt(x * x + y * y);
    }

    /**
     * Determines the angle between two Vector2 objects.
     * 
     * @param a
     * @param b
     * @return the angle between a and b.
     */
    public static double angle(Vector2 a, Vector2 b) {
        double dot = Vector2.dot(a, b);
        double mag = a.mag() * b.mag();
        return Math.acos(dot / mag);
    }

    /**
     * Converts a direction vector to the appropriate direction number.
     * 
     * @param direction
     *            a direction vector
     * @return the corresponding direction number (based on the Direction
     *         class).
     */
    public static int vectorToDirection(Vector2 direction) {
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

    /**
     * Adds two vectors and returns a new Vector2.
     * 
     * @param a
     * @param b
     * @return the vector addition a + b.
     */
    public static Vector2 add(Vector2 a, Vector2 b) {
        return new Vector2(a.x + b.x, a.y + b.y);
    }
}
