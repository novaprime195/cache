#include <bits/stdc++.h>
using namespace std;

bool isFloat(const string &s) {
    try {
        stod(s);
        return true;
    } catch (...) {
        return false;
    }
}

// Function to normalize column by method
vector<double> normalizeColumn(const vector<double>& col, int method) {
    vector<double> normCol;
    if (method == 1) { // Min-Max
        double minVal = *min_element(col.begin(), col.end());
        double maxVal = *max_element(col.begin(), col.end());
        for (double x : col)
            normCol.push_back((maxVal != minVal) ? (x - minVal) / (maxVal - minVal) : 0.0);
    } 
    else if (method == 2) { // Z-Score
        double mean = accumulate(col.begin(), col.end(), 0.0) / col.size();
        double sqSum = 0;
        for (double x : col) sqSum += (x - mean) * (x - mean);
        double stddev = sqrt(sqSum / col.size());
        for (double x : col)
            normCol.push_back((stddev != 0) ? (x - mean) / stddev : 0.0);
    } 
    else if (method == 3) { // Decimal Scaling
        double maxAbs = 0;
        for (double x : col) maxAbs = max(maxAbs, fabs(x));
        int j = (maxAbs != 0) ? to_string((long long)maxAbs).length() : 1;
        for (double x : col) normCol.push_back(x / pow(10, j));
    }
    return normCol;
}

// Function to apply normalization method to entire dataset
vector<vector<string>> applyNormalization(
    int method,
    const vector<string>& header,
    const vector<vector<string>>& rows,
    const vector<int>& numCols)
{
    int colCount = header.size();
    vector<vector<string>> columns(colCount);
    for (int c = 0; c < colCount; c++) {
        for (auto &row : rows) {
            columns[c].push_back(row[c]);
        }
    }

    vector<vector<string>> normalizedCols;

    for (int c = 0; c < colCount; c++) {
        if (find(numCols.begin(), numCols.end(), c) != numCols.end()) {
            vector<double> col;
            for (auto &v : columns[c]) col.push_back(stod(v));

            vector<double> normCol = normalizeColumn(col, method);

            vector<string> normStr;
            for (double val : normCol) {
                ostringstream oss;
                oss << fixed << setprecision(6) << val;
                normStr.push_back(oss.str());
            }
            normalizedCols.push_back(normStr);
        }
        else {
            normalizedCols.push_back(columns[c]);
        }
    }

    // Transpose back to rows
    vector<vector<string>> normalizedRows(rows.size(), vector<string>(colCount));
    for (int c = 0; c < colCount; c++) {
        for (int r = 0; r < rows.size(); r++) {
            normalizedRows[r][c] = normalizedCols[c][r];
        }
    }

    return normalizedRows;
}

// Function to save CSV
void saveCSV(const vector<string>& header, const vector<vector<string>>& normalizedRows, const string& outFile) {
    ofstream out(outFile);
    for (int i = 0; i < header.size(); i++) {
        out << header[i] << (i < header.size() - 1 ? "," : "\n");
    }
    for (auto &row : normalizedRows) {
        for (int i = 0; i < row.size(); i++) {
            out << row[i] << (i < row.size() - 1 ? "," : "\n");
        }
    }
    out.close();
    cout << "Normalized data saved to '" << outFile << "'\n";
}

// Function to print first 5 rows
void printPreview(const vector<string>& header, const vector<vector<string>>& rows, const string& title) {
    cout << "\n--- " << title << "\n";
    for (auto &h : header) cout << h << "\t";
    cout << "\n";
    for (int r = 0; r < min((int)rows.size(), 5); r++) {
        for (auto &cell : rows[r]) cout << cell << "\t";
        cout << "\n";
    }
}

