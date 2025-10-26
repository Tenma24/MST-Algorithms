import java.util.*;

public class KruskalMST {
    public static class Result {
        public final List<Edge> mst;
        public final int totalCost;
        public final Metrics m;

        public Result(List<Edge> mst, int totalCost, Metrics m) {
            this.mst = mst; this.totalCost = totalCost; this.m = m;
        }
    }

    public static Result run(Graph g) {
        long t0 = System.nanoTime();
        Metrics M = new Metrics();

        Edge[] arr = g.edges.toArray(new Edge[0]);
        mergeSortByWeight(arr, M);

        DisjointSet dsu = new DisjointSet(g.V);
        List<Edge> mst = new ArrayList<>();
        int total = 0;

        for (Edge e : arr) {
            boolean joined = dsu.union(e.u, e.v);
            M.auxOps += 1 + dsu.getFindOps();
            if (joined) {
                mst.add(e);
                total += e.w;
                if (mst.size() == Math.max(0, g.V - 1)) break;
            }
        }

        M.timeMs = (System.nanoTime() - t0) / 1_000_000.0;
        return new Result(mst, total, M);
    }

    private static void mergeSortByWeight(Edge[] a, Metrics M) {
        Edge[] tmp = new Edge[a.length];
        ms(a, 0, a.length - 1, tmp, M);
    }
    private static void ms(Edge[] a, int l, int r, Edge[] tmp, Metrics M) {
        if (l >= r) return;
        int m = (l + r) >>> 1;
        ms(a, l, m, tmp, M);
        ms(a, m + 1, r, tmp, M);
        int i = l, j = m + 1, k = 0;
        while (i <= m && j <= r) {
            M.comparisons++;
            if (a[i].w <= a[j].w) tmp[k++] = a[i++];
            else tmp[k++] = a[j++];
        }
        while (i <= m) tmp[k++] = a[i++];
        while (j <= r) tmp[k++] = a[j++];
        System.arraycopy(tmp, 0, a, l, k);
    }
}
