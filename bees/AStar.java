package bees;

import java.util.*;
import java.util.function.BiFunction;

public class AStar {
    public static int calc(Point[] points, Point start, Point[] end, BiFunction<Point, Point[], LinkedList<Point>> getNeighbors, BiFunction<Point, Point[], Double> getHeuristics) {
        // str is the point, int is the heuristic
        HashMap<Point, Double> unseen = new HashMap<>();
        // int is the distance, string is the point, second string is the previous point
        SortedMap<Integer, HashMap<Point, String>> minDistances = new TreeMap<>();

        for (Point point : points) {
            unseen.put(point, getHeuristics.apply(point, end));
        }
//        unseen.remove(start.toString());
        HashMap<Point, String> temp = new HashMap<>();
        temp.put(start, null);
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
                HashMap<Point, String> pointsMap2 = minDistances.get(distance);
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
                    HashMap<Point, String> subMap = minDistances.get(dist);
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
                            minDistances.get(newDist).put(neighbor, null);
                        } else {
                            HashMap<Point, String> map = new HashMap<>();
                            map.put(neighbor, null);
                            minDistances.put(newDist, map);
                        }
                    }
                } else {
                    if (minDistances.containsKey(newDist)) {
                        minDistances.get(newDist).put(neighbor, null);
                    } else {
                        HashMap<Point, String> map = new HashMap<>();
                        map.put(neighbor, null);
                        minDistances.put(newDist, map);
                    }
                }
            }
        }
        assert current != null;
        System.out.println("Done: " + current + " | " + currentDistance);
        return currentDistance;
    }
}
