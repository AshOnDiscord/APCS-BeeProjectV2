import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.function.BiFunction;

public class AStar {
    public static void calc(Point[] points, Point start, Point[] end, BiFunction<String, Point[], Neighbor[]> getNeighbors, BiFunction<String, Point[], Double> getHeuristics) {
        // str is the point, int is the heuristic
        Map<String, Double> unseen = new HashMap<>();
        // int is the distance, string is the point, second string is the previous point
        SortedMap<Integer, SortedMap<String, String>> minDistances = new TreeMap<>();

        for (Point point : points) {
            unseen.put(point.toString(), getHeuristics.apply(point.toString(), end));
        }
//        unseen.remove(start.toString());
        minDistances.put(0, new TreeMap<>(Map.of(start.toString(), ""))); // you can't pass a null value to a treemap

//         minDistances.get(minDistances.firstKey());
        SortedMap<String, String> pointsMap = minDistances.firstEntry().getValue();
        String current = pointsMap.firstKey();
        Integer currentDistance = null;

        unseenLoop:
        while (!unseen.isEmpty()) {
            // find first entry in minDistances that is in unseen
            current = null;
            getCurrent:
            for (Integer distance : minDistances.keySet()) {
                SortedMap<String, String> pointsMap2 = minDistances.get(distance);
                for (String point : pointsMap2.keySet()) {
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
                if (current.equals(endPoint.toString())) {
                    break unseenLoop;
                }
            }

            Neighbor[] neighbors = getNeighbors.apply(current, end);
            for (Neighbor neighbor : neighbors) {
                String neighborStr = neighbor.point.toString();
                // see if the neighbor already has a distance in minDistances
                Integer prevSize = null;
                for (int dist : minDistances.keySet()) {
                    SortedMap<String, String> subMap = minDistances.get(dist);
                    if (subMap.containsKey(neighborStr)) {
                        prevSize = dist;
                        break;
                    }
                }
                int newDist = currentDistance + neighbor.distance;
                if (prevSize != null) {
                    // prev exists
                    if (newDist < prevSize) {
                        minDistances.get(prevSize).remove(neighborStr);
                        if (minDistances.containsKey(newDist)) {
                            minDistances.get(newDist).put(neighborStr, current);
                        } else {
                            minDistances.put(newDist, new TreeMap<>(Map.of(neighborStr, current)));
                        }
                    }
                } else {
                    if (minDistances.containsKey(newDist)) {
                        minDistances.get(newDist).put(neighborStr, current);
                    } else {
                        minDistances.put(newDist, new TreeMap<>(Map.of(neighborStr, current)));
                    }
                }
            }
        }
        assert current != null;
        System.out.println("Done: " + current + " | " + currentDistance);
    }
}