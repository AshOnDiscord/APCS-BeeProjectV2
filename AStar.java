import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.function.BiFunction;

public class AStar {
    public static void calc(Point[] points, Point start, Point[] end, BiFunction<Point, Point[], Neighbor[]> getNeighbors, BiFunction<Point, Point[], Double> getHeuristics) {
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

            Neighbor[] neighbors = getNeighbors.apply(current, end);
            for (Neighbor neighbor : neighbors) {
                // see if the neighbor already has a distance in minDistances
                Integer prevSize = null;
                for (int dist : minDistances.keySet()) {
                    SortedMap<Point, Point> subMap = minDistances.get(dist);
                    if (subMap.containsKey(neighbor.point)) {
                        prevSize = dist;
                        break;
                    }
                }
                int newDist = currentDistance + neighbor.distance;
                if (prevSize != null) {
                    // prev exists
                    if (newDist < prevSize) {
                        minDistances.get(prevSize).remove(neighbor.point);
                        if (minDistances.containsKey(newDist)) {
                            minDistances.get(newDist).put(neighbor.point, current);
                        } else {
                            minDistances.put(newDist, new TreeMap<>(Map.of(neighbor.point, current)));
                        }
                    }
                } else {
                    if (minDistances.containsKey(newDist)) {
                        minDistances.get(newDist).put(neighbor.point, current);
                    } else {
                        minDistances.put(newDist, new TreeMap<>(Map.of(neighbor.point, current)));
                    }
                }
            }
        }
        assert current != null;
        System.out.println("Done: " + current + " | " + currentDistance);
    }
}