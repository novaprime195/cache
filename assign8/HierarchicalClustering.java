import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class HierarchicalClustering {

    static class Point {
        String name;
        double[] coords;

        Point(String name, double[] coords) {
            this.name = name;
            this.coords = coords;
        }
    }

    static double distance(Point a, Point b) {
        double sum = 0;
        for (int i = 0; i < a.coords.length; i++) {
            double diff = a.coords[i] - b.coords[i];
            sum += diff * diff;
        }
        return Math.sqrt(sum);
    }

    static double[] centroid(List<Point> cluster) {
        int dims = cluster.get(0).coords.length;
        double[] mean = new double[dims];
        for (Point p : cluster) {
            for (int i = 0; i < dims; i++) {
                mean[i] += p.coords[i];
            }
        }
        for (int i = 0; i < dims; i++) {
            mean[i] /= cluster.size();
        }
        return mean;
    }

    static List<Point> readCSV(String filename) {
        List<Point> points = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            boolean headerSkipped = false;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;
                String[] vals = line.split(",");

                // Skip header
                if (!headerSkipped && Arrays.stream(vals).anyMatch(v -> v.toLowerCase().contains("id"))) {
                    headerSkipped = true;
                    continue;
                }

                try {
                    String name = vals[0];
                    double[] coords = new double[vals.length - 1];
                    for (int i = 1; i < vals.length; i++) {
                        coords[i - 1] = Double.parseDouble(vals[i]);
                    }
                    points.add(new Point(name, coords));
                } catch (Exception e) {
                    // skip invalid rows
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return points;
    }

    static double linkageDistance(List<Point> A, List<Point> B, String method) {
        double dist;
        switch (method) {
            case "single":
                dist = Double.MAX_VALUE;
                for (Point a : A)
                    for (Point b : B)
                        dist = Math.min(dist, distance(a, b));
                return dist;

            case "complete":
                dist = Double.MIN_VALUE;
                for (Point a : A)
                    for (Point b : B)
                        dist = Math.max(dist, distance(a, b));
                return dist;

            default: // average
                double total = 0;
                int count = 0;
                for (Point a : A)
                    for (Point b : B) {
                        total += distance(a, b);
                        count++;
                    }
                return total / count;
        }
    }

    static void printDistanceMatrix(List<List<Point>> clusters, String method) {
        int n = clusters.size();
        double[][] matrix = new double[n][n];
        List<String> labels = clusters.stream()
                .map(cl -> cl.stream().map(p -> p.name).collect(Collectors.joining(",")))
                .collect(Collectors.toList());

        for (int i = 0; i < n; i++) {
            for (int j = i + 1; j < n; j++) {
                double d = linkageDistance(clusters.get(i), clusters.get(j), method);
                matrix[i][j] = matrix[j][i] = d;
            }
        }

        System.out.println("\nDistance Matrix:");
        System.out.print("            ");
        for (String lbl : labels) System.out.printf("%-15s", lbl);
        System.out.println();
        for (int i = 0; i < n; i++) {
            System.out.printf("%-12s", labels.get(i));
            for (int j = 0; j < n; j++) {
                System.out.printf("%-15.2f", matrix[i][j]);
            }
            System.out.println();
        }
    }

    static void printClusters(List<List<Point>> clusters) {
        System.out.println("\nCurrent Clusters and Centroids:");
        int i = 1;
        for (List<Point> cluster : clusters) {
            List<String> names = cluster.stream().map(p -> p.name).collect(Collectors.toList());
            double[] c = centroid(cluster);
            System.out.printf("Cluster %d: %d points %s, Centroid = [", i++, cluster.size(), names);
            for (int j = 0; j < c.length; j++) {
                System.out.printf("%.2f", c[j]);
                if (j < c.length - 1) System.out.print(", ");
            }
            System.out.println("]");
        }
    }

    static void hierarchicalCluster(List<Point> points, String method) {
        List<List<Point>> clusters = new ArrayList<>();
        for (Point p : points)
            clusters.add(new ArrayList<>(List.of(p)));

        int iteration = 1;
        while (clusters.size() > 1) {
            System.out.println("\n--- Iteration " + iteration + " ---");
            printClusters(clusters);
            printDistanceMatrix(clusters, method);

            double minDist = Double.MAX_VALUE;
            int mergeA = -1, mergeB = -1;

            for (int i = 0; i < clusters.size(); i++) {
                for (int j = i + 1; j < clusters.size(); j++) {
                    double d = linkageDistance(clusters.get(i), clusters.get(j), method);
                    if (d < minDist) {
                        minDist = d;
                        mergeA = i;
                        mergeB = j;
                    }
                }
            }

            System.out.printf("\nMerging clusters %d and %d (distance=%.2f)\n", mergeA + 1, mergeB + 1, minDist);
            clusters.get(mergeA).addAll(clusters.get(mergeB));
            clusters.remove(mergeB);
            iteration++;
        }

        System.out.println("\n--- Final Cluster ---");
        List<Point> finalCluster = clusters.get(0);
        List<String> finalNames = finalCluster.stream().map(p -> p.name).collect(Collectors.toList());
        double[] finalCentroid = centroid(finalCluster);
        System.out.println("Final Cluster: " + finalNames);
        System.out.print("Centroid: [");
        for (int i = 0; i < finalCentroid.length; i++) {
            System.out.printf("%.2f", finalCentroid[i]);
            if (i < finalCentroid.length - 1) System.out.print(", ");
        }
        System.out.println("]");
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        System.out.print("Enter the CSV filename: ");
        String filename = sc.nextLine();
        List<Point> allPoints = readCSV(filename);

        System.out.print("Enter column indices to use (excluding name column): ");
        String[] colStr = sc.nextLine().trim().split("\\s+");
        int[] cols = Arrays.stream(colStr).mapToInt(Integer::parseInt).toArray();

        List<Point> points = new ArrayList<>();
        for (Point p : allPoints) {
            double[] selected = new double[cols.length];
            for (int i = 0; i < cols.length; i++) {
                selected[i] = p.coords[cols[i]];
            }
            points.add(new Point(p.name, selected));
        }

        System.out.print("Enter linkage method (single / average / complete): ");
        String method = sc.nextLine().trim().toLowerCase();

        hierarchicalCluster(points, method);
    }
}
