import math

# Read CSV file
filename = input("Enter CSV file name (e.g., play_tennis.csv): ")
data = []
with open(filename, 'r') as f:
    lines = f.readlines()
headers = lines[0].strip().split(',')
for line in lines[1:]:
    data.append(line.strip().split(','))

target_index = len(headers) - 1
n = len(data)

# Get distinct class labels
classes = set(row[target_index] for row in data)

# Get user input
user_input = {}
for i in range(len(headers) - 1):
    val = input(f"Enter {headers[i]}: ")
    user_input[headers[i]] = val

# Helper functions
def is_numeric(s):
    try:
        float(s)
        return True
    except ValueError:
        return False

def mean(values):
    return sum(values) / len(values)

def stddev(values, mu):
    return math.sqrt(sum((v - mu)**2 for v in values) / len(values))

def gaussian_prob(x, mu, sd):
    if sd == 0:  # Avoid division by zero
        return 1e-6
    return (1 / (math.sqrt(2 * math.pi) * sd)) * math.exp(-((x - mu) ** 2) / (2 * sd ** 2))

# Calculate class probabilities
class_prob = {}
for c in classes:
    count_class = sum(1 for row in data if row[target_index].lower() == c.lower())
    prior = count_class / n

    likelihood = 1.0
    for i in range(len(headers) - 1):
        feature = headers[i]
        user_val = user_input[feature]
        numeric = is_numeric(data[0][i])

        if numeric:
            class_values = [float(row[i]) for row in data if row[target_index].lower() == c.lower()]
            mu = mean(class_values)
            sd = stddev(class_values, mu)
            x = float(user_val)
            prob = gaussian_prob(x, mu, sd)
            likelihood *= prob if prob != 0 else 1e-6
        else:
            match = sum(1 for row in data if row[target_index].lower() == c.lower() and row[i].lower() == user_val.lower())
            likelihood *= (match / count_class) if match != 0 else 1e-6

    class_prob[c] = prior * likelihood

# Show results
print("\n--- Naive Bayes Classification ---")
for c in classes:
    print(f"P({c}|X) = {class_prob[c]:.8f}")

predicted = max(class_prob, key=class_prob.get)
print(f"\nPredicted Class: {predicted}")