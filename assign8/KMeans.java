import java.io.*;
import java.util.*;

public class KMeans {

    static double distance(double[] a, double[] b) {
        double sum = 0;
        for (int i = 0; i < a.length; i++)
            sum += Math.pow(a[i] - b[i], 2);
        return Math.sqrt(sum);
    }

    static List<double[]> readCSV(String filename) throws IOException {
        List<double[]> data = new ArrayList<>();
        BufferedReader br = new BufferedReader(new FileReader(filename));
        String line;
        while ((line = br.readLine()) != null) {
            String[] parts = line.trim().split(",");
            List<Double> nums = new ArrayList<>();
            for (String p : parts) {
                try {
                    nums.add(Double.parseDouble(p));
                } catch (NumberFormatException ignored) {}
            }
            if (!nums.isEmpty()) {
                double[] row = new double[nums.size()];
                for (int i = 0; i < nums.size(); i++) row[i] = nums.get(i);
                data.add(row);
            }
        }
        br.close();
        return data;
    }

    static double[][] copy(double[][] src) {
        double[][] dest = new double[src.length][src[0].length];
        for (int i = 0; i < src.length; i++)
            dest[i] = Arrays.copyOf(src[i], src[i].length);
        return dest;
    }

    public static void main(String[] args) throws IOException {
        Scanner sc = new Scanner(System.in);
        System.out.print("Enter CSV filename: ");
        String filename = sc.nextLine();

        List<double[]> data = readCSV(filename);
        if (data.isEmpty()) {
            System.out.println("No valid data found in file!");
            return;
        }

        System.out.print("Enter column indices (space separated, 0-based): ");
        String[] colInput = sc.nextLine().trim().split("\\s+");
        int[] cols = Arrays.stream(colInput).mapToInt(Integer::parseInt).toArray();

        List<double[]> points = new ArrayList<>();
        for (double[] row : data) {
            double[] selected = new double[cols.length];
            for (int i = 0; i < cols.length; i++)
                selected[i] = row[cols[i]];
            points.add(selected);
        }

        System.out.print("Enter number of clusters (k): ");
        int k = sc.nextInt();

        if (k <= 0 || k > points.size()) {
            System.out.println("Invalid number of clusters.");
            return;
        }

        Random rand = new Random();
        List<double[]> centroids = new ArrayList<>(points.subList(0, k));
        int[] labels = new int[points.size()];
        Arrays.fill(labels, -1);

        boolean changed = true;
        int iteration = 0, maxIter = 100;

        while (changed && iteration < maxIter) {
            changed = false;
            iteration++;

            for (int i = 0; i < points.size(); i++) {
                double minDist = Double.MAX_VALUE;
                int cluster = -1;
                for (int c = 0; c < k; c++) {
                    double d = distance(points.get(i), centroids.get(c));
                    if (d < minDist) {
                        minDist = d;
                        cluster = c;
                    }
                }
                if (labels[i] != cluster) {
                    labels[i] = cluster;
                    changed = true;
                }
            }

            double[][] newCentroids = new double[k][points.get(0).length];
            int[] count = new int[k];

            for (int i = 0; i < points.size(); i++) {
                int c = labels[i];
                for (int j = 0; j < points.get(i).length; j++)
                    newCentroids[c][j] += points.get(i)[j];
                count[c]++;
            }

            for (int c = 0; c < k; c++) {
                if (count[c] > 0)
                    for (int j = 0; j < newCentroids[c].length; j++)
                        newCentroids[c][j] /= count[c];
            }

            for (int c = 0; c < k; c++)
                centroids.set(c, newCentroids[c]);
        }

        System.out.println("\nFinal Centroids and Cluster Members:\n");
        for (int c = 0; c < k; c++) {
            System.out.print("C" + (c + 1) + " (");
            for (int j = 0; j < centroids.get(c).length; j++) {
                System.out.print(centroids.get(c)[j]);
                if (j < centroids.get(c).length - 1) System.out.print(", ");
            }
            System.out.print(") -> ");

            List<String> members = new ArrayList<>();
            for (int i = 0; i < labels.length; i++)
                if (labels[i] == c) members.add("P" + (i + 1));

            System.out.println(String.join(", ", members));
        }

        System.out.println("\nTotal Iterations: " + iteration);
    }
}
