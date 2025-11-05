import java.io.*;
import java.util.*;

public class ClassComparison {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        System.out.print("Enter path to input CSV file: ");
        String file = sc.nextLine();

        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String header = br.readLine(); 

            if (header == null) {
                System.out.println("File is empty!");
                br.close();
                return;
            }

            Map<String, Map<String, Integer>> data = new LinkedHashMap<>();
            Set<String> places = new LinkedHashSet<>();
            int grandTotal = 0;

            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length < 3) continue;

                String cls = parts[0].trim();
                String place = parts[1].trim();
                int count = Integer.parseInt(parts[2].trim());

                data.putIfAbsent(cls, new LinkedHashMap<>());
                data.get(cls).put(place, count);
                places.add(place);
                grandTotal += count;
            }
            br.close();

            Map<String, Integer> classTotal = new LinkedHashMap<>();
            Map<String, Integer> placeTotal = new LinkedHashMap<>();

            for (String cls : data.keySet()) {
                int sum = 0;
                for (String place : data.get(cls).keySet()) {
                    int val = data.get(cls).get(place);
                    sum += val;
                    placeTotal.put(place, placeTotal.getOrDefault(place, 0) + val);
                }
                classTotal.put(cls, sum);
            }

            System.out.println("\n===================== CLASS COMPARISON TABLE =====================\n");
            System.out.printf("%-12s", "Class");
            for (String place : places)
                System.out.printf("%-25s", place + " (Cnt / T-weight / D-weight)");
            System.out.printf("%-25s%n", "Total (Cnt / T-weight / D-weight)");
            System.out.println("---------------------------------------------------------------------------------------------------------------------------------");

            for (String cls : data.keySet()) {
                System.out.printf("%-12s", cls);
                int totalClass = classTotal.get(cls);

                for (String place : places) {
                    int cnt = data.get(cls).getOrDefault(place, 0);
                    double tWeight = (double) cnt / totalClass * 100; 
                    double dWeight = (double) cnt / grandTotal * 100; 
                    System.out.printf("%-25s", String.format("%3d / %6.2f%% / %6.2f%%", cnt, tWeight, dWeight));
                }

                double totalT = 100.0;
                double totalD = (double) totalClass / grandTotal * 100;
                System.out.printf("%-25s%n", String.format("%3d / %6.2f%% / %6.2f%%", totalClass, totalT, totalD));
            }

            System.out.println("---------------------------------------------------------------------------------------------------------------------------------");

            System.out.printf("%-12s", "Total");
            for (String place : places) {
                int cnt = placeTotal.getOrDefault(place, 0);
                double tWeight = (double) cnt / grandTotal * 100;
                double dWeight = tWeight;
                System.out.printf("%-25s", String.format("%3d / %6.2f%% / %6.2f%%", cnt, tWeight, dWeight));
            }
            System.out.printf("%-25s%n", String.format("%3d / 100.00%% / 100.00%%", grandTotal));

            System.out.println("\n=================================================================\n");

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }

        sc.close();
    }
}




// import java.io.*;
// import java.util.*;

// public class CsvSummarizer {

//     // Function to read CSV and return headers + list of records
//     public static Pair<List<String>, List<Map<String, String>>> readCSV(String filename) {
//         List<String> headers = new ArrayList<>();
//         List<Map<String, String>> rows = new ArrayList<>();

//         try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
//             String line = br.readLine();
//             if (line == null) {
//                 System.out.println("Empty file.");
//                 System.exit(1);
//             }

//             headers = Arrays.asList(line.split(","));

//             while ((line = br.readLine()) != null) {
//                 String[] values = line.split(",", -1);
//                 Map<String, String> record = new LinkedHashMap<>();
//                 for (int i = 0; i < headers.size(); i++) {
//                     record.put(headers.get(i), i < values.length ? values[i].trim() : "");
//                 }
//                 rows.add(record);
//             }

//         } catch (FileNotFoundException e) {
//             System.out.println("File not found.");
//             System.exit(1);
//         } catch (IOException e) {
//             System.out.println("Error reading CSV: " + e.getMessage());
//             System.exit(1);
//         }

//         return new Pair<>(headers, rows);
//     }

