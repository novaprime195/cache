#include <bits/stdc++.h>
using namespace std;

// Function to read CSV file
pair<vector<string>, vector<map<string, string>>> read_csv(const string& filename) {
    ifstream file(filename);
    if (!file.is_open()) {
        cerr << "File not found.\n";
        exit(1);
    }

    vector<string> headers;
    vector<map<string, string>> rows;
    string line;

    // Read header line
    if (getline(file, line)) {
        stringstream ss(line);
        string col;
        while (getline(ss, col, ',')) {
            headers.push_back(col);
        }
    } else {
        cerr << "Empty file.\n";
        exit(1);
    }

    // Read data rows
    while (getline(file, line)) {
        stringstream ss(line);
        string value;
        map<string, string> row;
        for (size_t i = 0; i < headers.size(); ++i) {
            if (!getline(ss, value, ',')) value = "";
            row[headers[i]] = value;
        }
        rows.push_back(row);
    }

    return {headers, rows};
}

int main() {
    string filename;
    cout << "Enter input CSV path: ";
    getline(cin, filename);

    auto [headers, rows] = read_csv(filename);

    cout << "\nColumns available: ";
    for (size_t i = 0; i < headers.size(); ++i) {
        cout << headers[i];
        if (i != headers.size() - 1) cout << ", ";
    }
    cout << "\n";

    string row_field, col_field, val_field;
    cout << "Enter row field (e.g., " << headers[0] << "): ";
    getline(cin, row_field);
    cout << "Enter column field (e.g., " << headers[1] << "): ";
    getline(cin, col_field);
    cout << "Enter numeric value field (e.g., " << headers.back() << "): ";
    getline(cin, val_field);

    if (find(headers.begin(), headers.end(), row_field) == headers.end() ||
        find(headers.begin(), headers.end(), col_field) == headers.end() ||
        find(headers.begin(), headers.end(), val_field) == headers.end()) {
        cerr << "Invalid column name(s).\n";
        return 1;
    }

    // Data structures
    map<string, map<string, double>> data;
    vector<string> col_values;

    for (auto& r : rows) {
        try {
            string row_val = r[row_field];
            string col_val = r[col_field];
            double val = stod(r[val_field]);

            data[row_val][col_val] = val;
            if (find(col_values.begin(), col_values.end(), col_val) == col_values.end())
                col_values.push_back(col_val);
        } catch (...) {
            cerr << "Non-numeric value in '" << val_field << "', skipping row.\n";
            continue;
        }
    }

    if (data.empty()) {
        cerr << "No valid numeric data found.\n";
        return 1;
    }

    // Compute totals
    map<string, double> row_totals, col_totals;
    for (auto& [r, cols] : data) {
        double sum = 0;
        for (auto& [c, v] : cols) sum += v;
        row_totals[r] = sum;
    }

    for (auto& [r, cols] : data) {
        for (auto& [c, v] : cols) {
            col_totals[c] += v;
        }
    }

    double grand_total = 0;
    for (auto& [c, v] : col_totals) grand_total += v;

    // Print formatted table
    int col_width = 22;
    cout << "\n" << setw(8) << " ";
    for (auto& c : col_values)
        cout << setw(col_width) << c;
    cout << "Total\n";
    cout << string(8 + col_width * col_values.size() + 10, '-') << "\n";

    for (auto& [r, cols] : data) {
        cout << left << setw(8) << r;
        for (auto& c : col_values) {
            double v = cols.count(c) ? cols[c] : 0.0;
            double row_total = row_totals[r];
            double col_total = col_totals[c];
            double dwt = (row_total != 0) ? (v / row_total * 100.0) : 0.0;
            double twt = (col_total != 0) ? (v / col_total * 100.0) : 0.0;

            stringstream cell;
            cell << fixed << setprecision(2) << setw(7) << v
                 << setw(7) << twt << "% " << setw(6) << dwt << "%";
            cout << setw(col_width) << cell.str();
        }
        cout << fixed << setprecision(2) << row_totals[r] << " 100.00%\n";
    }

    cout << string(8 + col_width * col_values.size() + 10, '-') << "\n";

    cout << left << setw(8) << "Total";
    for (auto& c : col_values) {
        stringstream cell;
        cell << fixed << setprecision(2) << setw(7) << col_totals[c] << " 100.00%";
        cout << setw(col_width) << cell.str();
    }
    cout << fixed << setprecision(2) << grand_total << "\n";
    cout << string(8 + col_width * col_values.size() + 10, '-') << "\n";

    return 0;
}



