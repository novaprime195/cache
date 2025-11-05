#include <bits/stdc++.h>
using namespace std;

// ---------- Utility functions ----------

bool isNumeric(const string& str) {
    try {
        stod(str);
        return true;
    } catch (...) {
        return false;
    }
}

double mean(const vector<double>& values) {
    if (values.empty()) return 0;
    double sum = accumulate(values.begin(), values.end(), 0.0);
    return sum / values.size();
}

double stddev(const vector<double>& values, double mean) {
    if (values.empty()) return 0;
    double sum = 0;
    for (double v : values) sum += pow(v - mean, 2);
    return sqrt(sum / values.size());
}

double gaussianProb(double x, double mean, double sd) {
    if (sd == 0) return 1e-6; // avoid divide-by-zero
    return (1.0 / (sqrt(2 * M_PI) * sd)) * exp(-pow(x - mean, 2) / (2 * sd * sd));
}

// ---------- CSV Reader ----------
pair<vector<string>, vector<vector<string>>> readCSV(const string& filename) {
    ifstream file(filename);
    if (!file.is_open()) {
        cerr << "Error reading file: " << filename << endl;
        exit(1);
    }

    vector<string> headers;
    vector<vector<string>> data;
    string line;

    if (getline(file, line)) {
        stringstream ss(line);
        string col;
        while (getline(ss, col, ',')) headers.push_back(col);
    } else {
        cerr << "Empty file!" << endl;
        exit(1);
    }

    while (getline(file, line)) {
        stringstream ss(line);
        string value;
        vector<string> row;
        while (getline(ss, value, ',')) row.push_back(value);
        if (!row.empty()) data.push_back(row);
    }

    return {headers, data};
}

// ---------- Main ----------
int main() {
    string filename;
    cout << "Enter CSV file name (e.g., loan.csv): ";
    getline(cin, filename);

    auto [headers, data] = readCSV(filename);

    if (data.empty()) {
        cerr << "No data found in file.\n";
        return 1;
    }

    int n = data.size();
    int targetIndex = headers.size() - 1;

    // Get distinct class labels
    set<string> classes;
    for (auto& row : data) classes.insert(row[targetIndex]);

    // Get user input
    map<string, string> input;
    for (size_t i = 0; i < headers.size() - 1; ++i) {
        cout << "Enter " << headers[i] << ": ";
        string val;
        getline(cin, val);
        input[headers[i]] = val;
    }

    // Compute class probabilities
    map<string, double> classProb;

    for (auto& c : classes) {
        double countClass = 0;
        for (auto& row : data)
            if (row[targetIndex] == c) countClass++;

        double prior = countClass / n;
        double likelihood = 1.0;

        for (size_t i = 0; i < headers.size() - 1; ++i) {
            string feature = headers[i];
            string userVal = input[feature];
            bool numeric = isNumeric(data[0][i]);

            if (numeric) {
                vector<double> classValues;
                for (auto& row : data) {
                    if (row[targetIndex] == c)
                        classValues.push_back(stod(row[i]));
                }

                double m = mean(classValues);
                double s = stddev(classValues, m);
                double x = stod(userVal);
                double prob = gaussianProb(x, m, s);

                likelihood *= (prob == 0 ? 1e-6 : prob);
            } else {
                double match = 0;
                for (auto& row : data) {
                    if (row[targetIndex] == c && row[i] == userVal)
                        match++;
                }
                likelihood *= (match == 0 ? 1e-6 : match / countClass);
            }
        }

        classProb[c] = prior * likelihood;
    }

    // Display results
    cout << "\n--- Naive Bayes Classification ---\n";
    for (auto& kv : classProb)
        cout << "P(" << kv.first << "|X) = " << fixed << setprecision(8) << kv.second << endl;

    string predicted = max_element(classProb.begin(), classProb.end(),
                                   [](auto& a, auto& b) { return a.second < b.second; })
                           ->first;

    cout << "\nPredicted Class: " << predicted << endl;

    return 0;
}