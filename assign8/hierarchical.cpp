#include <bits/stdc++.h>
using namespace std;

struct Cluster {
    vector<string> items;
    Cluster() {}
    Cluster(const string& name) { items.push_back(name); }
};
double getDistance(const map<pair<string, string>, double>& distMap,
                   const string& a, const string& b) {
    if (a == b) return 0;
    auto it = distMap.find({a, b});
    if (it != distMap.end()) return it->second;
    return distMap.at({b, a});
}
double clusterDistance(const Cluster& c1, const Cluster& c2,
                       const map<pair<string, string>, double>& distMap,
                       const string& method) {
    double minD = DBL_MAX, maxD = 0, sum = 0;
    int count = 0;

    for (auto& i : c1.items)
        for (auto& j : c2.items) {
            double d = getDistance(distMap, i, j);
            minD = min(minD, d);
            maxD = max(maxD, d);
            sum += d;
            count++;
        }

    if (method == "single") return minD;
    if (method == "complete") return maxD;
    return sum / count; // average
}
void hierarchicalClustering(const vector<string>& labels,
                            const map<pair<string, string>, double>& distMap,
                            const string& method) {
    vector<Cluster> clusters;
    for (auto& l : labels)
        clusters.push_back(Cluster(l));

    cout << "\n" << method << " LINKAGE CLUSTERING:\n";

    while (clusters.size() > 1) {
        double minDist = DBL_MAX;
        int a = -1, b = -1;

        for (int i = 0; i < clusters.size(); i++) {
            for (int j = i + 1; j < clusters.size(); j++) {
                double d = clusterDistance(clusters[i], clusters[j], distMap, method);
                if (d < minDist) {
                    minDist = d;
                    a = i;
                    b = j;
                }
            }
        }

        cout << "Merging (";
        for (auto& x : clusters[a].items) cout << x;
        cout << ") and (";
        for (auto& y : clusters[b].items) cout << y;
        cout << ") at distance = " << minDist << endl;

        Cluster merged;
        merged.items.insert(merged.items.end(),
                            clusters[a].items.begin(), clusters[a].items.end());
        merged.items.insert(merged.items.end(),
                            clusters[b].items.begin(), clusters[b].items.end());

        if (b > a) {
            clusters.erase(clusters.begin() + b);
            clusters.erase(clusters.begin() + a);
        } else {
            clusters.erase(clusters.begin() + a);
            clusters.erase(clusters.begin() + b);
        }

        clusters.push_back(merged);
    }
}
int main() {
    string filename = "input.csv";
    ifstream file(filename);
    if (!file.is_open()) {
        cout << "Error opening " << filename << endl;
        return 1;
    }
    string line;
    getline(file, line);
    vector<string> headers;
    stringstream ss(line);
    string val;
    getline(ss, val, ',');
    while (getline(ss, val, ','))
        headers.push_back(val);
    map<pair<string, string>, double> distMap;
    while (getline(file, line)) {
        stringstream ss(line);
        string rowLabel;
        getline(ss, rowLabel, ',');
        for (int i = 0; i < headers.size(); i++) {
            string cell;
            getline(ss, cell, ',');
            double d = stod(cell);
            distMap[{rowLabel, headers[i]}] = d;
        }
    }
    file.close();

    vector<string> linkages = {"single", "average", "complete"};
    for (auto& method : linkages)
        hierarchicalClustering(headers, distMap, method);

    return 0;
}



// #include <iostream>
// #include <fstream>
// #include <sstream>
// #include <vector>
// #include <cmath>
// #include <string>
// #include <algorithm>
// #include <iomanip>
// #include <limits>

// using namespace std;

// struct Point {
//     string name;
//     vector<double> coords;
// };

// // Compute Euclidean distance
// double distance(const Point& a, const Point& b) {
//     double sum = 0.0;
//     for (size_t i = 0; i < a.coords.size(); i++) {
//         double diff = a.coords[i] - b.coords[i];
//         sum += diff * diff;
//     }
//     return sqrt(sum);
// }

