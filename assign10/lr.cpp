#include <bits/stdc++.h>
using namespace std;

int main() {
    string filename = "input.csv";
    ifstream file(filename);
    if (!file.is_open()) {
        cerr << "Error: Cannot open file " << filename << endl;
        return 1;
    }
    string line;
    getline(file, line); 
    vector<double> x, y;
    while (getline(file, line)) {
        stringstream ss(line);
        string val1, val2;
        getline(ss, val1, ',');
        getline(ss, val2, ',');
        if (!val1.empty() && !val2.empty()) {
            x.push_back(stod(val1));
            y.push_back(stod(val2));
        }
    }
    file.close();
    int n = x.size();
    double sumx = 0, sumy = 0, sumxy = 0, sumx2 = 0;
    for (int i = 0; i < n; i++) {
        sumx += x[i];
        sumy += y[i];
        sumxy += x[i] * y[i];
        sumx2 += x[i] * x[i];
    }

    double m = (n * sumxy - sumx * sumy) / (n * sumx2 - sumx * sumx);
    double c = (sumy * sumx2 - sumx * sumxy) / (n * sumx2 - sumx * sumx);
    cout << fixed << setprecision(3);
    cout << "Sum(x) = " << sumx << endl;
    cout << "Sum(y) = " << sumy << endl;
    cout << "Sum(x^2) = " << sumx2 << endl;
    cout << "Sum(xy) = " << sumxy << endl << endl;
    cout << "Slope (m) = " << m << endl;
    cout << "Intercept (c) = " << c << endl;
    cout << "\nEquation of line: y = " << m << "x + " << c << endl;
    return 0;
}
