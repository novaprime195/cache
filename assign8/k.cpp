#include <bits/stdc++.h>
using namespace std;

vector<vector<double>> readDataset(const string& filename) {
    ifstream file(filename);
    if (!file.is_open()) {
        cerr << "Error opening file " << filename << endl;
        exit(1);
    }
    vector<vector<double>> data;
    string line;
    while (getline(file, line)) {
        if (line.empty()) continue;
        stringstream ss(line);
        string token;
        vector<string> parts;
        while (getline(ss, token, ','))
            parts.push_back(token);
        for (auto &p : parts) {
            vector<double> row(1, stod(p));
            data.push_back(row);
        }
    }
    file.close();
    return data;
}

double dist(const vector<double>& a, const vector<double>& b) {
    double sum = 0;
    for (size_t i = 0; i < a.size(); i++)
        sum += pow(a[i] - b[i], 2);
    return sqrt(sum);
}

int main() {
    string filename = "kinput.csv";
    vector<vector<double>> points = readDataset(filename);
    int k;
    cout << "Enter number of clusters: ";
    cin >> k;
    if (k > (int)points.size()) {
        cerr << "Error: k cannot exceed number of points.\n";
        return 1;
    }
    vector<vector<double>> centroids(points.begin(), points.begin() + k);
    vector<int> cluster(points.size(), -1);
    bool changed = true;
    while (changed) {
        changed = false;
        for (int i = 0; i < (int)points.size(); i++) {
            double minDist = numeric_limits<double>::max();
            int best = -1;
            for (int c = 0; c < k; c++) {
                double d = dist(points[i], centroids[c]);
                if (d < minDist) {
                    minDist = d;
                    best = c;
                }
            }
            if (cluster[i] != best) {
                cluster[i] = best;
                changed = true;
            }
        }
        vector<vector<double>> newCentroids(k, vector<double>(points[0].size(), 0.0));
        vector<int> count(k, 0);
        for (int i = 0; i < (int)points.size(); i++) {
            for (size_t j = 0; j < points[i].size(); j++)
                newCentroids[cluster[i]][j] += points[i][j];
            count[cluster[i]]++;
        }
        for (int c = 0; c < k; c++) {
            if (count[c] > 0) {
                for (size_t j = 0; j < newCentroids[c].size(); j++)
                    newCentroids[c][j] /= count[c];
            }
        }
        centroids = newCentroids;
    }
    for (int c = 0; c < k; c++) {
        cout << "\nCluster " << c + 1 << " Centroid: ";
        for (size_t j = 0; j < centroids[c].size(); j++)
            cout << fixed << setprecision(2) << centroids[c][j];
        cout << "\nPoints: ";
        for (int i = 0; i < (int)points.size(); i++) {
            if (cluster[i] == c)
                cout << points[i][0] << " ";
        }
        cout << endl;
    }
    return 0;
}



// #include <iostream>
// #include <fstream>
// #include <sstream>
// #include <vector>
// #include <cmath>
// #include <limits>
// #include <string>
// #include <cstdlib>
// #include <iomanip>

// using namespace std;

// // Function to compute Euclidean distance
// double distance(const vector<double>& a, const vector<double>& b) {
//     double sum = 0.0;
//     for (size_t i = 0; i < a.size(); i++)
//         sum += pow(a[i] - b[i], 2);
//     return sqrt(sum);
// }

// // Read CSV file and extract numeric values
// vector<vector<double>> readCSV(const string& filename) {
//     vector<vector<double>> data;
//     ifstream file(filename);
//     if (!file.is_open()) {
//         cerr << "Error opening file: " << filename << endl;
//         return data;
//     }

//     string line;
//     while (getline(file, line)) {
//         if (line.empty()) continue;
//         stringstream ss(line);
//         string value;
//         vector<double> row;

//         while (getline(ss, value, ',')) {
//             try {
//                 row.push_back(stod(value));
//             } catch (...) {
//                 // ignore non-numeric values
//             }
//         }

//         if (!row.empty()) data.push_back(row);
//     }

//     file.close();
//     return data;
// }

// int main() {
//     string filename;
//     cout << "Enter CSV filename: ";
//     getline(cin, filename);

//     vector<vector<double>> data = readCSV(filename);
//     if (data.empty()) {
//         cout << "No valid data found in file!" << endl;
//         return 0;
//     }

//     cout << "Enter column indices (space separated, 0-based): ";
//     string colInput;
//     getline(cin, colInput);
//     stringstream ss(colInput);
//     vector<int> cols;
//     int temp;
//     while (ss >> temp)
//         cols.push_back(temp);

//     vector<vector<double>> points;
//     for (auto& row : data) {
//         vector<double> selected;
//         for (int idx : cols)
//             if (idx < (int)row.size())
//                 selected.push_back(row[idx]);
//         if (!selected.empty())
//             points.push_back(selected);
//     }

//     int k;
//     cout << "Enter number of clusters (k): ";
//     cin >> k;

//     if (k <= 0 || k > (int)points.size()) {
//         cout << "Invalid number of clusters." << endl;
//         return 0;
//     }

//     vector<vector<double>> centroids(points.begin(), points.begin() + k);
//     vector<int> labels(points.size(), -1);
//     bool changed = true;
//     int iteration = 0, maxIter = 100;

//     while (changed && iteration < maxIter) {
//         changed = false;
//         iteration++;

//         // Assign each point to nearest centroid
//         for (size_t i = 0; i < points.size(); i++) {
//             double minDist = numeric_limits<double>::max();
//             int cluster = -1;
//             for (int c = 0; c < k; c++) {
//                 double d = distance(points[i], centroids[c]);
//                 if (d < minDist) {
//                     minDist = d;
//                     cluster = c;
//                 }
//             }
//             if (labels[i] != cluster) {
//                 labels[i] = cluster;
//                 changed = true;
//             }
//         }

//         // Compute new centroids
//         vector<vector<double>> newCentroids(k, vector<double>(points[0].size(), 0.0));
//         vector<int> count(k, 0);

//         for (size_t i = 0; i < points.size(); i++) {
//             int c = labels[i];
//             for (size_t j = 0; j < points[i].size(); j++)
//                 newCentroids[c][j] += points[i][j];
//             count[c]++;
//         }

//         for (int c = 0; c < k; c++) {
//             if (count[c] > 0) {
//                 for (size_t j = 0; j < newCentroids[c].size(); j++)
//                     newCentroids[c][j] /= count[c];
//             }
//         }

//         centroids = newCentroids;
//     }

//     cout << fixed << setprecision(2);
//     cout << "\nFinal Centroids and Cluster Members:\n";
//     for (int c = 0; c < k; c++) {
//         cout << "C" << c + 1 << " (";
//         for (size_t j = 0; j < centroids[c].size(); j++) {
//             cout << centroids[c][j];
//             if (j < centroids[c].size() - 1) cout << ", ";
//         }
//         cout << ") -> ";

//         vector<string> members;
//         for (size_t i = 0; i < labels.size(); i++)
//             if (labels[i] == c)
//                 members.push_back("P" + to_string(i + 1));

//         for (size_t i = 0; i < members.size(); i++) {
//             cout << members[i];
//             if (i < members.size() - 1) cout << ", ";
//         }
//         cout << endl;
//     }

//     cout << "\nTotal Iterations: " << iteration << endl;
//     return 0;
// }