int main() {
    string fileName;
    cout << "Enter the CSV file name: ";
    cin >> fileName;

    ifstream file(fileName);
    if (!file.is_open()) {
        cerr << "File not found." << endl;
        return 1;
    }

    vector<string> header;
    vector<vector<string>> rows;
    string line;

    // Read header
    if (getline(file, line)) {
        stringstream ss(line);
        string cell;
        while (getline(ss, cell, ',')) {
            header.push_back(cell);
        }
    }

    // Read rows
    while (getline(file, line)) {
        stringstream ss(line);
        string cell;
        vector<string> row;
        while (getline(ss, cell, ',')) {
            row.push_back(cell);
        }
        rows.push_back(row);
    }
    file.close();

    int colCount = header.size();
    vector<int> numCols;

    // Detect numeric columns
    for (int c = 0; c < colCount; c++) {
        bool allNum = true;
        for (auto &row : rows) {
            if (!isFloat(row[c])) {
                allNum = false;
                break;
            }
        }
        if (allNum) numCols.push_back(c);
    }

    // Choose normalization method
    int choice;
    cout << "\nChoose Normalization Method:\n";
    cout << "1. Min-Max Normalization\n";
    cout << "2. Z-Score Normalization\n";
    cout << "3. Decimal Scaling Normalization\n";
    cout << "4. All Methods\n";
    cout << "Enter choice (1/2/3/4): ";
    cin >> choice;

    if (choice >= 1 && choice <= 3) {
        auto normalizedRows = applyNormalization(choice, header, rows, numCols);
        printPreview(header, normalizedRows, "Normalized Data");
        
        string outFile;
        if (choice == 1) outFile = "minmax_normalized.csv";
        else if (choice == 2) outFile = "zscore_normalized.csv";
        else if (choice == 3) outFile = "decimalscaling_normalized.csv";

        saveCSV(header, normalizedRows, outFile);
    } 
    else if (choice == 4) {
        vector<string> titles = {"Min-Max Normalization", "Z-Score Normalization", "Decimal Scaling Normalization"};
        vector<string> outFiles = {"minmax_normalized.csv", "zscore_normalized.csv", "decimalscaling_normalized.csv"};
        for (int m = 1; m <= 3; m++) {
            auto normalizedRows = applyNormalization(m, header, rows, numCols);
            printPreview(header, normalizedRows, titles[m-1]);
            saveCSV(header, normalizedRows, outFiles[m-1]);
        }
    } 
    else {
        cout << "Invalid choice." << endl;
    }

    return 0;
}




// #include <iostream>
// #include <fstream>
// #include <sstream>
// #include <vector>
// #include <algorithm>
// #include <cmath>
// using namespace std;
// int main() {
//     string inputFilePath, outputFilePath, columnName;
//     cout << "Enter path to input CSV file: ";
//     getline(cin, inputFilePath);
//     cout << "Enter the column name to normalize: ";
//     getline(cin, columnName);
//     cout << "Choose normalization method (1 = Min-Max, 2 = Z-score, 3 = Decimal Scaling): ";
//     int method;
//     cin >> method;
//     cin.ignore();
//     double newMin = -1.0, newMax = 1.0;
//     cout << "Enter path to output CSV file: ";
//     getline(cin, outputFilePath);
//     ifstream inFile(inputFilePath);
//     if (!inFile.is_open()) {
//         cout << "Unable to open input file." << endl;
//         return 1;
//     }
//     string headerLine;
//     getline(inFile, headerLine);
//     if (headerLine.empty()) {
//         cout << "Input CSV file is empty." << endl;
//         return 1;
//     }
//     vector<string> headers;
//     stringstream ss(headerLine);
//     string temp;
//     while (getline(ss, temp, ',')) headers.push_back(temp);
//     int colIndex = -1;
//     for (int i = 0; i < headers.size(); i++) {
//         string h = headers[i];
//         if (h == columnName) { colIndex = i; break; }
//     }
//     if (colIndex == -1) {
//         cout << "Column '" << columnName << "' not found." << endl;
//         return 1;
//     }
//     vector<vector<string>> allRows;
//     allRows.push_back(headers);
//     vector<double> values;
//     string line;
//     while (getline(inFile, line)) {
//         stringstream ss(line);
//         vector<string> row;
//         string part;
//         while (getline(ss, part, ',')) row.push_back(part);
//         values.push_back(stod(row[colIndex]));
//         allRows.push_back(row);
//     }
//     inFile.close();
//     double minVal = *min_element(values.begin(), values.end());
//     double maxVal = *max_element(values.begin(), values.end());
//     double mean = 0;
//     for (double v : values) mean += v;
//     mean /= values.size();
//     double variance = 0;
//     for (double v : values) variance += pow(v - mean, 2);
//     double stdDev = sqrt(variance / values.size());
//     int maxAbs = pow(10, to_string((int)*max_element(values.begin(), values.end())).length());
//     for (int i = 1; i < allRows.size(); i++) {
//         double original = stod(allRows[i][colIndex]);
//         double normalized = 0;
//         if (method == 1)
//             normalized = (original - minVal) / (maxVal - minVal) * (newMax - newMin) + newMin;
//         else if (method == 2)
//             normalized = (stdDev != 0) ? (original - mean) / stdDev : 0;
//         else if (method == 3)
//             normalized = original / maxAbs;
//         else {
//             cout << "Invalid method choice." << endl;
//             return 1;
//         }
//         allRows[i][colIndex] = to_string(normalized);
//     }
//     ofstream outFile(outputFilePath);
//     for (auto &row : allRows) {
//         for (int i = 0; i < row.size(); i++) {
//             outFile << row[i];
//             if (i < row.size() - 1) outFile << ",";
//         }
//         outFile << "\n";
//     }
//     outFile.close();

//     cout << "Normalization complete. Output saved to " << outputFilePath << endl;
//     return 0;
// }
