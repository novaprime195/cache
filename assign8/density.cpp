#include <bits/stdc++.h>
using namespace std;

struct Point {
    vector<double> values;
    bool visited = false;
    int cluster = 0;

    Point(vector<double> vals) {
        values = vals;
    }

    double distance(const Point& other) const {
        double sum = 0.0;
        for (size_t i = 0; i < values.size(); i++)
            sum += pow(values[i] - other.values[i], 2);
        return sqrt(sum);
    }
};

vector<Point> readDataset(const string& filename) {
    vector<Point> points;
    ifstream file(filename);
    if (!file.is_open()) {
        cerr << "Error: Could not open file " << filename << endl;
        return points;
    }

    string line;
    bool firstLine = true;
    while (getline(file, line)) {
        if (firstLine) { 
            firstLine = false; 
            continue; 
        }
        if (line.empty()) continue;

        stringstream ss(line);
        string id, x, y;
        getline(ss, id, ',');  // skip Point ID
        getline(ss, x, ',');
        getline(ss, y, ',');

        points.emplace_back(vector<double>{stod(x), stod(y)});
    }

    file.close();
    return points;
}

vector<Point*> regionQuery(vector<Point>& dataset, Point& p, double eps) {
    vector<Point*> neighbors;
    for (auto& other : dataset) {
        if (p.distance(other) <= eps)
            neighbors.push_back(&other);
    }
    return neighbors;
}

void expandCluster(Point& p, vector<Point*>& neighbors, int clusterId,
                   vector<Point>& dataset, double eps, int minPts) {
    p.cluster = clusterId;
    queue<Point*> q;
    for (auto* n : neighbors) q.push(n);

    while (!q.empty()) {
        Point* current = q.front();
        q.pop();

        if (!current->visited) {
            current->visited = true;
            vector<Point*> newNeighbors = regionQuery(dataset, *current, eps);
            if ((int)newNeighbors.size() >= minPts) {
                for (auto* np : newNeighbors) q.push(np);
            }
        }

        if (current->cluster == 0)
            current->cluster = clusterId;
    }
}

void dbscan(vector<Point>& dataset, double eps, int minPts) {
    int clusterId = 0;
    for (auto& p : dataset) {
        if (!p.visited) {
            p.visited = true;
            vector<Point*> neighbors = regionQuery(dataset, p, eps);
            if ((int)neighbors.size() < minPts) {
                p.cluster = -1; // noise
            } else {
                clusterId++;
                expandCluster(p, neighbors, clusterId, dataset, eps, minPts);
            }
        }
    }
}

int main() {
    string filename = "input.csv"; 
    vector<Point> dataset = readDataset(filename);

    if (dataset.empty()) {
        cout << "Dataset not found or empty!" << endl;
        return 0;
    }

    double eps;
    int minPts;
    cout << "Enter epsilon (eps): ";
    cin >> eps;
    cout << "Enter minimum points (minPts): ";
    cin >> minPts;

    dbscan(dataset, eps, minPts);

    unordered_map<int, int> clusterCount;
    for (auto& p : dataset)
        clusterCount[p.cluster]++;

    int total = dataset.size();
    cout << "\nCluster Percentage Distribution:\n";
    for (auto& kv : clusterCount) {
        string name = (kv.first == -1) ? "Noise" : "Cluster " + to_string(kv.first);
        double percent = (kv.second * 100.0) / total;
        cout << name << ": " << kv.second << " points (" << fixed << setprecision(2) << percent << "%)\n";
    }

    return 0;
}



// #include <bits/stdc++.h>
// using namespace std;

// struct Point {
//     string name;
//     vector<double> coords;
//     Point(string n, vector<double> c) : name(move(n)), coords(move(c)) {}
// };

// double distance(const vector<double>& a, const vector<double>& b) {
//     double sum = 0.0;
//     for (size_t i = 0; i < a.size(); i++)
//         sum += pow(a[i] - b[i], 2);
//     return sqrt(sum);
// }

// vector<Point> readCSV(const string& filename) {
//     vector<Point> points;
//     ifstream file(filename);
//     if (!file.is_open()) {
//         cerr << "Error: Unable to open file.\n";
//         return points;
//     }

//     string line;
//     bool headerSkipped = false;
//     while (getline(file, line)) {
//         if (line.empty()) continue;
//         stringstream ss(line);
//         string token;
//         vector<string> vals;
//         while (getline(ss, token, ','))
//             vals.push_back(token);

//         // Skip header line
//         if (!headerSkipped && vals[0].find("point") != string::npos) {
//             headerSkipped = true;
//             continue;
//         }

//         try {
//             string name = vals[0];
//             vector<double> coords;
//             for (size_t i = 1; i < vals.size(); i++)
//                 coords.push_back(stod(vals[i]));
//             points.emplace_back(name, coords);
//         } catch (...) {
//             continue;
//         }
//     }
//     file.close();
//     return points;
// }

