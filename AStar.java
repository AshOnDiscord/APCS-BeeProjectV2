import java.util.*;
import java.util.function.BiFunction;

public class AStar {
    public static int calc(Point[] points, Point start, Point[] end, BiFunction<Point, Point[], LinkedList<Point>> getNeighbors, BiFunction<Point, Point[], Double> getHeuristics) {
        // str is the point, int is the heuristic
        Map<Point, Double> unseen = new HashMap<>();
        // int is the distance, string is the point, second string is the previous point
        SortedMap<Integer, SortedMap<Point, Point>> minDistances = new TreeMap<>();

        for (Point point : points) {
            unseen.put(point, getHeuristics.apply(point, end));
        }
//        unseen.remove(start.toString());
        minDistances.put(0, new TreeMap<>(Map.of(start, new Point(-1, -1, -1)))); // you can't pass a null value to a treemap

//         minDistances.get(minDistances.firstKey());
        SortedMap<Point, Point> pointsMap = minDistances.firstEntry().getValue();
        Point current = pointsMap.firstKey();
        Integer currentDistance = null;

        unseenLoop:
        while (!unseen.isEmpty()) {
            // find first entry in minDistances that is in unseen
            current = null;
            getCurrent:
            for (Integer distance : minDistances.keySet()) {
                SortedMap<Point, Point> pointsMap2 = minDistances.get(distance);
                for (Point point : pointsMap2.keySet()) {
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
                    SortedMap<Point, Point> subMap = minDistances.get(dist);
                    if (subMap.containsKey(neighbor)) {
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
                            minDistances.get(newDist).put(neighbor, current);
                        } else {
                            minDistances.put(newDist, new TreeMap<>(Map.of(neighbor, current)));
                        }
                    }
                } else {
                    if (minDistances.containsKey(newDist)) {
                        minDistances.get(newDist).put(neighbor, current);
                    } else {
                        minDistances.put(newDist, new TreeMap<>(Map.of(neighbor, current)));
                    }
                }
            }
        }
        assert current != null;
        System.out.println("Done: " + current + " | " + currentDistance);
        return currentDistance;
    }
}