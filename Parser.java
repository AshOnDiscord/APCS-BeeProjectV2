import java.io.BufferedReader;
import java.io.File;

public class Parser {
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
        BufferedReader br = null;
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
