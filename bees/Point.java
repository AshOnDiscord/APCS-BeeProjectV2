package bees;

public class Point implements Comparable<Point> {
    public int x;
    public int y;
    public int z;
    public boolean state;

    public Point(int y, int x, int z, boolean state) {
        this.y = y;
        this.x = x;
        this.z = z;
        this.state = state;
    }

    public Point(int y, int x, int z) {
        this(y, x, z, false);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj == null || obj.getClass() != this.getClass()) {
            return false;
        }
        Point point = (Point) obj;
        return point.y == this.y && point.x == this.x && point.z == this.z;
    }

    @Override
    public String toString() {
        return "(" + this.y + ", " + this.x + ", " + this.z + ")";
    }

    public Point fromString(String str) {
        String[] substrings = str.substring(1, str.length() - 1).split(",");

        // Convert substrings to integers
        int[] values = new int[substrings.length];
        for (int i = 0; i < substrings.length; i++) {
            values[i] = Integer.parseInt(substrings[i]);
        }

        return new Point(values[0], values[1], values[2]);
    }

    @Override
    public int compareTo(Point o) {
        // compare y, x, z
        if (this.y != o.y) {
            return this.y - o.y;
        }
        if (this.x != o.x) {
            return this.x - o.x;
        }
        return this.z - o.z;
    }

    @Override
    public int hashCode() {
        int result = this.y;
        result = 31 * result + this.x;
        result = 31 * result + this.z;
        return result;
    }
}