#Apriori Algorithm for Frequent Itemset 
def read_csv(filename):
    transactions = []
    with open(filename, 'r') as f:
        for line in f:
            items = [item.strip() for item in line.strip().split(',') if item.strip()]
            if items:
                transactions.append(set(items))
    return transactions

def gen_candidate1(transactions):
    candidates = set()
    for tr in transactions:
        for item in tr:
            candidates.add(frozenset([item]))
    return candidates

def count_support(transactions, candidates):
    support_counts = {}
    for tr in transactions:
        for cand in candidates:
            if cand.issubset(tr):
                support_counts[cand] = support_counts.get(cand, 0) + 1
    return support_counts

def prune_itemsets(support_counts, min_support_pct, total_transactions):
    frequent_itemsets = set()
    for itemset, count in support_counts.items():
        support_percentage = count / total_transactions * 100
        if support_percentage >= min_support_pct:
            frequent_itemsets.add(itemset)
    return frequent_itemsets

def generate_candidates(frequent_itemsets, k):
    candidates = set()
    itemsets_list = list(frequent_itemsets)
    n = len(itemsets_list)
    for i in range(n):
        for j in range(i + 1, n):
            merged = itemsets_list[i] | itemsets_list[j]
            if len(merged) == k:
                candidates.add(merged)
    return candidates

def print_frequent_itemsets(frequent_itemsets, support_counts, total_transactions):
    for itemset in frequent_itemsets:
        support_pct = support_counts[itemset] / total_transactions * 100
        print(f"{set(itemset)} - Support: {support_pct:.2f}%")

def manual_combinations(items, r):
    result = []
    def combine(start, path):
        if len(path) == r:
            result.append(list(path))
            return
        for i in range(start, len(items)):
            combine(i + 1, path + [items[i]])
    combine(0, [])
    return result

def generate_association_rules(all_frequent_itemsets, support_counts, total_transactions, min_confidence):
    for itemset in all_frequent_itemsets:
        if len(itemset) < 2:
            continue
        items = list(itemset)
        n = len(items)
        support_itemset = support_counts[itemset]
        for r in range(1, n):
            for subset in manual_combinations(items, r):
                antecedent = frozenset(subset)
                consequent = itemset - antecedent
                if antecedent in support_counts:
                    support_antecedent = support_counts[antecedent]
                    confidence = support_itemset / support_antecedent * 100
                    if confidence >= min_confidence:
                        support_pct = support_itemset / total_transactions * 100
                        print(f"{set(antecedent)} => {set(consequent)} (Support: {support_pct:.2f}%, Confidence: {confidence:.2f}%)")

if __name__ == '__main__':
    filename = input("Enter CSV file name: ")
    min_support = float(input("Enter minimum support (%): "))
    min_confidence = float(input("Enter minimum confidence (%): "))

    transactions = read_csv(filename)
    total_transactions = len(transactions)

    candidate1 = gen_candidate1(transactions)
    support_counts = count_support(transactions, candidate1)
    frequent_itemsets = prune_itemsets(support_counts, min_support, total_transactions)
    all_frequent_itemsets = set(frequent_itemsets)

    print("\nFrequent Itemsets (size 1):")
    print_frequent_itemsets(frequent_itemsets, support_counts, total_transactions)

    k = 2
    while frequent_itemsets:
        candidate_k = generate_candidates(frequent_itemsets, k)
        if not candidate_k:
            break
        support_k = count_support(transactions, candidate_k)
        frequent_itemsets = prune_itemsets(support_k, min_support, total_transactions)
        if frequent_itemsets:
            print(f"\nFrequent Itemsets (size {k}):")
            print_frequent_itemsets(frequent_itemsets, support_k, total_transactions)
            support_counts.update(support_k)
            all_frequent_itemsets.update(frequent_itemsets)
        k += 1

    print("\nAssociation Rules:")
    generate_association_rules(all_frequent_itemsets, support_counts, total_transactions, min_confidence)
