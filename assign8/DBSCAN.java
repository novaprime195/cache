import java.io.*;
import java.util.*;
import java.lang.Math;

class Point {
    String name;
    double[] coords;
    Point(String name, double[] coords) {
        this.name = name;
        this.coords = coords;
    }
}

public class DBSCAN {
    public static double distance(double[] a, double[] b) {
        double sum = 0.0;
        for (int i = 0; i < a.length; i++)
            sum += Math.pow(a[i] - b[i], 2);
        return Math.sqrt(sum);
    }

    public static List<Point> readCSV(String filename) throws IOException {
        List<Point> points = new ArrayList<>();
        BufferedReader br = new BufferedReader(new FileReader(filename));
        String line;
        boolean headerSkipped = false;

        while ((line = br.readLine()) != null) {
            if (line.trim().isEmpty()) continue;
            String[] vals = line.split(",");
            if (!headerSkipped && vals[0].toLowerCase().contains("point")) {
                headerSkipped = true;
                continue;
            }
            try {
                String name = vals[0].trim();
                double[] coords = new double[vals.length - 1];
                for (int i = 1; i < vals.length; i++)
                    coords[i - 1] = Double.parseDouble(vals[i]);
                points.add(new Point(name, coords));
            } catch (Exception e) {
                continue;
            }
        }
        br.close();
        return points;
    }

    public static double[][] computeDistanceMatrix(List<Point> points) {
        int n = points.size();
        double[][] matrix = new double[n][n];

        System.out.print("\nDistance Matrix:\n          ");
        for (Point p : points)
            System.out.printf("%-10s", p.name);
        System.out.println();

        for (int i = 0; i < n; i++) {
            System.out.printf("%-10s", points.get(i).name);
            for (int j = 0; j < n; j++) {
                if (i == j) matrix[i][j] = 0.0;
                else if (i < j) matrix[i][j] = matrix[j][i] = distance(points.get(i).coords, points.get(j).coords);
                System.out.printf("%-10.2f", matrix[i][j]);
            }
            System.out.println();
        }
        return matrix;
    }

    public static List<Integer> regionQuery(List<Point> points, Point point, double eps) {
        List<Integer> neighbors = new ArrayList<>();
        for (int i = 0; i < points.size(); i++) {
            if (distance(points.get(i).coords, point.coords) <= eps)
                neighbors.add(i);
        }
        return neighbors;
    }

    public static void expandCluster(List<Point> points, int[] clusters, int clusterId,
                                     int pointIdx, List<Integer> neighbors, double eps, int minPts) {
        clusters[pointIdx] = clusterId;
        Queue<Integer> queue = new LinkedList<>(neighbors);

        while (!queue.isEmpty()) {
            int qIdx = queue.poll();
            if (clusters[qIdx] == 0) {
                clusters[qIdx] = clusterId;
                List<Integer> qNeighbors = regionQuery(points, points.get(qIdx), eps);
                if (qNeighbors.size() >= minPts)
                    queue.addAll(qNeighbors);
            }
            if (clusters[qIdx] == -1)
                clusters[qIdx] = clusterId;
        }
    }

    public static Map<Integer, List<Point>> dbscan(List<Point> points, double eps, int minPts) {
        int n = points.size();
        int[] clusters = new int[n];
        int clusterId = 0;

        for (int i = 0; i < n; i++) {
            if (clusters[i] != 0) continue;

            List<Integer> neighbors = regionQuery(points, points.get(i), eps);
            if (neighbors.size() < minPts)
                clusters[i] = -1;
            else {
                clusterId++;
                expandCluster(points, clusters, clusterId, i, neighbors, eps, minPts);
            }
        }

        Map<Integer, List<Point>> clusterMap = new LinkedHashMap<>();
        for (int i = 0; i < n; i++) {
            if (clusters[i] > 0) {
                clusterMap.computeIfAbsent(clusters[i], k -> new ArrayList<>()).add(points.get(i));
            }
        }

        System.out.println("\nResults:");
        int total = points.size();
        for (int id : clusterMap.keySet()) {
            List<Point> cluster = clusterMap.get(id);
            double percent = (cluster.size() * 100.0) / total;
            System.out.printf("Cluster %d: %d points (%.2f%%) → ", id, cluster.size(), percent);
            for (Point p : cluster) System.out.print(p.name + " ");
            System.out.println();
        }

        List<String> noise = new ArrayList<>();
        for (int i = 0; i < n; i++)
            if (clusters[i] == -1)
                noise.add(points.get(i).name);

        if (!noise.isEmpty()) {
            double percent = (noise.size() * 100.0) / total;
            System.out.printf("Noise points: %d (%.2f%%) → %s\n", noise.size(), percent, noise);
        }

        System.out.println("\nCentroids:");
        for (int id : clusterMap.keySet()) {
            double[] mean = centroid(clusterMap.get(id));
            System.out.printf("Centroid %d: [%.2f, %.2f]\n", id, mean[0], mean[1]);
        }

        return clusterMap;
    }

    public static double[] centroid(List<Point> cluster) {
        int dims = cluster.get(0).coords.length;
        double[] mean = new double[dims];
        for (Point p : cluster)
            for (int i = 0; i < dims; i++)
                mean[i] += p.coords[i];
        for (int i = 0; i < dims; i++)
            mean[i] /= cluster.size();
        return mean;
    }

    public static void main(String[] args) throws IOException {
        Scanner sc = new Scanner(System.in);
        System.out.print("Enter CSV filename: ");
        String filename = sc.nextLine();

        List<Point> points = readCSV(filename);
        computeDistanceMatrix(points);

        System.out.print("\nEnter epsilon: ");
        double eps = sc.nextDouble();
        System.out.print("Enter minPts: ");
        int minPts = sc.nextInt();

        dbscan(points, eps, minPts);
    }
}
