import java.util.*;

public class Graph {
    public final int V;
    public final List<String> names;
    public final List<Edge> edges;
    public final List<List<int[]>> adj;

    public Graph(List<String> names, List<Edge> edges) {
        this.names = new ArrayList<>(names);
        this.V = names.size();
        this.edges = new ArrayList<>(edges);
        this.adj = new ArrayList<>(V);
        for (int i = 0; i < V; i++) adj.add(new ArrayList<>());
        for (Edge e : edges) {
            adj.get(e.u).add(new int[]{e.v, e.w});
            adj.get(e.v).add(new int[]{e.u, e.w});
        }
    }

    public static boolean isConnectedMST(int V, List<Edge> mst) {
        if (V == 0) return true;
        List<List<Integer>> g = new ArrayList<>();
        for (int i = 0; i < V; i++) g.add(new ArrayList<>());
        for (Edge e : mst) { g.get(e.u).add(e.v); g.get(e.v).add(e.u); }

        boolean[] vis = new boolean[V];
        int start = 0;
        if (!mst.isEmpty()) start = mst.get(0).u;
        Deque<Integer> dq = new ArrayDeque<>();
        dq.add(start); vis[start] = true;
        int cnt = 1;
        while (!dq.isEmpty()) {
            int x = dq.poll();
            for (int y : g.get(x)) if (!vis[y]) { vis[y] = true; dq.add(y); cnt++; }
        }

        return mst.isEmpty() ? V <= 1 : (cnt == countUsedVertices(V, mst));
    }

    private static int countUsedVertices(int V, List<Edge> mst) {
        boolean[] used = new boolean[V];
        for (Edge e : mst) { used[e.u] = true; used[e.v] = true; }
        int c = 0; for (boolean b : used) if (b) c++;
        return c == 0 ? 1 : c;
    }
}
