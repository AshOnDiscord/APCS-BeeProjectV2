import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class Grid3D {
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