// vector<vector<double>> computeDistanceMatrix(const vector<Point>& points) {
//     int n = points.size();
//     vector<vector<double>> matrix(n, vector<double>(n, 0.0));

//     cout << "\nDistance Matrix:\n          ";
//     for (auto& p : points) cout << setw(10) << left << p.name;
//     cout << "\n";

//     for (int i = 0; i < n; i++) {
//         cout << setw(10) << left << points[i].name;
//         for (int j = 0; j < n; j++) {
//             if (i == j) matrix[i][j] = 0.0;
//             else if (i < j)
//                 matrix[i][j] = matrix[j][i] = distance(points[i].coords, points[j].coords);
//             cout << setw(10) << left << fixed << setprecision(2) << matrix[i][j];
//         }
//         cout << "\n";
//     }
//     return matrix;
// }

// vector<int> regionQuery(const vector<Point>& points, const Point& point, double eps) {
//     vector<int> neighbors;
//     for (int i = 0; i < points.size(); i++)
//         if (distance(points[i].coords, point.coords) <= eps)
//             neighbors.push_back(i);
//     return neighbors;
// }

// void expandCluster(vector<Point>& points, vector<int>& clusters, int clusterId, int pointIdx,
//                    const vector<int>& neighbors, double eps, int minPts) {
//     clusters[pointIdx] = clusterId;
//     queue<int> q;
//     for (int idx : neighbors) q.push(idx);

//     while (!q.empty()) {
//         int qIdx = q.front(); q.pop();
//         if (clusters[qIdx] == 0) {
//             clusters[qIdx] = clusterId;
//             vector<int> qNeighbors = regionQuery(points, points[qIdx], eps);
//             if (qNeighbors.size() >= minPts)
//                 for (int n : qNeighbors) q.push(n);
//         }
//         if (clusters[qIdx] == -1)
//             clusters[qIdx] = clusterId;
//     }
// }

// vector<double> centroid(const vector<Point>& cluster) {
//     int dims = cluster[0].coords.size();
//     vector<double> mean(dims, 0.0);
//     for (auto& p : cluster)
//         for (int i = 0; i < dims; i++)
//             mean[i] += p.coords[i];
//     for (int i = 0; i < dims; i++)
//         mean[i] /= cluster.size();
//     return mean;
// }

// map<int, vector<Point>> dbscan(vector<Point>& points, double eps, int minPts) {
//     int n = points.size();
//     vector<int> clusters(n, 0);
//     int clusterId = 0;

//     for (int i = 0; i < n; i++) {
//         if (clusters[i] != 0) continue;

//         vector<int> neighbors = regionQuery(points, points[i], eps);
//         if (neighbors.size() < minPts)
//             clusters[i] = -1; // noise
//         else {
//             clusterId++;
//             expandCluster(points, clusters, clusterId, i, neighbors, eps, minPts);
//         }
//     }

//     map<int, vector<Point>> clusterMap;
//     for (int i = 0; i < n; i++) {
//         if (clusters[i] > 0)
//             clusterMap[clusters[i]].push_back(points[i]);
//     }

//     cout << "\nResults:\n";
//     int total = n;
//     for (auto& [id, cluster] : clusterMap) {
//         double percent = (cluster.size() * 100.0) / total;
//         cout << "Cluster " << id << ": " << cluster.size() << " points (" 
//              << fixed << setprecision(2) << percent << "%) → ";
//         for (auto& p : cluster) cout << p.name << " ";
//         cout << "\n";
//     }

//     vector<string> noise;
//     for (int i = 0; i < n; i++)
//         if (clusters[i] == -1)
//             noise.push_back(points[i].name);

//     if (!noise.empty()) {
//         double percent = (noise.size() * 100.0) / total;
//         cout << "Noise points: " << noise.size() << " (" << percent << "%) → ";
//         for (auto& name : noise) cout << name << " ";
//         cout << "\n";
//     }

//     cout << "\nCentroids:\n";
//     for (auto& [id, cluster] : clusterMap) {
//         vector<double> mean = centroid(cluster);
//         cout << "Centroid " << id << ": [";
//         for (int i = 0; i < mean.size(); i++) {
//             cout << fixed << setprecision(2) << mean[i];
//             if (i < mean.size() - 1) cout << ", ";
//         }
//         cout << "]\n";
//     }

//     return clusterMap;
// }

// int main() {
//     string filename;
//     cout << "Enter CSV filename: ";
//     getline(cin, filename);

//     vector<Point> points = readCSV(filename);
//     if (points.empty()) {
//         cerr << "No valid points loaded.\n";
//         return 1;
//     }

//     computeDistanceMatrix(points);

//     double eps;
//     int minPts;
//     cout << "\nEnter epsilon: ";
//     cin >> eps;
//     cout << "Enter minPts: ";
//     cin >> minPts;

//     dbscan(points, eps, minPts);
//     return 0;
// }
