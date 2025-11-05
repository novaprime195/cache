import math
import random

def dist(a, b):
    return math.sqrt(sum((a[i] - b[i])**2 for i in range(len(a))))

def read_csv(filename):
    points = []
    with open(filename, "r") as f:
        for line in f:
            parts = line.strip().split(",")
            values = []
            for p in parts:
                try:
                    values.append(float(p))
                except:
                    pass
            if values:
                points.append(values)
    return points

filename = input("Enter the CSV filename: ")
points = read_csv(filename)
if not points:
    print("No valid data found in file!")
    exit()

columns_input = input("Enter column indices: ")
columns = list(map(int, columns_input.strip().split()))
points = [[p[c] for c in columns] for p in points]

k = int(input("Enter the number of clusters (k): "))
if k <= 0 or k > len(points):
    print("Invalid number of clusters.")
    exit()

centroids = random.sample(points, k)
labels = [-1] * len(points)
changed = True
max_iter = 100
iteration = 0

while changed and iteration < max_iter:
    iteration += 1
    changed = False
    for i, p in enumerate(points):
        min_dist = float("inf")
        best_cluster = -1
        for c in range(k):
            d = dist(p, centroids[c])
            if d < min_dist:
                min_dist = d
                best_cluster = c
        if labels[i] != best_cluster:
            labels[i] = best_cluster
            changed = True

    new_centroids = [[0.0]*len(points[0]) for _ in range(k)]
    count = [0]*k

    for i, p in enumerate(points):
        cluster = labels[i]
        count[cluster] += 1
        for j in range(len(p)):
            new_centroids[cluster][j] += p[j]

    for c in range(k):
        if count[c] > 0:
            for j in range(len(new_centroids[c])):
                new_centroids[c][j] /= count[c]
            centroids[c] = new_centroids[c]

print("\nResults :")
for c in range(k):
    cluster_size = sum(1 for label in labels if label == c)
    percentage = cluster_size * 100.0 / len(points)
    cluster_points = [chr(65+i) for i, label in enumerate(labels) if label == c]
    print(f"Cluster {c+1}: {cluster_size} points ({percentage:.2f}%) → {cluster_points}")

print("\nUpdated Centroids:")
for c in range(k):
    print(f"Centroid {c+1}: [{', '.join(f'{x:.2f}' for x in centroids[c])}]")
