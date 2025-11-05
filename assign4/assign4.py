import csv
import sys

def read_csv(file):
    try:
        with open(file, 'r', encoding='utf-8') as f:
            reader = csv.DictReader(f)
            rows = [r for r in reader]
            header = reader.fieldnames
        return header, rows
    except FileNotFoundError:
        print(" File not found.")
        sys.exit()
    except Exception as e:
        print(f" Error reading CSV: {e}")
        sys.exit()


def main():
    file = input("Enter input CSV path: ").strip()
    header, rows = read_csv(file)

    print("\n Columns available:", ", ".join(header))

    row_field = input(f"Enter row field (e.g., {header[0]}): ").strip()
    col_field = input(f"Enter column field (e.g., {header[1]}): ").strip()
    val_field = input(f"Enter numeric value field (e.g., {header[-1]}): ").strip()

    if row_field not in header or col_field not in header or val_field not in header:
        print("Invalid column name(s).")
        sys.exit()

    data = {}
    col_values = []

    for r in rows:
        try:
            row_val = r[row_field].strip()
            col_val = r[col_field].strip()
            val = float(r[val_field])
        except ValueError:
            print(f"Non-numeric value in '{val_field}', skipping row.")
            continue

        data.setdefault(row_val, {})[col_val] = val
        if col_val not in col_values:
            col_values.append(col_val)

    if not data:
        print("No valid numeric data found.")
        sys.exit()

    # Compute totals
    row_totals = {r: sum(data[r].values()) for r in data}
    col_totals = {}
    for r in data:
        for c in data[r]:
            col_totals[c] = col_totals.get(c, 0) + data[r][c]
    grand_total = sum(col_totals.values())

    # Print formatted table
    col_width = 22
    print("\n" + " " * 8, end="")
    for c in col_values:
        print(f"{c:^{col_width}}", end="")
    print("Total")
    print("-" * (8 + len(col_values) * col_width + 10))

    for r in data:
        print(r.ljust(8), end="")
        for c in col_values:
            v = data[r].get(c, 0)
            row_total = row_totals[r]
            col_total = col_totals[c]
            dwt = (v / row_total * 100) if row_total else 0
            twt = (v / col_total * 100) if col_total else 0
            cell = f"{v:7.2f} {twt:6.2f}% {dwt:6.2f}%"
            print(f"{cell:>{col_width}}", end="")
        print(f"{row_totals[r]:7.2f} 100.00%")

    print("-" * (8 + len(col_values) * col_width + 10))

    print("Total".ljust(8), end="")
    for c in col_values:
        cell = f"{col_totals[c]:7.2f} 100.00%"
        print(f"{cell:^{col_width}}", end="")
    print(f"{grand_total:7.2f}")

    print("-" * (8 + len(col_values) * col_width + 10))


if __name__ == "__main__":
    main()