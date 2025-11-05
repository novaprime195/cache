import java.io.*;
import java.util.*;

public class SimpleOLAP {

    // Read CSV file
    public static List<Map<String, String>> readCSV(String fileName, List<String> header) {
        List<Map<String, String>> records = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line = br.readLine();
            if (line == null) return records;
            String[] headers = line.split(",");
            header.addAll(Arrays.asList(headers));

            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                Map<String, String> row = new HashMap<>();
                for (int i = 0; i < headers.length; i++) {
                    row.put(headers[i], parts[i]);
                }
                records.add(row);
            }
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        }
        return records;
    }

    // Print record
    public static void printRecord(Map<String, String> r, List<String> header) {
        for (String h : header)
            System.out.print(r.get(h) + " | ");
        System.out.println();
    }

    // Slice: single condition
    public static void slice(List<Map<String, String>> data, String field, String value, List<String> header) {
        System.out.println("\nSlice: " + field + " = " + value);
        for (Map<String, String> r : data) {
            if (r.get(field).equalsIgnoreCase(value))
                printRecord(r, header);
        }
    }

    // Dice: multiple conditions
    public static void dice(List<Map<String, String>> data, Map<String, String> filters, List<String> header) {
        System.out.println("\nDice result:");
        for (Map<String, String> r : data) {
            boolean match = true;
            for (String key : filters.keySet()) {
                if (!r.get(key).equalsIgnoreCase(filters.get(key))) {
                    match = false;
                    break;
                }
            }
            if (match) printRecord(r, header);
        }
    }

    // Roll-up: group by and sum
    public static void rollup(List<Map<String, String>> data, String groupField, String numericField) {
        System.out.println("\nRoll-Up by " + groupField + " (sum of " + numericField + ")");
        Map<String, Double> totals = new HashMap<>();
        for (Map<String, String> r : data) {
            try {
                double val = Double.parseDouble(r.get(numericField));
                totals.put(r.get(groupField), totals.getOrDefault(r.get(groupField), 0.0) + val);
            } catch (NumberFormatException e) {
                System.out.println("Column not numeric!");
                return;
            }
        }
        for (String key : totals.keySet()) {
            System.out.println(key + " → " + totals.get(key));
        }
    }

    // Drill-down: show all data
    public static void drilldown(List<Map<String, String>> data, List<String> header) {
        System.out.println("\nDrill-down view:");
        for (Map<String, String> r : data)
            printRecord(r, header);
    }

    // Pivot
    public static void pivot(List<Map<String, String>> data, String rowField, String colField, String numericField) {
        System.out.println("\nPivot (" + rowField + " vs " + colField + ")");
        Map<String, Map<String, Double>> pivot = new HashMap<>();
        Set<String> colValues = new TreeSet<>();

        for (Map<String, String> r : data) {
            try {
                double val = Double.parseDouble(r.get(numericField));
                String row = r.get(rowField);
                String col = r.get(colField);
                colValues.add(col);

                pivot.putIfAbsent(row, new HashMap<>());
                Map<String, Double> inner = pivot.get(row);
                inner.put(col, inner.getOrDefault(col, 0.0) + val);
            } catch (NumberFormatException e) {
                System.out.println("Column not numeric!");
                return;
            }
        }

        // Print pivot table
        System.out.print(rowField + "\t");
        for (String col : colValues) System.out.print(col + "\t");
        System.out.println();

        for (String row : pivot.keySet()) {
            System.out.print(row + "\t");
            for (String col : colValues) {
                System.out.print(pivot.get(row).getOrDefault(col, 0.0) + "\t");
            }
            System.out.println();
        }
    }

    // Main
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        System.out.print("Enter CSV file name: ");
        String fileName = sc.nextLine();

        List<String> header = new ArrayList<>();
        List<Map<String, String>> data = readCSV(fileName, header);

        if (data.isEmpty()) {
            System.out.println("No data found!");
            return;
        }

        System.out.println("\nColumns: " + header);

        while (true) {
            System.out.println("\n--- OLAP MENU ---");
            System.out.println("1. Show Data");
            System.out.println("2. Slice");
            System.out.println("3. Dice");
            System.out.println("4. Roll-Up");
            System.out.println("5. Drill-Down");
            System.out.println("6. Pivot");
            System.out.println("0. Exit");
            System.out.print("Choice: ");
            int ch = sc.nextInt();
            sc.nextLine();

            if (ch == 1) {
                for (Map<String, String> r : data) printRecord(r, header);

            } else if (ch == 2) {
                System.out.print("Field: ");
                String field = sc.nextLine();
                System.out.print("Value: ");
                String value = sc.nextLine();
                slice(data, field, value, header);

            } else if (ch == 3) {
                System.out.print("How many filters? ");
                int n = sc.nextInt(); sc.nextLine();
                Map<String, String> filters = new HashMap<>();
                for (int i = 0; i < n; i++) {
                    System.out.print("Field: ");
                    String f = sc.nextLine();
                    System.out.print("Value: ");
                    String v = sc.nextLine();
                    filters.put(f, v);
                }
                dice(data, filters, header);

            } else if (ch == 4) {
                System.out.print("Group by field: ");
                String group = sc.nextLine();
                System.out.print("Numeric field: ");
                String num = sc.nextLine();
                rollup(data, group, num);

            } else if (ch == 5) {
                drilldown(data, header);

            } else if (ch == 6) {
                System.out.print("Row field: ");
                String row = sc.nextLine();
                System.out.print("Column field: ");
                String col = sc.nextLine();
                System.out.print("Numeric field: ");
                String num = sc.nextLine();
                pivot(data, row, col, num);

            } else if (ch == 0) {
                System.out.println("Goodbye!");
                break;
            } else {
                System.out.println("Invalid choice!");
            }
        }
    }
}



