# Assignment 3 — Minimum Spanning Tree

## Overview
This project implements two classical algorithms for finding the **Minimum Spanning Tree (MST)** of a graph:
1. **Prim’s Algorithm**
2. **Kruskal’s Algorithm**

Both algorithms have been implemented and compared in terms of execution time, the number of operations, and their performance on different graph sizes.

---

## Implemented Algorithms

### **Prim’s Algorithm**
- **Complexity:** O(E log V)
- **Data structure:** Priority Queue (min heap)
- **Works best for:** Dense graphs
- **Optimizations:**
    - Sentinel value: Global minimum moved to index 0 to simplify comparisons.
    - Fast path: Skip iteration if the current value is already greater than the previous value (for nearly-sorted data).
    - Binary search: Use binary search to find the correct insertion position.

### **Kruskal’s Algorithm**
- **Complexity:** O(E log E)
- **Data structure:** Disjoint Set Union (DSU)
- **Works best for:** Sparse graphs
- **Optimizations:**
    - Sort edges first.
    - Use the Union-Find data structure to efficiently manage connected components.

---

## Input & Output

### Input:
- **File:** `input.json`
- **Format:** The file contains a graph with `nodes` and `edges` properties.

### Output:
- **File 1:** `output.json` — Contains the results of the MST calculation for both Prim and Kruskal algorithms.
- **File 2:** `summary.csv` — Performance metrics including execution time, comparisons, and auxiliary operations for both algorithms.

---

## Performance Metrics

During execution, the following metrics are collected:
- **Execution time (ms):** The total time taken by the algorithm.
- **Comparisons:** The number of comparisons made.
- **Swaps:** The number of swaps performed.
- **Accesses:** The total number of array reads and writes.
- **Allocations:** The number of memory allocations made.

---

## How to Run

1. Clone the repository or download the files.
2. Open the project in your favorite IDE (e.g., IntelliJ IDEA).
3. **Run** the `Runner.java` class to execute the MST algorithms.

Alternatively, you can run the project from the command line:

````
java Runner --in input.json --outJson output.json --outCsv summary.csv
{
    "graphs": [
        {
            "id": "1",
            "nodes": ["A", "B", "C", "D"],
            "edges": [
                {"from": "A", "to": "B", "weight": 10},
                {"from": "A", "to": "C", "weight": 5},
                {"from": "B", "to": "C", "weight": 2},
                {"from": "B", "to": "D", "weight": 1},
                {"from": "C", "to": "D", "weight": 3}
            ]
        }
    ]
}
````
Performance Graphs
Time vs n (Execution Time vs Number of Vertices)

CSV Output Example

Here’s an example of the results stored in summary.csv:
````
dataset,V,E,algo,totalCost,time_ms,op_main,op_extra
id=1,5,7,Prim,16,3,304,43,28
id=1,5,7,Kruskal,16,0,673,11,43
id=2,4,5,Prim,6,0,25,26,20
id=2,4,5,Kruskal,6,0,15,8,18
````