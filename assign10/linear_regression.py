n = int(input("Enter number of data points: "))
x = []
y = []
print("Enter data as: X Y (space-separated)")
for _ in range(n):
    a, b = map(float, input().split())
    x.append(a)
    y.append(b)

sum_x = sum(x)
sum_y = sum(y)
sum_xy = sum(x[i] * y[i] for i in range(n))
sum_x2 = sum(val ** 2 for val in x)

denominator = (n * sum_x2) - (sum_x ** 2)
b = ((n * sum_xy) - (sum_x * sum_y)) / denominator
a = ((sum_y * sum_x2) - (sum_x * sum_xy)) / denominator
if a >= 0:
    equation = f"Y = {b:.3f}X + {a:.3f}"
else:
    equation = f"Y = {b:.3f}X - {abs(a):.3f}"

print(f"\nLinear Regression Equation: {equation}")
print(f"Intercept (c): {a:.3f}")
print(f"Slope (m): {b:.3f}")