#include <bits/stdc++.h>
using namespace std;

vector<string> splitCSVLine(const string &line) {
    vector<string> result;
    stringstream ss(line);
    string cell;
    while (getline(ss, cell, ',')) {
        result.push_back(cell);
    }
    return result;
}

double calculateCorrelation(const vector<double>& X, const vector<double>& Y) {
    int n = X.size();
    double meanX = accumulate(X.begin(), X.end(), 0.0) / n;
    double meanY = accumulate(Y.begin(), Y.end(), 0.0) / n;

    double numerator = 0.0, denomX = 0.0, denomY = 0.0;
    for (int i = 0; i < n; i++) {
        double dx = X[i] - meanX;
        double dy = Y[i] - meanY;
        numerator += dx * dy;
        denomX += dx * dx;
        denomY += dy * dy;
    }

    double denominator = sqrt(denomX * denomY);
    if (denominator == 0) return 0.0;
    return numerator / denominator;
}

int main() {
    string inputFilename = "Input.csv";
    ifstream file(inputFilename);
    if (!file.is_open()) {
        cout << "Error opening input file: " << inputFilename << endl;
        return 1;
    }

    string line;
    getline(file, line); 
    vector<string> header = splitCSVLine(line);

    vector<double> X, Y;
    while (getline(file, line)) {
        vector<string> row = splitCSVLine(line);
        if (row.size() >= 2) {
            X.push_back(stod(row[0]));
            Y.push_back(stod(row[1]));
        }
    }
    file.close();

    double r = calculateCorrelation(X, Y);
    string verdict;
    if (r == 0)
        verdict = "No correlation";
    else if (r > 0)
        verdict = "Positive correlation";
    else
        verdict = "Negative correlation";

    cout << "Correlation coefficient (r): " << r << endl;
    cout << "Type: " << verdict << endl;

    return 0;
}
