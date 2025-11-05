import pandas as pd
import matplotlib.pyplot as plt
from scipy.cluster.hierarchy import linkage, dendrogram

# Load dataset
df = pd.read_csv(r"C:\Users\prapt\OneDrive\Desktop\sem 7\DM\DM lab\Hierarchical\Iris.csv")

# Show column names for clarity
print("Columns in dataset:", df.columns.tolist())

# Automatically detect label column (the last non-numeric one)
numeric_df = df.select_dtypes(include=['float64', 'int64'])
features = numeric_df.values

# Try to use the last column as labels (if it’s not numeric)
if not pd.api.types.is_numeric_dtype(df.iloc[:, -1]):
    labels = df.iloc[:, -1].values
else:
    labels = None  # no labels available

methods = ['single', 'complete', 'average']

for method in methods:
    plt.figure(figsize=(8, 5))
    linkage_matrix = linkage(features, method=method)
    dendrogram(linkage_matrix, labels=labels)
    plt.title(f"{method.capitalize()} Linkage Dendrogram")
    plt.xlabel("Samples" if labels is None else "Samples (Labels)")
    plt.ylabel("Distance")
    plt.tight_layout()
    plt.show()
