import java.io.*;
import java.util.*;

public class Apriori {

    // Read CSV file and return transactions
    public static List<Set<String>> readCSV(String filename) throws IOException {
        List<Set<String>> transactions = new ArrayList<>();
        BufferedReader br = new BufferedReader(new FileReader(filename));
        String line;
        while ((line = br.readLine()) != null) {
            Set<String> transaction = new HashSet<>(Arrays.asList(line.split(",")));
            transactions.add(transaction);
        }
        br.close();
        return transactions;
    }

    // Generate candidate 1-itemsets
    public static Set<Set<String>> genCan(List<Set<String>> transactions) {
        Set<Set<String>> candidate1 = new HashSet<>();
        for (Set<String> transaction : transactions) {
            for (String item : transaction) {
                candidate1.add(new HashSet<>(Collections.singletonList(item)));
            }
        }
        return candidate1;
    }

    // Count support for candidate itemsets
    public static Map<Set<String>, Integer> countSupport(List<Set<String>> transactions, Set<Set<String>> candidates) {
        Map<Set<String>, Integer> supportCounts = new HashMap<>();
        for (Set<String> transaction : transactions) {
            for (Set<String> candidate : candidates) {
                if (transaction.containsAll(candidate)) {
                    supportCounts.put(candidate, supportCounts.getOrDefault(candidate, 0) + 1);
                }
            }
        }
        return supportCounts;
    }

    // Prune based on minSupport
    public static Set<Set<String>> pruneItemsets(Map<Set<String>, Integer> supportCounts, double minSupport, int totalTransactions) {
        Set<Set<String>> frequentItemsets = new HashSet<>();
        for (Map.Entry<Set<String>, Integer> entry : supportCounts.entrySet()) {
            double supportPercentage = (double) entry.getValue() / totalTransactions * 100;
            if (supportPercentage >= minSupport) {
                frequentItemsets.add(entry.getKey());
            }
        }
        return frequentItemsets;
    }

    // Generate candidate k-itemsets
    public static Set<Set<String>> generateCandidates(Set<Set<String>> frequentItemsets, int k) {
        Set<Set<String>> candidates = new HashSet<>();
        List<Set<String>> itemsets = new ArrayList<>(frequentItemsets);

        for (int i = 0; i < itemsets.size(); i++) {
            for (int j = i + 1; j < itemsets.size(); j++) {
                Set<String> merged = new HashSet<>(itemsets.get(i));
                merged.addAll(itemsets.get(j));
                if (merged.size() == k) {
                    candidates.add(merged);
                }
            }
        }
        return candidates;
    }

    // Print frequent itemsets
    public static void printFrequentItemsets(Set<Set<String>> frequentItemsets, Map<Set<String>, Integer> supportCounts, int totalTransactions) {
        for (Set<String> itemset : frequentItemsets) {
            double supportPercentage = (double) supportCounts.get(itemset) / totalTransactions * 100;
            System.out.print("{ ");
            for (String item : itemset) {
                System.out.print(item + " ");
            }
            System.out.println("} - Support: " + supportPercentage + "%");
        }
    }

    // Generate association rules
    public static void generateAssociationRules(Set<Set<String>> allFrequentItemsets, Map<Set<String>, Integer> supportCounts, int totalTransactions, double minConfidence) {
        for (Set<String> itemset : allFrequentItemsets) {
            if (itemset.size() < 2) continue;

            List<String> items = new ArrayList<>(itemset);
            int n = items.size();
            int supportItemset = supportCounts.get(itemset);

            for (int mask = 1; mask < (1 << n) - 1; mask++) {
                Set<String> antecedent = new HashSet<>();
                Set<String> consequent = new HashSet<>();
                for (int i = 0; i < n; i++) {
                    if ((mask & (1 << i)) != 0)
                        antecedent.add(items.get(i));
                    else
                        consequent.add(items.get(i));
                }

                if (!supportCounts.containsKey(antecedent)) continue;
                int supportAntecedent = supportCounts.get(antecedent);
                double confidence = (double) supportItemset / supportAntecedent * 100;

                if (confidence >= minConfidence) {
                    double supportPercentage = (double) supportItemset / totalTransactions * 100;
                    System.out.print("{ ");
                    for (String a : antecedent) System.out.print(a + " ");
                    System.out.print("} => { ");
                    for (String c : consequent) System.out.print(c + " ");
                    System.out.println("} (Support: " + supportPercentage + "%, Confidence: " + confidence + "%)");
                }
            }
        }
    }

    public static void main(String[] args) throws IOException {
        Scanner sc = new Scanner(System.in);
        System.out.print("Enter CSV file name: ");
        String filename = sc.nextLine();
        System.out.print("Enter minimum support (%): ");
        double minSupport = sc.nextDouble();
        System.out.print("Enter minimum confidence (%): ");
        double minConfidence = sc.nextDouble();
        sc.close();

        List<Set<String>> transactions = readCSV(filename);
        int totalTransactions = transactions.size();

        Set<Set<String>> candidate1 = genCan(transactions);
        Map<Set<String>, Integer> supportCounts = countSupport(transactions, candidate1);
        Set<Set<String>> allFrequentItemsets = new HashSet<>();
        Set<Set<String>> frequentItemsets = pruneItemsets(supportCounts, minSupport, totalTransactions);
        allFrequentItemsets.addAll(frequentItemsets);

        System.out.println("Frequent Itemsets (size 1):");
        printFrequentItemsets(frequentItemsets, supportCounts, totalTransactions);

        int k = 2;
        while (!frequentItemsets.isEmpty()) {
            Set<Set<String>> candidateK = generateCandidates(frequentItemsets, k);
            if (candidateK.isEmpty()) break;
            Map<Set<String>, Integer> supportK = countSupport(transactions, candidateK);
            frequentItemsets = pruneItemsets(supportK, minSupport, totalTransactions);

            if (!frequentItemsets.isEmpty()) {
                System.out.println("\nFrequent Itemsets (size " + k + "):");
                printFrequentItemsets(frequentItemsets, supportK, totalTransactions);

                supportCounts.putAll(supportK);
                allFrequentItemsets.addAll(frequentItemsets);
            }
            k++;
        }

        System.out.println("\nAssociation Rules:");
        generateAssociationRules(allFrequentItemsets, supportCounts, totalTransactions, minConfidence);
    }
}
