#normalization

import csv
import math
import sys

def is_float(s):
    try:
        float(s)
        return True
    except ValueError:
        return False



file_name = input("Enter the CSV file name: ").strip()

try:
    try:
        with open(file_name, 'r', encoding='utf-8') as f:
            reader = csv.reader(f)
            header = next(reader)
            rows = [row for row in reader]
    except UnicodeDecodeError:
        with open(file_name, 'r', encoding='latin-1') as f:
            reader = csv.reader(f)
            header = next(reader)
            rows = [row for row in reader]
except FileNotFoundError:
    print("File not found.")
    sys.exit()


print("\nAvailable columns:")
for idx, col_name in enumerate(header):
    print(f"{idx}. {col_name}")


col_choices = input("\nEnter column names to normalize (comma-separated): ").strip().split(",")
col_choices = [c.strip() for c in col_choices if c.strip()]

if not col_choices:
    print("No valid columns entered.")
    sys.exit()

for col_choice in col_choices:
    if col_choice not in header:
        print(f" Invalid column name: {col_choice}")
        sys.exit()

col_indices = [header.index(c) for c in col_choices]


for col_choice, col_index in zip(col_choices, col_indices):
    if not all(is_float(row[col_index]) for row in rows):
        print(f"Column '{col_choice}' contains non-numeric values.")
        sys.exit()

method = input("Enter normalization method (min-max, z-score, decimal-scaling): ").strip().lower()

if method == "min-max":
    a = float(input("Enter lower bound [default=0]: ") or 0)
    b = float(input("Enter upper bound [default=1]: ") or 1)

original_rows = [row.copy() for row in rows]  

for col_index in col_indices:
    col = [float(row[col_index]) for row in rows]

    if method == "min-max":
        min_val, max_val = min(col), max(col)
        norm_col = [((x - min_val) / (max_val - min_val) * (b - a) + a) if max_val != min_val else a for x in col]

    elif method == "z-score":
        mean = sum(col) / len(col)
        std = math.sqrt(sum((x - mean) ** 2 for x in col) / len(col))
        norm_col = [(x - mean) / std if std != 0 else 0.0 for x in col]

    elif method == "decimal-scaling":
        max_val = max(abs(x) for x in col)
        j = len(str(int(max_val))) if max_val != 0 else 1
        norm_col = [x / (10 ** j) for x in col]

    else:
        print("Invalid normalization method.")
        sys.exit()

    for i in range(len(rows)):
        rows[i][col_index] = str(round(norm_col[i], 6))


print("\n Normalized Data:")
for col_choice in col_choices:
    idx = header.index(col_choice)
    print(f"\nColumn: {col_choice}")
    print("-" * 40)
    print(f"{'Row':<5} {'Original':<15} {'Normalized':<15}")
    print("-" * 40)
    for i in range(min(5, len(rows))):
        print(f"{i+1:<5} {original_rows[i][idx]:<15} {rows[i][idx]:<15}")
    print("-" * 40)





