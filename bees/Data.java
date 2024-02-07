package bees;

public class Data {
    public int cycle;
    public Point size;
    public Point[] end;
    public Point[] bees;
    public Point[] obstacles;

    public Data(int cycle, Point size, Point[] end, Point[] bees, Point[] obstacles) {
        this.cycle = cycle;
        this.size = size;
        this.end = end;
        this.bees = bees;
        this.obstacles = obstacles;
    }
}