// // Compute centroid of a cluster
// vector<double> centroid(const vector<Point>& cluster) {
//     int dims = cluster[0].coords.size();
//     vector<double> mean(dims, 0.0);
//     for (const auto& p : cluster)
//         for (int i = 0; i < dims; i++)
//             mean[i] += p.coords[i];
//     for (int i = 0; i < dims; i++)
//         mean[i] /= cluster.size();
//     return mean;
// }

// // Read CSV file into a list of points
// vector<Point> readCSV(const string& filename) {
//     vector<Point> points;
//     ifstream file(filename);
//     if (!file.is_open()) {
//         cerr << "Error opening file: " << filename << endl;
//         return points;
//     }

//     string line;
//     bool headerSkipped = false;
//     while (getline(file, line)) {
//         if (line.empty()) continue;
//         stringstream ss(line);
//         string cell;
//         vector<string> vals;
//         while (getline(ss, cell, ',')) vals.push_back(cell);

//         // Skip header line
//         if (!headerSkipped) {
//             bool header = false;
//             for (auto& v : vals)
//                 if (v.find("id") != string::npos || v.find("ID") != string::npos)
//                     header = true;
//             if (header) {
//                 headerSkipped = true;
//                 continue;
//             }
//         }

//         try {
//             string name = vals[0];
//             vector<double> coords;
//             for (size_t i = 1; i < vals.size(); i++)
//                 coords.push_back(stod(vals[i]));
//             points.push_back({name, coords});
//         } catch (...) {
//             // skip invalid rows
//         }
//     }
//     file.close();
//     return points;
// }

// // Compute linkage distance between two clusters
// double linkageDistance(const vector<Point>& A, const vector<Point>& B, const string& method) {
//     if (method == "single") {
//         double dist = numeric_limits<double>::max();
//         for (const auto& a : A)
//             for (const auto& b : B)
//                 dist = min(dist, distance(a, b));
//         return dist;
//     } else if (method == "complete") {
//         double dist = 0.0;
//         for (const auto& a : A)
//             for (const auto& b : B)
//                 dist = max(dist, distance(a, b));
//         return dist;
//     } else { // average
//         double total = 0.0;
//         int count = 0;
//         for (const auto& a : A)
//             for (const auto& b : B) {
//                 total += distance(a, b);
//                 count++;
//             }
//         return total / count;
//     }
// }

// // Print distance matrix between clusters
// void printDistanceMatrix(const vector<vector<Point>>& clusters, const string& method) {
//     int n = clusters.size();
//     vector<vector<double>> matrix(n, vector<double>(n, 0.0));

//     vector<string> labels;
//     for (const auto& cl : clusters) {
//         string lbl;
//         for (size_t i = 0; i < cl.size(); i++) {
//             lbl += cl[i].name;
//             if (i < cl.size() - 1) lbl += ",";
//         }
//         labels.push_back(lbl);
//     }

//     for (int i = 0; i < n; i++) {
//         for (int j = i + 1; j < n; j++) {
//             double d = linkageDistance(clusters[i], clusters[j], method);
//             matrix[i][j] = matrix[j][i] = d;
//         }
//     }

//     cout << "\nDistance Matrix:\n";
//     cout << setw(15) << " ";
//     for (const auto& lbl : labels)
//         cout << setw(15) << lbl;
//     cout << endl;

//     for (int i = 0; i < n; i++) {
//         cout << setw(15) << labels[i];
//         for (int j = 0; j < n; j++)
//             cout << setw(15) << fixed << setprecision(2) << matrix[i][j];
//         cout << endl;
//     }
// }

