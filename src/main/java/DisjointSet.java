public class DisjointSet {
    private final int[] parent, rank;
    private long findOps = 0, unionOps = 0;

    public DisjointSet(int n) {
        parent = new int[n]; rank = new int[n];
        for (int i = 0; i < n; i++) parent[i] = i;
    }

    public int find(int x) {
        findOps++;
        if (parent[x] != x) parent[x] = find(parent[x]);
        return parent[x];
    }

    public boolean union(int a, int b) {
        unionOps++;
        a = find(a); b = find(b);
        if (a == b) return false;
        if (rank[a] < rank[b]) parent[a] = b;
        else if (rank[a] > rank[b]) parent[b] = a;
        else { parent[b] = a; rank[a]++; }
        return true;
    }

    public long getFindOps()  { return findOps; }
    public long getUnionOps() { return unionOps; }
}
