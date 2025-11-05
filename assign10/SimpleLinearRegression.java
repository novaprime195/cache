import java.io.*;
import java.util.*;

public class SimpleLinearRegression {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        System.out.print("Enter CSV file name: ");
        String filename = sc.nextLine();

        List<Double> xList = new ArrayList<>();
        List<Double> yList = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 2) {
                    xList.add(Double.parseDouble(parts[0].trim()));
                    yList.add(Double.parseDouble(parts[1].trim()));
                }
            }
        } catch (Exception e) {
            System.out.println("Error reading file!");
            return;
        }

        int n = xList.size();
        if (n == 0) {
            System.out.println("No valid data points!");
            return;
        }

        double sumX = 0, sumY = 0, sumXY = 0, sumX2 = 0;
        for (int i = 0; i < n; i++) {
            double x = xList.get(i);
            double y = yList.get(i);
            sumX += x;
            sumY += y;
            sumXY += x * y;
            sumX2 += x * x;
        }

        double m = (n * sumXY - sumX * sumY) / (n * sumX2 - sumX * sumX);
        double c = (sumY - m * sumX) / n;

        System.out.printf("\nEquation: y = %.4f * x + %.4f%n", m, c);

        System.out.print("\nEnter x to predict y: ");
        double xNew = sc.nextDouble();
        double yPred = m * xNew + c;
        System.out.printf("Predicted y = %.4f%n", yPred);

        sc.close();
    }
}