// // Print current clusters and their centroids
// void printClusters(const vector<vector<Point>>& clusters) {
//     cout << "\nCurrent Clusters and Centroids:\n";
//     int idx = 1;
//     for (const auto& cluster : clusters) {
//         cout << "Cluster " << idx++ << ": ";
//         vector<string> names;
//         for (const auto& p : cluster)
//             names.push_back(p.name);

//         cout << "[";
//         for (size_t i = 0; i < names.size(); i++) {
//             cout << names[i];
//             if (i < names.size() - 1) cout << ", ";
//         }
//         cout << "]";

//         auto c = centroid(cluster);
//         cout << ", Centroid = [";
//         for (size_t i = 0; i < c.size(); i++) {
//             cout << fixed << setprecision(2) << c[i];
//             if (i < c.size() - 1) cout << ", ";
//         }
//         cout << "]" << endl;
//     }
// }

// // Perform hierarchical clustering
// void hierarchicalCluster(vector<Point> points, const string& method) {
//     vector<vector<Point>> clusters;
//     for (auto& p : points)
//         clusters.push_back({p});

//     int iteration = 1;
//     while (clusters.size() > 1) {
//         cout << "\n--- Iteration " << iteration << " ---";
//         printClusters(clusters);
//         printDistanceMatrix(clusters, method);

//         double minDist = numeric_limits<double>::max();
//         int mergeA = -1, mergeB = -1;

//         for (size_t i = 0; i < clusters.size(); i++) {
//             for (size_t j = i + 1; j < clusters.size(); j++) {
//                 double d = linkageDistance(clusters[i], clusters[j], method);
//                 if (d < minDist) {
//                     minDist = d;
//                     mergeA = i;
//                     mergeB = j;
//                 }
//             }
//         }

//         cout << "\nMerging clusters " << mergeA + 1 << " and " << mergeB + 1
//              << " (distance=" << fixed << setprecision(2) << minDist << ")\n";

//         clusters[mergeA].insert(clusters[mergeA].end(), clusters[mergeB].begin(), clusters[mergeB].end());
//         clusters.erase(clusters.begin() + mergeB);

//         iteration++;
//     }

//     cout << "\n--- Final Cluster ---\n";
//     auto& finalCluster = clusters[0];
//     vector<string> finalNames;
//     for (auto& p : finalCluster)
//         finalNames.push_back(p.name);

//     cout << "Final Cluster: [";
//     for (size_t i = 0; i < finalNames.size(); i++) {
//         cout << finalNames[i];
//         if (i < finalNames.size() - 1) cout << ", ";
//     }
//     cout << "]\n";

//     auto finalCentroid = centroid(finalCluster);
//     cout << "Centroid: [";
//     for (size_t i = 0; i < finalCentroid.size(); i++) {
//         cout << fixed << setprecision(2) << finalCentroid[i];
//         if (i < finalCentroid.size() - 1) cout << ", ";
//     }
//     cout << "]\n";
// }

// int main() {
//     string filename;
//     cout << "Enter the CSV filename: ";
//     getline(cin, filename);

//     vector<Point> allPoints = readCSV(filename);
//     if (allPoints.empty()) {
//         cout << "No valid data found!" << endl;
//         return 0;
//     }

//     cout << "Enter column indices to use (excluding name column): ";
//     string colLine;
//     getline(cin, colLine);
//     stringstream ss(colLine);
//     vector<int> cols;
//     int val;
//     while (ss >> val) cols.push_back(val);

//     vector<Point> points;
//     for (const auto& p : allPoints) {
//         vector<double> selected;
//         for (int idx : cols)
//             if (idx >= 0 && idx < (int)p.coords.size())
//                 selected.push_back(p.coords[idx]);
//         points.push_back({p.name, selected});
//     }

//     cout << "Enter linkage method (single / average / complete): ";
//     string method;
//     getline(cin, method);
//     transform(method.begin(), method.end(), method.begin(), ::tolower);

//     hierarchicalCluster(points, method);
//     return 0;
// }
