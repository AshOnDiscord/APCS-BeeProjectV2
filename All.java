import java.io.BufferedReader;
import java.io.File;
import java.util.*;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

public class All {
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


class Point implements Comparable<Point> {
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
        result = 5381 * result + this.z;
        return result;
    }
}

class Parser {
    public static Point parsePoint(String line) {
        String[] values = line.split(",");
        if (values.length != 3) {
            System.err.println("Not 3 values");
            return null;
        }
        int[] intValues = new int[values.length];
        for (int i = 0; i < values.length; i++) {
            intValues[i] = Integer.parseInt(values[i]);
        }
        return new Point(intValues[0], intValues[1], intValues[2]);
    }

    public static Data parse(String fileName) {
        File file = new File(fileName);
        BufferedReader br;
        try {
            br = new BufferedReader(new java.io.FileReader(file));
            String line;

            int i = 0;
            int cycle = Integer.parseInt(br.readLine());
            Point size = parsePoint(br.readLine());
            Point[] end = new Point[15];
            Point[] bees = new Point[15];
            int obstaclesCount;
            Point[] obstacles = new Point[0];
            while ((line = br.readLine()) != null) {
                if (i < 15) {
                    end[i] = parsePoint(line);
                } else if (i < 30) {
                    bees[i - 15] = parsePoint(line);
                } else if (i == 30) {
                    obstaclesCount = Integer.parseInt(line);
                    obstacles = new Point[obstaclesCount];
                } else {
                    obstacles[i - 31] = parsePoint(line);
                }
                i++;
            }
            if (obstacles[obstacles.length - 1] == null) {
                System.err.println("Missing obstacles");
                return null;
            }
            return new Data(cycle, size, end, bees, obstacles);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}


class Neighbor {
    public Point point;
    public int distance;

    public Neighbor(Point point, int distance) {
        this.point = point;
        this.distance = distance;
    }
}

class Grid3D {
    public Point[][][] grid;
    public int width;
    public int height;
    public int depth;

    public Grid3D(int width, int height, int depth) {
        this.width = width;
        this.height = height;
        this.depth = depth;
        grid = new Point[width][height][depth];
        for (int y = 0; y < height; y++) {
            Point[][] plane = new Point[width][depth];
            for (int x = 0; x < width; x++) {
                Point[] row = new Point[depth];
                for (int z = 0; z < depth; z++) {
                    row[z] = new Point(y, x, z);
                }
                plane[x] = row;
            }
            grid[y] = plane;
        }
    }

    public Double getHeuristics(Point point, Point[] end) {
        int min = Integer.MAX_VALUE;
        for (Point endPoint : end) {
            int distance = Math.abs(point.x - endPoint.x) + Math.abs(point.y - endPoint.y) + Math.abs(point.z - endPoint.z);
            if (distance < min) {
                min = distance;
            }
        }
        return min * 0.25;
    }

    public LinkedList<Point> getNeighbors(Point point, Point[] end) {
        LinkedList<Point> neighbors = new LinkedList<>();
        neighbors.addAll(this.rookPath(point, 0, 1, 0, end)); // down | +x
        neighbors.addAll(this.rookPath(point, 0, -1, 0, end)); // up | -x
        neighbors.addAll(this.rookPath(point, -1, 0, 0, end)); // left | -y
        neighbors.addAll(this.rookPath(point, 1, 0, 0, end)); // right | +y
        neighbors.addAll(this.bishopPath(point, -1, -1, 0)); // upLeft | -x -y
        neighbors.addAll(this.bishopPath(point, 1, -1, 0)); // upRight | +x -y
        neighbors.addAll(this.bishopPath(point, -1, 1, 0)); // downLeft | -x +y
        neighbors.addAll(this.bishopPath(point, 1, 1, 0)); // downRight | +x +y
        neighbors.addAll(this.rookPath(point, 0, 0, 1, end)); // downZ | +z
        neighbors.addAll(this.rookPath(point, 0, 0, -1, end)); // upZ | -z
        neighbors.addAll(this.bishopPath(point, 0, -1, -1)); // upLeftZ | -y -z
        neighbors.addAll(this.bishopPath(point, 0, 1, -1)); // upRightZ | +y -z
        neighbors.addAll(this.bishopPath(point, 0, -1, 1)); // downLeftZ | -y +z
        neighbors.addAll(this.bishopPath(point, 0, 1, 1)); // downRightZ | +y +z
        neighbors.addAll(this.bishopPath(point, -1, 0, 1)); // downX | -x +z
        neighbors.addAll(this.bishopPath(point, -1, 0, -1)); // upX | -x -z
        neighbors.addAll(this.bishopPath(point, 1, 0, 1)); // downXZ | +x +z
        neighbors.addAll(this.bishopPath(point, 1, 0, -1)); // upXZ | +x -z
        return neighbors;
    }

    public List<Point> rookPath(Point origin, int xDir, int yDir, int zDir, Point[] end) {
        if (xDir == 0 && yDir == 0 && zDir == 0) {
            throw new Error("Invalid direction"); // require at least one direction
        }
        // zy diagonal
        if (xDir != 0 && yDir != 0) {
            throw new Error("Invalid direction");
        }
        // yx diagonal
        if (xDir != 0 && zDir != 0) {
            throw new Error("Invalid direction");
        }
        // zx diagonal
        if (yDir != 0 && zDir != 0) {
            throw new Error("Invalid direction");
        }
        boolean isX = xDir != 0;
        boolean isY = yDir != 0;
        // no isZ because if it's not x or y, it's z
        int dir = isX ? xDir : isY ? yDir : zDir;
        int ob;
        List<Point> important = new ArrayList<>();
        if (dir == 1) {
            ob = isX ? this.width : isY ? this.height : this.depth;
        } else {
            ob = -1;
        }
        int movingStart = isX ? origin.x : isY ? origin.y : origin.z;
        for (int i = movingStart; i != ob; i += dir) {
            Point cell;
            if (isX) {
                cell = this.grid[origin.y][i][origin.z];
            } else if (isY) {
                try {
                    cell = this.grid[i][origin.x][origin.z];
                } catch (ArrayIndexOutOfBoundsException e) {
                    cell = null;
                }
            } else {
                cell = this.grid[i][origin.y][origin.x];
            }
            if (cell.state) {
                break;
            }
            Point[] sides = new Point[4];
            if (isX) {
                try {
                    sides[0] = this.grid[origin.y - 1][i][origin.z];
                } catch (ArrayIndexOutOfBoundsException e) {
                    sides[0] = null;
                }
                try {
                    sides[1] = this.grid[origin.y + 1][i][origin.z];
                } catch (ArrayIndexOutOfBoundsException e) {
                    sides[1] = null;
                }
                try {
                    sides[2] = this.grid[origin.y][i][origin.z - 1];
                } catch (ArrayIndexOutOfBoundsException e) {
                    sides[2] = null;
                }
                try {
                    sides[3] = this.grid[origin.y][i][origin.z + 1];
                } catch (ArrayIndexOutOfBoundsException e) {
                    sides[3] = null;
                }
            } else if (isY) {
                try {
                    sides[0] = this.grid[i][origin.x - 1][origin.z];
                } catch (ArrayIndexOutOfBoundsException e) {
                    sides[0] = null;
                }
                try {
                    sides[1] = this.grid[i][origin.x + 1][origin.z];
                } catch (ArrayIndexOutOfBoundsException e) {
                    sides[1] = null;
                }
                try {
                    sides[2] = this.grid[i][origin.x][origin.z - 1];
                } catch (ArrayIndexOutOfBoundsException e) {
                    sides[2] = null;
                }
                try {
                    sides[3] = this.grid[i][origin.x][origin.z + 1];
                } catch (ArrayIndexOutOfBoundsException e) {
                    sides[3] = null;
                }
            } else {
                try {
                    sides[0] = this.grid[origin.y - 1][origin.x][i];
                } catch (ArrayIndexOutOfBoundsException e) {
                    sides[0] = null;
                }
                try {
                    sides[1] = this.grid[origin.y + 1][origin.x][i];
                } catch (ArrayIndexOutOfBoundsException e) {
                    sides[1] = null;
                }
                try {
                    sides[2] = this.grid[origin.y][origin.x - 1][i];
                } catch (ArrayIndexOutOfBoundsException e) {
                    sides[2] = null;
                }
                try {
                    sides[3] = this.grid[origin.y][origin.x + 1][i];
                } catch (ArrayIndexOutOfBoundsException e) {
                    sides[3] = null;
                }
            }
            // console.log(sides, isX, isY);
            Point finalCell = cell;
            if (Arrays.asList(end).contains(finalCell)) {
                // if atleast one side is open, the cell is important
                for (Point side : sides) {
                    if (side != null && !side.state) {
                        // this.important.push(cell);
                        important.add(cell);
                        break;
                    }
                }
            }
        }
        return important; // non-important cells are pruned
    }

    public List<Point> bishopPath(Point origin, int xDir, int yDir, int zDir) {
        // exactly 2 will not be 0
        if (xDir == 0 && yDir == 0 && zDir == 0) {
            throw new Error("Invalid direction"); // require at least one direction
        }
        if (!(xDir != 0 && yDir != 0) && !(xDir != 0 && zDir != 0) && !(yDir != 0 && zDir != 0)) {
            throw new Error("Invalid direction"); // there is only one direction, use rookPath instead
        }
        int x = origin.x + xDir;
        int y = origin.y + yDir;
        int z = origin.z + zDir;
        List<Point> important = new ArrayList<>();
        while (x >= 0 && x < this.width && y >= 0 && y < this.height && z >= 0 && z < this.depth) {
            Point cell = this.grid[y][x][z];
            if (cell.state) {
                break;
            }
            Point[] intermediate = new Point[2];
            if (xDir != 0 && yDir != 0) {
                // xy diagonal, z is constant
                try {
                    intermediate[0] = this.grid[y - yDir][x][z];
                } catch (ArrayIndexOutOfBoundsException e) {
                    intermediate[0] = null;
                }
                try {
                    intermediate[1] = this.grid[y][x - xDir][z];
                } catch (ArrayIndexOutOfBoundsException e) {
                    intermediate[1] = null;
                }
            } else if (xDir != 0) {
                // xz diagonal, y is constant
                try {
                    intermediate[0] = this.grid[y][x - xDir][z];
                } catch (ArrayIndexOutOfBoundsException e) {
                    intermediate[0] = null;
                }
                try {
                    intermediate[1] = this.grid[y][x][z - zDir];
                } catch (ArrayIndexOutOfBoundsException e) {
                    intermediate[1] = null;
                }
            } else {
                // yz diagonal, x is constant
                try {
                    intermediate[0] = this.grid[y][x][z - zDir];
                } catch (ArrayIndexOutOfBoundsException e) {
                    intermediate[0] = null;
                }
                try {
                    intermediate[1] = this.grid[y - yDir][x][z];
                } catch (ArrayIndexOutOfBoundsException e) {
                    intermediate[1] = null;
                }
            }
            if (!(intermediate[0] != null && !intermediate[0].state) && !(intermediate[1] != null && !intermediate[1].state)) {
                break; // both intermediates are closed
            }
            // there will always be an open side due to the intermediate checks
            important.add(cell);
            x += xDir;
            y += yDir;
            z += zDir;
        }
        return important; // non-important cells are pruned
    }
}


class Data {
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

class AStar {
    public static int calc(Point[] points, Point start, Point[] end, BiFunction<Point, Point[], LinkedList<Point>> getNeighbors, BiFunction<Point, Point[], Double> getHeuristics) {
        // str is the point, int is the heuristic
        Map<Point, Double> unseen = new HashMap<>();
        // int is the distance, string is the point, second string is the previous point
        SortedMap<Integer, HashSet<Point>> minDistances = new TreeMap<>();

        for (Point point : points) {
            unseen.put(point, getHeuristics.apply(point, end));
        }
//        unseen.remove(start.toString());
        HashSet<Point> temp = new HashSet<>();
        temp.add(start);
        minDistances.put(0, temp); // you can't pass a null value to a treemap

//         minDistances.get(minDistances.firstKey());
        Point current = null;
        Integer currentDistance = null;

        unseenLoop:
        while (!unseen.isEmpty()) {
            // find first entry in minDistances that is in unseen
            current = null;
            getCurrent:
            for (Integer distance : minDistances.keySet()) {
                HashSet<Point> pointsSet = minDistances.get(distance);
                for (Point point : pointsSet) {
                    if (unseen.containsKey(point)) {
                        current = point;
                        currentDistance = distance;
                        unseen.remove(point);
                        break getCurrent;
                    }
                }
            }
            if (current == null) {
                System.err.println("No current");
                break;
            }

            for (Point endPoint : end) {
                if (current.equals(endPoint)) {
                    break unseenLoop;
                }
            }

            LinkedList<Point> neighbors = getNeighbors.apply(current, end);
            for (Point neighbor : neighbors) {
                // see if the neighbor already has a distance in minDistances
                Integer prevSize = null;
                for (int dist : minDistances.keySet()) {
                    HashSet<Point> subMap = minDistances.get(dist);
                    if (subMap.contains(neighbor)) {
                        prevSize = dist;
                        break;
                    }
                }
                int newDist = currentDistance + 1;
                if (prevSize != null) {
                    // prev exists
                    if (newDist < prevSize) {
                        minDistances.get(prevSize).remove(neighbor);
                        if (minDistances.containsKey(newDist)) {
                            minDistances.get(newDist).add(neighbor);
                        } else {
                            temp = new HashSet<>();
                            temp.add(neighbor);
                            minDistances.put(newDist, temp);
                        }
                    }
                } else {
                    if (minDistances.containsKey(newDist)) {
                        minDistances.get(newDist).add(neighbor);
                    } else {
                        temp = new HashSet<>();
                        temp.add(neighbor);
                        minDistances.put(newDist, temp);
                    }
                }
            }
        }
        assert current != null;
        System.out.println("Done: " + current + " | " + currentDistance);
        return currentDistance;
    }
}