// #include <bits/stdc++.h>
// #include <fstream>
// using namespace std;
// int main() {
//     fstream file("input.csv", ios::in);
//     if (!file.is_open()) {
//         cout << "Couldn't Open file";
//         return 0;
//     }

//     string line, word;
//     string col, row, count_str; 
//     int val;

//     map<string, map<string, int>> classrowcolMap;
//     map<string, int> colMap;
//     map<string, int> rowMap;
// int i = 0;
//     while (getline(file, line)) {
//         stringstream str(line);
//         if (i == 0) { // skip header
//             i++;
//             continue;
//         }
//         getline(str, row, ',');      
//         getline(str, col, ',');     
//         getline(str, count_str, ',');   
//         val = stoi(count_str);
//         classrowcolMap[row][col] = val;
//         colMap[col] += val;
//         rowMap[row] += val;
//     }
//     file.close();

//     int colSum = 0; 
//     for (auto c : colMap) {
//         colSum += c.second;
//     }
// int rowSum = 0; 
//     for (auto r : rowMap) {
//         rowSum += r.second;
//     }

//     ofstream fw("output.csv", ios::out);
//     if (!fw.is_open()) {
//         cout << "Couldn't create output file";
//         return 0;
//     }

//     auto writeAndPrint = [&](const string &s) {
//         fw << s;
//         cout << s;
//     };

//     // Header
//     writeAndPrint("Region\\Brand,");
//     for (auto c : colMap) {
//         writeAndPrint(c.first + " Sales," + c.first + " t-weight," + c.first + " d-weight,");
//     }
//     writeAndPrint("Total Sales,Total t-weight,Total d-weight\n");

//     // Rows
//     for (auto r : rowMap) {
//         row = r.first;
//         writeAndPrint(row + ",");

//         for (auto c : colMap) {
//             col = c.first;
//             float current_sales = classrowcolMap[row][col];
//             float brand_total_sales = colMap[col];
//             float region_total_sales = rowMap[row]; 

//             writeAndPrint(to_string((int)current_sales) + ",");

//             float t_weight = (region_total_sales > 0) ? (current_sales / region_total_sales) * 100 : 0;
//             writeAndPrint(to_string(t_weight) + "%,");

//             float d_weight = (brand_total_sales > 0) ? (current_sales / brand_total_sales) * 100 : 0;
//             writeAndPrint(to_string(d_weight) + "%,");
//         }

//         float total_region_sales = rowMap[row];
//         writeAndPrint(to_string((int)total_region_sales) + ",");
//         writeAndPrint("100.00%,");
//         float d_weight_total_row = (colSum > 0) ? (total_region_sales / colSum) * 100 : 0;
//         writeAndPrint(to_string(d_weight_total_row) + "%\n");
//     }

//     // Totals row
//     writeAndPrint("Total,");
//     for (auto c : colMap) {
//         col = c.first;
//         float total_brand_sales = colMap[col];
//         writeAndPrint(to_string((int)total_brand_sales) + ",");
//         float t_weight_total_col = (rowSum > 0) ? (total_brand_sales / rowSum) * 100 : 0;
//         writeAndPrint(to_string(t_weight_total_col) + "%,");
//         writeAndPrint("100.00%,");
//     }
//     writeAndPrint(to_string(colSum) + ",100.00%,100.00%\n");

//     fw.close(); 
//     return 0;
// }