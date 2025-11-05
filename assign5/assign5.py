# 5 no. summary
import csv

def median(v):
    v = sorted(v)
    n = len(v)
    if n % 2 == 0:
        return (v[n//2 - 1] + v[n//2]) / 2.0
    else:
        return v[n//2]

def five_number_summary(data):
    data = sorted(data)
    Q2 = median(data)
    n = len(data)

    if n % 2 == 0:
        lower_half = data[:n//2]
        upper_half = data[n//2:]
    else:
        lower_half = data[:n//2]
        upper_half = data[n//2+1:]

    Q1 = median(lower_half)
    Q3 = median(upper_half)
    IQR = Q3 - Q1
    lower_bound = Q1 - 1.5 * IQR
    upper_bound = Q3 + 1.5 * IQR
    lower_whisker = min([x for x in data if x >= lower_bound], default=data[0])
    upper_whisker = max([x for x in data if x <= upper_bound], default=data[-1])

    return {
        "Min": data[0],
        "Q1": Q1,
        "Median (Q2)": Q2,
        "Q3": Q3,
        "Max": data[-1],
        "IQR": IQR,
        "Lower Whisker": lower_whisker,
        "Upper Whisker": upper_whisker
    }

def is_float(val):
    try:
        float(val)
        return True
    except ValueError:
        return False


filename = input("Enter CSV file name: ")

with open(filename, "r") as file:
    reader = csv.DictReader(file)
    headers = reader.fieldnames

    columns = {h: [] for h in headers}
    for row in reader:
        for h in headers:
            if is_float(row[h]):
                columns[h].append(float(row[h]))

for col, values in columns.items():
    if values: 
        print(f"\nSummary for {col}:")
        summary = five_number_summary(values) 
        for k, v in summary.items():
            print(f"{k}: {v}")
