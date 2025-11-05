#OLAP
import csv

def read_csv(filename):
    records = []
    header = []
    try:
        with open(filename, 'r') as file:
            reader = csv.DictReader(file) 
            header = reader.fieldnames
            for row in reader:
                records.append(row)
    except FileNotFoundError:
        print("File not found!")
    return header, records


def print_record(r, header):
    print(" | ".join(str(r[h]) for h in header))


def slice_operation(data, field, value, header):
    print(f"\nSlice: {field} = {value}")
    for r in data:
        if r[field].lower() == value.lower():
            print_record(r, header)


def dice_operation(data, filters, header):
    print("\nDice")
    for r in data:
        match = all(r[key].lower() == value.lower() for key, value in filters.items())
        if match:
            print_record(r, header)


def rollup_operation(data, group_field, numeric_field):
    print(f"\nRoll up: (Total {numeric_field} by {group_field})")
    totals = {}
    for r in data:
        try:
            val = float(r[numeric_field])
        except ValueError:
            print(f"Column '{numeric_field}' is not numeric.")
            return
        key = r[group_field]
        totals[key] = totals.get(key, 0) + val
    for key, total in totals.items():
        print(f"{key} → {total}")


def drilldown_operation(data, header):
    print("\nDrill-down")
    for r in data:
        print_record(r, header)


def pivot_operation(data, row_field, col_field, numeric_field):
    print(f"\nPivot: {row_field} vs {col_field} (values={numeric_field})")
    pivot = {}
    col_values = set()

    for r in data:
        try:
            sales = float(r[numeric_field])
        except ValueError:
            print(f"Column '{numeric_field}' is not numeric.")
            return
        row = r[row_field]
        col = r[col_field]
        col_values.add(col)

        if row not in pivot:
            pivot[row] = {}
        pivot[row][col] = pivot[row].get(col, 0) + sales

    col_values = sorted(col_values)
    print(row_field, *col_values, sep='\t')
    for row in pivot:
        print(row, end='\t')
        for col in col_values:
            print(pivot[row].get(col, 0), end='\t')
        print()


def main():
    filename = input("Enter CSV file name: ")
    header, data = read_csv(filename)

    if not data:
        print("No data loaded")
        return

    print("\nLoaded columns:", ", ".join(header))

    while True:
        print("\n* OLAP Menu *")
        print("1. Original Data")
        print("2. Slice")
        print("3. Dice")
        print("4. Roll-Up")
        print("5. Drill-Down")
        print("6. Pivot")
        print("0. Exit")

        choice = input("Enter your choice: ")

        if choice == '1':
            print("\nOriginal Data ")
            for r in data:
                print_record(r, header)

        elif choice == '2':
            field = input(f"Enter field to slice by {header}: ")
            value = input(f"Enter value for {field}: ")
            if field not in header:
                print("Invalid field")
            else:
                slice_operation(data, field, value, header)

        elif choice == '3':
            num_filters = int(input("How many filters? "))
            filters = {}
            for _ in range(num_filters):
                field = input(f"Enter field name {header}: ")
                value = input(f"Enter value for {field}: ")
                if field not in header:
                    print("Invalid field")
                    continue
                filters[field] = value
            dice_operation(data, filters, header)

        elif choice == '4':
            group_field = input(f"Enter field to group by {header}: ")
            numeric_field = input(f"Enter numeric field to aggregate {header}: ")
            rollup_operation(data, group_field, numeric_field)

        elif choice == '5':
            drilldown_operation(data, header)

        elif choice == '6':
            row_field = input(f"Enter row field {header}: ")
            col_field = input(f"Enter column field {header}: ")
            numeric_field = input(f"Enter numeric field to aggregate {header}: ")
            pivot_operation(data, row_field, col_field, numeric_field)

        elif choice == '0':
            print("Exiting...")
            break

        else:
            print("Invalid choice")


if __name__ == "__main__":
    main()
