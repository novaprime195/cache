#correlation coefficient
def mean(values):
    return sum(values) / len(values)

def pearson_correlation(x, y):
    if len(x) != len(y):
        raise ValueError("Can't calculate as len(x) != len(y)")

    mean_x = mean(x)
    mean_y = mean(y)

    numerator = sum((a - mean_x) * (b - mean_y) for a, b in zip(x, y))
    denominator_x = sum((a - mean_x) ** 2 for a in x)
    denominator_y = sum((b - mean_y) ** 2 for b in y)

    denominator = (denominator_x * denominator_y) ** 0.5
    if denominator == 0:
        return 0  

    return numerator / denominator

if __name__ == "__main__":
    n = int(input("Enter number of data points: "))

    print("Enter values for X:")
    x = list(map(float, input().split()))
    print("Enter values for Y:")
    y = list(map(float, input().split()))

    if len(x) != n or len(y) != n:
        print("Error!")
    else:
        r = pearson_correlation(x, y)
        print(f"\nPearson correlation coefficient (r): {r:.4f}")

        if r == 1:
            print("Perfect positive correlation")
        elif r == -1:
            print("Perfect negative correlation")
        elif r > 0:
            print("Positive correlation")
        elif r < 0:
            print("Negative correlation")
        else:
            print("No correlation")
