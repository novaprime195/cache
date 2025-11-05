import java.io.*;
import java.util.*;

public class PearsonCorrelation {

    public static double mean(List<Double> values) {
        double sum = 0;
        for (double v : values) sum += v;
        return sum / values.size();
    }

    public static double pearsonCorrelation(List<Double> x, List<Double> y) {
        if (x.size() != y.size())
            throw new IllegalArgumentException("Can't calculate as len(x) != len(y)");

        double meanX = mean(x);
        double meanY = mean(y);

        double numerator = 0, denomX = 0, denomY = 0;
        for (int i = 0; i < x.size(); i++) {
            numerator += (x.get(i) - meanX) * (y.get(i) - meanY);
            denomX += Math.pow(x.get(i) - meanX, 2);
            denomY += Math.pow(y.get(i) - meanY, 2);
        }

        double denominator = Math.sqrt(denomX * denomY);
        if (denominator == 0) return 0;
        return numerator / denominator;
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        System.out.print("Enter CSV filename (with extension): ");
        String file = sc.nextLine().trim();

        List<Double> x = new ArrayList<>();
        List<Double> y = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line = br.readLine(); // skip header
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length < 2) continue;
                try {
                    x.add(Double.parseDouble(parts[0]));
                    y.add(Double.parseDouble(parts[1]));
                } catch (NumberFormatException e) {
                    // skip rows with invalid data (like "A", "B", etc.)
                    System.out.println("Skipping invalid row: " + line);
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading file: " + e.getMessage());
            return;
        }

        if (x.isEmpty() || y.isEmpty()) {
            System.out.println("No valid numeric data found in the file.");
            return;
        }

        double r = pearsonCorrelation(x, y);
        System.out.printf("\nPearson correlation coefficient (r): %.4f\n", r);

        if (r == 1.0)
            System.out.println("Perfect positive correlation");
        else if (r == -1.0)
            System.out.println("Perfect negative correlation");
        else if (r > 0)
            System.out.println("Positive correlation");
        else if (r < 0)
            System.out.println("Negative correlation");
        else
            System.out.println("No correlation");
    }
}
