import math

def entropy(class_counts):
    total = sum(class_counts.values())
    if total == 0:
        return 0
    ent = 0.0
    for count in class_counts.values():
        if count == 0:
            continue
        p = count / total
        ent -= p * math.log2(p)
    return ent

def gini_index(class_counts):
    total = sum(class_counts.values())
    if total == 0:
        return 0
    gini = 1.0
    for count in class_counts.values():
        p = count / total
        gini -= p ** 2
    return gini

def class_counts(rows, label_index):
    counts = {}
    for row in rows:
        label = row[label_index]
        counts[label] = counts.get(label, 0) + 1
    return counts

def split_categorical(rows, feature_index):
    splits = {}
    for row in rows:
        key = row[feature_index]
        if key not in splits:
            splits[key] = []
        splits[key].append(row)
    return splits

def split_numerical(rows, feature_index):
    values = sorted(float(row[feature_index]) for row in rows)
    if not values:
        return {}
    median = values[len(values)//2]
    left = [row for row in rows if float(row[feature_index]) <= median]
    right = [row for row in rows if float(row[feature_index]) > median]
    return {"<=" + str(median): left, ">" + str(median): right}

def info_gain(rows, feature_index, label_index, numerical=False):
    if numerical:
        splits = split_numerical(rows, feature_index)
    else:
        splits = split_categorical(rows, feature_index)
    total_entropy = entropy(class_counts(rows, label_index))
    total_len = len(rows)
    weighted_entropy = 0.0
    for split_rows in splits.values():
        weighted_entropy += (len(split_rows)/total_len) * entropy(class_counts(split_rows, label_index))
    return total_entropy - weighted_entropy

def gini_gain(rows, feature_index, label_index, numerical=False):
    if numerical:
        splits = split_numerical(rows, feature_index)
    else:
        splits = split_categorical(rows, feature_index)
    total_len = len(rows)
    weighted_gini = 0.0
    for split_rows in splits.values():
        weighted_gini += (len(split_rows)/total_len) * gini_index(class_counts(split_rows, label_index))
    return weighted_gini

def read_csv(filename):
    dataset = []
    with open(filename, "r") as f:
        for line in f:
            line = line.strip()
            if not line:
                continue
            dataset.append(line.split(","))
    return dataset

def detect_numerical(value):
    try:
        float(value)
        return True
    except:
        return False

if __name__ == "__main__":
    filename = input("Enter CSV filename: ").strip()
    dataset = read_csv(filename)
    header = dataset[0]
    dataset = dataset[1:]
    label_index = len(header) - 1
    features = header[:-1]
    
    numerical_features = [detect_numerical(dataset[0][i]) for i in range(len(features))]
    
    print("Information Gain for each feature:")
    for i, feature in enumerate(features):
        gain = info_gain(dataset, i, label_index, numerical_features[i])
        print(f"{feature}: {gain:.4f}")
    
    print("\nGini Index for each feature:")
    for i, feature in enumerate(features):
        gini = gini_gain(dataset, i, label_index, numerical_features[i])
        print(f"{feature}: {gini:.4f}")