//     public static void main(String[] args) {
//         Scanner sc = new Scanner(System.in);
//         System.out.print("Enter input CSV path: ");
//         String file = sc.nextLine().trim();

//         Pair<List<String>, List<Map<String, String>>> csvData = readCSV(file);
//         List<String> headers = csvData.first;
//         List<Map<String, String>> rows = csvData.second;

//         System.out.println("\nColumns available: " + String.join(", ", headers));

//         System.out.print("Enter row field (e.g., " + headers.get(0) + "): ");
//         String rowField = sc.nextLine().trim();
//         System.out.print("Enter column field (e.g., " + headers.get(1) + "): ");
//         String colField = sc.nextLine().trim();
//         System.out.print("Enter numeric value field (e.g., " + headers.get(headers.size() - 1) + "): ");
//         String valField = sc.nextLine().trim();

//         if (!headers.contains(rowField) || !headers.contains(colField) || !headers.contains(valField)) {
//             System.out.println("Invalid column name(s).");
//             System.exit(1);
//         }

//         Map<String, Map<String, Double>> data = new LinkedHashMap<>();
//         List<String> colValues = new ArrayList<>();

//         for (Map<String, String> r : rows) {
//             try {
//                 String rowVal = r.get(rowField).trim();
//                 String colVal = r.get(colField).trim();
//                 double val = Double.parseDouble(r.get(valField));

//                 data.putIfAbsent(rowVal, new LinkedHashMap<>());
//                 data.get(rowVal).put(colVal, val);

//                 if (!colValues.contains(colVal)) colValues.add(colVal);
//             } catch (Exception e) {
//                 System.out.println("Non-numeric value in '" + valField + "', skipping row.");
//             }
//         }

//         if (data.isEmpty()) {
//             System.out.println("No valid numeric data found.");
//             System.exit(1);
//         }

//         // Compute totals
//         Map<String, Double> rowTotals = new LinkedHashMap<>();
//         Map<String, Double> colTotals = new LinkedHashMap<>();

//         for (String r : data.keySet()) {
//             double sum = data.get(r).values().stream().mapToDouble(Double::doubleValue).sum();
//             rowTotals.put(r, sum);
//             for (String c : data.get(r).keySet()) {
//                 colTotals.put(c, colTotals.getOrDefault(c, 0.0) + data.get(r).get(c));
//             }
//         }

//         double grandTotal = colTotals.values().stream().mapToDouble(Double::doubleValue).sum();

//         // Print formatted table
//         int colWidth = 22;
//         System.out.print("\n        ");
//         for (String c : colValues)
//             System.out.printf("%" + colWidth + "s", c);
//         System.out.println("Total");
//         System.out.println("-".repeat(8 + colWidth * colValues.size() + 10));

//         for (String r : data.keySet()) {
//             System.out.printf("%-8s", r);
//             for (String c : colValues) {
//                 double v = data.get(r).getOrDefault(c, 0.0);
//                 double rowTotal = rowTotals.get(r);
//                 double colTotal = colTotals.get(c);
//                 double dwt = rowTotal != 0 ? (v / rowTotal * 100.0) : 0.0;
//                 double twt = colTotal != 0 ? (v / colTotal * 100.0) : 0.0;
//                 System.out.printf("%" + colWidth + "s",
//                         String.format("%.2f %6.2f%% %6.2f%%", v, twt, dwt));
//             }
//             System.out.printf("%.2f 100.00%%\n", rowTotals.get(r));
//         }

//         System.out.println("-".repeat(8 + colWidth * colValues.size() + 10));

//         System.out.printf("%-8s", "Total");
//         for (String c : colValues) {
//             System.out.printf("%" + colWidth + "s",
//                     String.format("%.2f 100.00%%", colTotals.get(c)));
//         }
//         System.out.printf("%.2f\n", grandTotal);
//         System.out.println("-".repeat(8 + colWidth * colValues.size() + 10));
//     }

//     // Simple helper class to return two values (like Python tuples)
//     static class Pair<F, S> {
//         public final F first;
//         public final S second;

//         Pair(F f, S s) {
//             this.first = f;
//             this.second = s;
//         }
//     }
// }
