import java.io.*;
import java.util.*;

public class NaiveBayesCSV {

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        System.out.print("Enter CSV file name (e.g., loan.csv): ");
        String filename = sc.nextLine();

        List<String[]> data = new ArrayList<>();
        List<String> headers = new ArrayList<>();

        // --- Read CSV File ---
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line = br.readLine();
            if (line == null) {
                System.out.println("Empty file!");
                return;
            }
            headers = Arrays.asList(line.split(","));
            while ((line = br.readLine()) != null) {
                data.add(line.split(","));
            }
        } catch (IOException e) {
            System.out.println("Error reading file!");
            return;
        }

        int n = data.size();
        int targetIndex = headers.size() - 1;

        // --- Get distinct class labels ---
        Set<String> classes = new HashSet<>();
        for (String[] row : data) classes.add(row[targetIndex]);

        // --- Get user input for prediction ---
        Map<String, String> input = new HashMap<>();
        for (int i = 0; i < headers.size() - 1; i++) {
            System.out.print("Enter " + headers.get(i) + ": ");
            input.put(headers.get(i), sc.nextLine());
        }

        // --- Calculate priors and conditional probabilities ---
        Map<String, Double> classProb = new HashMap<>();
        for (String c : classes) {
            double countClass = 0;
            for (String[] row : data) {
                if (row[targetIndex].equalsIgnoreCase(c)) countClass++;
            }
            double prior = countClass / n;

            double likelihood = 1.0;



             for (int i = 0; i < headers.size() - 1; i++) {
                String feature = headers.get(i);
                String userVal = input.get(feature);
                boolean numeric = isNumeric(data.get(0)[i]); // check if numeric column

                if (numeric) {
                    // ⭐ Gaussian Handling for Continuous Attributes
                    List<Double> classValues = new ArrayList<>();
                    for (String[] row : data) {
                        if (row[targetIndex].equalsIgnoreCase(c))
                            classValues.add(Double.parseDouble(row[i]));
                    }

                    double mean = mean(classValues);
                    double sd = stddev(classValues, mean);
                    double x = Double.parseDouble(userVal);
                    double prob = gaussianProb(x, mean, sd);

                    likelihood *= (prob == 0 ? 1e-6 : prob);
                } else {
                    // Original categorical handling
                    double match = 0;
                    for (String[] row : data) {
                        if (row[targetIndex].equalsIgnoreCase(c) &&
                            row[i].equalsIgnoreCase(userVal)) {
                            match++;
                        }
                    }
                    likelihood *= (match == 0 ? 1e-6 : match / countClass);
                }
            }

            classProb.put(c, prior * likelihood);
        }

        // --- Show probabilities and result ---
        System.out.println("\n--- Naive Bayes Classification ---");
        for (String c : classes) {
            System.out.printf("P(%s|X) = %.8f%n", c, classProb.get(c));
        }

        String predicted = Collections.max(classProb.entrySet(), Map.Entry.comparingByValue()).getKey();
        System.out.println("\nPredicted Class: " + predicted);

        sc.close();
    }

    static boolean isNumeric(String str) {
        try {
            Double.parseDouble(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    static double mean(List<Double> values) {
        double sum = 0;
        for (double v : values) sum += v;
        return sum / values.size();
    }

    static double stddev(List<Double> values, double mean) {
        double sum = 0;
        for (double v : values) sum += Math.pow(v - mean, 2);
        return Math.sqrt(sum / values.size());
    }

    static double gaussianProb(double x, double mean, double sd) {
        return (1 / (Math.sqrt(2 * Math.PI) * sd)) * Math.exp(-Math.pow(x - mean, 2) / (2 * sd * sd));
    }

    // ---------------------------------------------------------------
}