import java.io.*;
import java.util.*;

public class BoxPlotAssignment {

    public static double findMedian(ArrayList<Integer> arr) {
        Collections.sort(arr);
        int n = arr.size();
        if (n % 2 == 0) {
            return (arr.get(n / 2 - 1) + arr.get(n / 2)) / 2.0;
        } else {
            return arr.get(n / 2);
        }
    }

    public static void fiveSummary(ArrayList<Integer> arr, String name) {
        Collections.sort(arr);
        int n = arr.size();

        double q2 = findMedian(arr);

        ArrayList<Integer> lower, upper;
        if (n % 2 == 0) {
            lower = new ArrayList<>(arr.subList(0, n / 2));
            upper = new ArrayList<>(arr.subList(n / 2, n));
        } else {
            lower = new ArrayList<>(arr.subList(0, n / 2));
            upper = new ArrayList<>(arr.subList(n / 2 + 1, n));
        }

        double q1 = findMedian(lower);
        double q3 = findMedian(upper);
        double iqr = q3 - q1;

        double lowerLimit = q1 - 1.5 * iqr;
        double upperLimit = q3 + 1.5 * iqr;

        int lowWhisker = arr.get(0);
        for (int val : arr) {
            if (val >= lowerLimit) {
                lowWhisker = val;
                break;
            }
        }

        int upWhisker = arr.get(n - 1);
        for (int i = n - 1; i >= 0; i--) {
            if (arr.get(i) <= upperLimit) {
                upWhisker = arr.get(i);
                break;
            }
        }

        System.out.println("\n--- " + name + " ---");
        System.out.println("Q1 = " + q1);
        System.out.println("Median (Q2) = " + q2);
        System.out.println("Q3 = " + q3);
        System.out.println("IQR = " + iqr);
        System.out.println("Lower Whisker = " + lowWhisker);
        System.out.println("Upper Whisker = " + upWhisker);
    }

    public static void main(String[] args) {
        String file = "data.csv";
        ArrayList<Integer> data = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line = br.readLine(); 
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                data.add(Integer.parseInt(parts[0].trim()));
            }
        } catch (IOException e) {
            System.out.println("Error reading file: " + e.getMessage());
        }

        fiveSummary(data, "5 Number Summary");
    }
}
