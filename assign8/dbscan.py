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

def compute_distance_matrix(points):
    n = len(points)
    matrix = [[0.0]*n for _ in range(n)]
    names = [p[0] for p in points]

    for i in range(n):
        for j in range(n):
            if i == j:
                matrix[i][j] = 0.0
            elif i < j:
                d = distance(points[i][1], points[j][1])
                matrix[i][j] = d
                matrix[j][i] = d

    print("\nDistance Matrix:")
    print("           " + " ".join(f"{name:10}" for name in names))  # header row
    for i, row in enumerate(matrix):
        print(f"{names[i]:10} " + " ".join(f"{x:<10.2f}" for x in row))
    return matrix

def region_query(points, point, eps):
    neighbors = []
    for idx, (_, p) in enumerate(points):
        if distance(p, point[1]) <= eps:
            neighbors.append(idx)
    return neighbors

def expand_cluster(points, clusters, cluster_id, point_idx, neighbors, eps, min_pts):
    clusters[point_idx] = cluster_id
    queue = list(neighbors)
    while queue:
        q_idx = queue.pop(0)
        if clusters[q_idx] == 0:  
            clusters[q_idx] = cluster_id
            q_neighbors = region_query(points, points[q_idx], eps)
            if len(q_neighbors) >= min_pts:
                queue.extend(q_neighbors)
        if clusters[q_idx] == -1:  
            clusters[q_idx] = cluster_id

def dbscan(points, eps, min_pts):
    n = len(points)
    clusters = [0] * n 
    cluster_id = 0
    for i in range(n):
        if clusters[i] != 0:
            continue
        neighbors = region_query(points, points[i], eps)
        if len(neighbors) < min_pts:
            clusters[i] = -1
        else:
            cluster_id += 1
            expand_cluster(points, clusters, cluster_id, i, neighbors, eps, min_pts)

    cluster_map = {}
    for idx, c_id in enumerate(clusters):
        if c_id > 0:
            if c_id not in cluster_map:
                cluster_map[c_id] = []
            cluster_map[c_id].append(points[idx])
    return list(cluster_map.values()), clusters


filename = input("Enter the CSV filename: ")
points = read_csv(filename)

compute_distance_matrix(points)

eps = float(input("\nEnter epsilon: "))
min_pts = int(input("Enter minPts: "))

clusters, cluster_assignments = dbscan(points, eps, min_pts)

total_points = len(points)
print("\nResults:")
for i, cluster in enumerate(clusters, start=1):
    percent = len(cluster) * 100.0 / total_points
    names = [p[0] for p in cluster]
    print(f"Cluster {i}: {len(cluster)} points ({percent:.2f}%) → {names}")

noise_points = [points[i][0] for i, c in enumerate(cluster_assignments) if c == -1]
if noise_points:
    noise_count = len(noise_points)
    noise_percent = noise_count * 100.0 / total_points
    print(f"Noise points: {noise_count} ({noise_percent:.2f}%) → {noise_points}")

print("\nCentroids:")
for i, cluster in enumerate(clusters, start=1):
    c = centroid(cluster)
    print(f"Centroid {i}: [{', '.join(f'{x:.2f}' for x in c)}]")
