public class Point {
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
}