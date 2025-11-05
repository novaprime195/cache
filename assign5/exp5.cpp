#include <bits/stdc++.h>
using namespace std;

double median(vector<double> arr) {
    int n = arr.size();
    if (n == 0) return 0;
    sort(arr.begin(), arr.end());
    if (n % 2 == 0)
        return (arr[n/2 - 1] + arr[n/2]) / 2.0;
    else
        return arr[n/2];
}

void fiveNumberSummary(const string &colName, vector<double> data) {
    sort(data.begin(), data.end());
    int n = data.size();
    if (n == 0) {
        cout << "\nNo numeric data found in column: " << colName << endl;
        return;
    }

    double minimum = data.front();
    double maximum = data.back();
    double Q2 = median(data);

    vector<double> lower, upper;
    if (n % 2 == 0) {
        lower.assign(data.begin(), data.begin() + n/2);
        upper.assign(data.begin() + n/2, data.end());
    } else {
        lower.assign(data.begin(), data.begin() + n/2);
        upper.assign(data.begin() + n/2 + 1, data.end());
    }

    double Q1 = median(lower);
    double Q3 = median(upper);
    double IQR = Q3 - Q1;

    double lower_bound = Q1 - 1.5 * IQR;
    double upper_bound = Q3 + 1.5 * IQR;

    double lower_whisker = minimum;
    for (auto v : data) {
        if (v >= lower_bound) { lower_whisker = v; break; }
    }
    double upper_whisker = maximum;
    for (auto it = data.rbegin(); it != data.rend(); ++it) {
        if (*it <= upper_bound) { upper_whisker = *it; break; }
    }

    cout << "\n=== " << colName << " Summary ===\n";
    cout << "Minimum: " << minimum << endl;
    cout << "Q1: " << Q1 << endl;
    cout << "Median (Q2): " << Q2 << endl;
    cout << "Q3: " << Q3 << endl;
    cout << "Maximum: " << maximum << endl;
    cout << "IQR: " << IQR << endl;
    cout << "Lower Whisker: " << lower_whisker << endl;
    cout << "Upper Whisker: " << upper_whisker << endl;

    cout << "Outliers: ";
    bool foundOutlier = false;
    for (auto v : data) {
        if (v < lower_bound || v > upper_bound) {
            cout << v << " ";
            foundOutlier = true;
        }
    }
    if (!foundOutlier) cout << "None";
    cout << "\n";
}

int main() {
    ifstream file("student_scores.csv");
    if (!file.is_open()) {
        cout << "Error: Could not open dataset" << endl;
        return 1;
    }

    string line;
    vector<string> headers;
    vector<vector<double>> columns;

    if (getline(file, line)) {
        stringstream ss(line);
        string colName;
        while (getline(ss, colName, ',')) {
            headers.push_back(colName);
            columns.push_back({});
        }
    }

    while (getline(file, line)) {
        stringstream ss(line);
        string value;
        int colIndex = 0;
        while (getline(ss, value, ',')) {
            if (!value.empty()) {
                try {
                    columns[colIndex].push_back(stod(value));
                } catch (...) {}
            }
            colIndex++;
        }
    }
    file.close();

    for (size_t i = 0; i < headers.size(); i++) {
        if (!columns[i].empty()) {
            fiveNumberSummary(headers[i], columns[i]);
        }
    }

    return 0;
}
