//Normalization
import java.io.*;
import java.util.*;

public class NormalizationWithChoice {

    public static void main(String[] args) throws IOException {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter path to input CSV file: ");
        String inputFilePath = scanner.nextLine();

        System.out.print("Enter the column name to normalize: ");
        String columnName = scanner.nextLine();

        System.out.print("Choose normalization method (1 = Min-Max, 2 = Z-score, 3 = Decimal Scaling): ");
        int method = Integer.parseInt(scanner.nextLine());

        double newMin = -1.0, newMax = 1.0;

        System.out.print("Enter path to output CSV file: ");
        String outputFilePath = scanner.nextLine();

        BufferedReader br = new BufferedReader(new FileReader(inputFilePath));
        String headerLine = br.readLine();

        if (headerLine == null) {
            System.out.println("Input CSV file is empty.");
            br.close();
            scanner.close();
            return;
        }

        String[] headers = headerLine.split(",");
        int colIndex = -1;
        for (int i = 0; i < headers.length; i++) {
            if (headers[i].trim().equalsIgnoreCase(columnName.trim())) {
                colIndex = i;
                break;
            }
        }

        if (colIndex == -1) {
            System.out.println("Column '" + columnName + "' not found in input CSV.");
            br.close();
            scanner.close();
            return;
        }

        List<String[]> allRows = new ArrayList<>();
        allRows.add(headers);

        List<Double> values = new ArrayList<>();
        String line;
        while ((line = br.readLine()) != null) {
            String[] parts = line.split(",");
            values.add(Double.parseDouble(parts[colIndex].trim()));
            allRows.add(parts);
        }
        br.close();

        double min = Collections.min(values);
        double max = Collections.max(values);

        double mean = 0.0;
        for (double v : values) mean += v;
        mean /= values.size();

        double variance = 0.0;
        for (double v : values) variance += Math.pow(v - mean, 2);
        double stdDev = Math.sqrt(variance / values.size());

        int maxAbs = (int) Math.pow(10, String.valueOf(Collections.max(values).intValue()).length());

        for (int i = 1; i < allRows.size(); i++) {
            String[] row = allRows.get(i);
            double original = Double.parseDouble(row[colIndex].trim());
            double normalized = 0.0;

            switch (method) {
                case 1:
                    // ✅ Scales into [-1, 1] now
                    normalized = (original - min) / (max - min) * (newMax - newMin) + newMin;
                    break;

                case 2:
                    normalized = (stdDev != 0) ? (original - mean) / stdDev : 0;
                    break;

                case 3:
                    normalized = original / maxAbs;
                    break;

                default:
                    System.out.println("Invalid method choice.");
                    return;
            }

            row[colIndex] = String.format("%.5f", normalized);
        }

        BufferedWriter bw = new BufferedWriter(new FileWriter(outputFilePath));
        for (String[] row : allRows) {
            bw.write(String.join(",", row));
            bw.newLine();
        }
        bw.close();

        System.out.println("Normalization complete. Output saved to " + outputFilePath);
        scanner.close();
    }
}


// import java.io.*;
// import java.util.*;

// public class SimpleNormalization {

//     public static void main(String[] args) {
//         Scanner sc = new Scanner(System.in);

//         System.out.print("Enter CSV file name: ");
//         String fileName = sc.nextLine().trim();

//         List<String[]> data = new ArrayList<>();
//         String[] header = null;

//         try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
//             header = br.readLine().split(",");
//             String line;
//             while ((line = br.readLine()) != null) {
//                 data.add(line.split(","));
//             }
//         } catch (Exception e) {
//             System.out.println("Error: " + e.getMessage());
//             return;
//         }

//         System.out.println("\nColumns:");
//         for (int i = 0; i < header.length; i++)
//             System.out.println(i + " - " + header[i]);

//         System.out.print("\nEnter column number to normalize: ");
//         int col = sc.nextInt();
//         sc.nextLine(); // consume newline

//         System.out.print("Method (min-max / z-score / decimal): ");
//         String method = sc.nextLine().trim().toLowerCase();

//         // Convert column values to double
//         double[] values = new double[data.size()];
//         for (int i = 0; i < data.size(); i++)
//             values[i] = Double.parseDouble(data.get(i)[col]);

//         double[] norm = new double[values.length];

//         if (method.equals("min-max")) {
//             double min = Arrays.stream(values).min().getAsDouble();
//             double max = Arrays.stream(values).max().getAsDouble();
//             for (int i = 0; i < values.length; i++)
//                 norm[i] = (values[i] - min) / (max - min);

//         } else if (method.equals("z-score")) {
//             double mean = Arrays.stream(values).average().getAsDouble();
//             double sum = 0;
//             for (double v : values) sum += Math.pow(v - mean, 2);
//             double std = Math.sqrt(sum / values.length);
//             for (int i = 0; i < values.length; i++)
//                 norm[i] = (values[i] - mean) / std;

//         } else if (method.equals("decimal")) {
//             double maxVal = Arrays.stream(values).map(Math::abs).max().getAsDouble();
//             int j = (int) Math.log10(maxVal) + 1;
//             for (int i = 0; i < values.length; i++)
//                 norm[i] = values[i] / Math.pow(10, j);

//         } else {
//             System.out.println("Invalid method!");
//             return;
//         }

//         System.out.println("\nOriginal vs Normalized:");
//         System.out.println("--------------------------");
//         for (int i = 0; i < Math.min(5, values.length); i++)
//             System.out.printf("%.3f  ->  %.3f%n", values[i], norm[i]);
//     }
// }
