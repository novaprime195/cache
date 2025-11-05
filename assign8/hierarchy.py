import math

def distance(a, b):
    return math.sqrt(sum((x - y) ** 2 for x, y in zip(a, b)))

def centroid(cluster):
    dims = len(cluster[0][1]) 
    mean = [0.0] * dims
    for _, p in cluster:
        for i in range(dims):
            mean[i] += p[i]
    mean = [x / len(cluster) for x in mean]
    return mean

def read_csv(filename):
    points = []
    with open(filename, "r") as f:
        header_skipped = False
        for line in f:
            line = line.strip()
            if not line:
                continue
            vals = line.split(",")
            if not header_skipped and any("id" in v.lower() for v in vals):
                header_skipped = True
                continue
            try:
                name = vals[0] 
                coords = [float(v) for v in vals[1:]]
                points.append((name, coords))
            except:
                continue
    return points

def linkage_distance(clusterA, clusterB, method):
    if method == "single":
        return min(distance(a[1], b[1]) for a in clusterA for b in clusterB)
    elif method == "complete":
        return max(distance(a[1], b[1]) for a in clusterA for b in clusterB)
    else:  
        total = sum(distance(a[1], b[1]) for a in clusterA for b in clusterB)
        count = len(clusterA) * len(clusterB)
        return total / count

def print_distance_matrix(clusters, method):
    n = len(clusters)
    matrix = [[0.0]*n for _ in range(n)]
    labels = [",".join(p[0] for p in cluster) for cluster in clusters]

    for i in range(n):
        for j in range(n):
            if i == j:
                matrix[i][j] = 0.0
            elif i < j:
                d = linkage_distance(clusters[i], clusters[j], method)
                matrix[i][j] = d
                matrix[j][i] = d

    print("\nDistance Matrix:")
    print("           " + " ".join(f"{lbl:10}" for lbl in labels))
    for i, row in enumerate(matrix):
        print(f"{labels[i]:10} " + " ".join(f"{x:<10.2f}" for x in row))
    return matrix

def print_clusters(clusters):
    print("\nCurrent Clusters and Centroids:")
    for i, cluster in enumerate(clusters):
        names = [p[0] for p in cluster]
        c = centroid(cluster)
        print(f"Cluster {i+1}: {len(cluster)} points {names}, Centroid = [{', '.join(f'{x:.2f}' for x in c)}]")

def hierarchical_cluster(points, method):
    clusters = [[p] for p in points]
    iteration = 1
    while len(clusters) > 1:
        print(f"\n--- Iteration {iteration} ---")
        print_clusters(clusters)
        print_distance_matrix(clusters, method)

        min_dist = float("inf")
        mergeA, mergeB = -1, -1
        for i in range(len(clusters)):
            for j in range(i + 1, len(clusters)):
                d = linkage_distance(clusters[i], clusters[j], method)
                if d < min_dist:
                    min_dist = d
                    mergeA, mergeB = i, j

        print(f"\nMerging clusters {mergeA+1} and {mergeB+1} (distance={min_dist:.2f})")
        clusters[mergeA].extend(clusters[mergeB])
        clusters.pop(mergeB)
        iteration += 1

    print("\n--- Final Cluster ---")
    final_cluster = clusters[0]
    final_names = [p[0] for p in final_cluster]
    final_centroid = centroid(final_cluster)
    print(f"Final Cluster: {len(final_cluster)} points {final_names}")
    print(f"Centroid: [{', '.join(f'{x:.2f}' for x in final_centroid)}]")


filename = input("Enter the CSV filename: ")
all_points = read_csv(filename)
cols_input = input("Enter column indices to use (excluding name column): ")
cols = list(map(int, cols_input.strip().split()))
points = [(name, [coords[i] for i in cols]) for name, coords in all_points]
method = input("Enter linkage method (single / average / complete): ").strip().lower()
hierarchical_cluster(points, method)
