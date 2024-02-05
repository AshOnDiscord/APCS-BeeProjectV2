import java.util.*;
import java.util.stream.Collectors;

public class Main {
    public static void main(String[] args) {
        Data data = Parser.parse("./data.txt");
        assert data != null;
        System.out.println("Cycle: " + data.cycle);
        System.out.println("Size: " + data.size);
        System.out.println("End: " + Arrays.stream(data.end).map(Point::toString).collect(Collectors.joining(", ")));
        System.out.println("Bees: " + Arrays.stream(data.bees).map(Point::toString).collect(Collectors.joining(", ")));
        System.out.println("Obstacles: " + data.obstacles.length);

        Grid3D grid = new Grid3D(data.size.x, data.size.y, data.size.z);
        for (Point obstacle : data.obstacles) {
            grid.grid[obstacle.y][obstacle.x][obstacle.z].state = true;
        }

        Point[] flattenedGrid = (Arrays.stream(grid.grid)
                .flatMap(Arrays::stream)
                .flatMap(Arrays::stream)
                .toList()).toArray(new Point[0]);

        int sum = 0;

        for (Point bee : data.bees) {
            sum += AStar.calc(flattenedGrid, bee, data.end, grid::getNeighbors, grid::getHeuristics);
        }

        System.out.println("Sum: " + sum);
    }
}


