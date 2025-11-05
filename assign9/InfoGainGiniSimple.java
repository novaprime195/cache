import java.io.*;
import java.util.*;

public class InfoGainGiniSimple {
    public static void main(String[] args) throws IOException {
        Scanner sc = new Scanner(System.in);
        System.out.print("Enter CSV filename: ");
        String filename = sc.nextLine().trim();
        sc.close();

        List<String[]> data = readCSV(filename);
        String[] header = data.get(0);
        data.remove(0);

        int labelIndex = header.length - 1;

        System.out.println("\n-----------------------------------------------");
        System.out.println("| Feature Name | Info Gain   | Gini Index     |");
        System.out.println("-----------------------------------------------");

        for (int i = 0; i < header.length - 1; i++) {
            double infoGain = calcInfoGain(data, i, labelIndex);
            double gini = calcGiniIndex(data, i, labelIndex);
            System.out.printf("| %-13s | %-11.4f | %-13.4f |\n", header[i], infoGain, gini);
        }

        System.out.println("-----------------------------------------------");
    }

    // ---------- Read CSV ----------
    static List<String[]> readCSV(String filename) throws IOException {
        List<String[]> rows = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = br.readLine()) != null)
                if (!line.trim().isEmpty())
                    rows.add(line.split(","));
        }
        return rows;
    }

    // ---------- Entropy ----------
    static double entropy(Map<String, Integer> counts) {
        double total = counts.values().stream().mapToInt(i -> i).sum();
        double ent = 0.0;
        for (int count : counts.values()) {
            double p = count / total;
            ent -= p * (Math.log(p) / Math.log(2));
        }
        return ent;
    }

    // ---------- Gini ----------
    static double gini(Map<String, Integer> counts) {
        double total = counts.values().stream().mapToInt(i -> i).sum();
        double gini = 1.0;
        for (int count : counts.values()) {
            double p = count / total;
            gini -= p * p;
        }
        return gini;
    }

    // ---------- Count classes ----------
    static Map<String, Integer> getClassCounts(List<String[]> data, int labelIndex) {
        Map<String, Integer> counts = new HashMap<>();
        for (String[] row : data)
            counts.put(row[labelIndex], counts.getOrDefault(row[labelIndex], 0) + 1);
        return counts;
    }

    // ---------- Info Gain ----------
    static double calcInfoGain(List<String[]> data, int featureIndex, int labelIndex) {
        double totalEntropy = entropy(getClassCounts(data, labelIndex));
        double total = data.size();

        Map<String, List<String[]>> splits = new HashMap<>();
        for (String[] row : data)
            splits.computeIfAbsent(row[featureIndex], k -> new ArrayList<>()).add(row);

        double weightedEntropy = 0.0;
        for (List<String[]> group : splits.values())
            weightedEntropy += (group.size() / total) * entropy(getClassCounts(group, labelIndex));

        return totalEntropy - weightedEntropy;
    }

    // ---------- Gini Index ----------
    static double calcGiniIndex(List<String[]> data, int featureIndex, int labelIndex) {
        double total = data.size();
        Map<String, List<String[]>> splits = new HashMap<>();
        for (String[] row : data)
            splits.computeIfAbsent(row[featureIndex], k -> new ArrayList<>()).add(row);

        double weightedGini = 0.0;
        for (List<String[]> group : splits.values())
            weightedGini += (group.size() / total) * gini(getClassCounts(group, labelIndex));

        return weightedGini;
    }
}
