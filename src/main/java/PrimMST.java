import java.util.*;

public class PrimMST {
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

        boolean[] vis = new boolean[g.V];
        List<Edge> mst = new ArrayList<>();
        int total = 0;

        PriorityQueue<int[]> pq = new PriorityQueue<>((a,b)->{
            M.comparisons++;
            return Integer.compare(a[0], b[0]);
        });

        for (int start = 0; start < g.V; start++) {
            if (vis[start]) continue;
            vis[start] = true;
            for (int[] e : g.adj.get(start)) { pq.offer(new int[]{e[1], start, e[0]}); M.auxOps++; }

            while (!pq.isEmpty()) {
                int[] top = pq.poll(); M.auxOps++;
                int w = top[0], u = top[1], v = top[2];
                if (vis[v]) continue;
                vis[v] = true;
                mst.add(new Edge(u, v, w));
                total += w;

                for (int[] e : g.adj.get(v)) { pq.offer(new int[]{e[1], v, e[0]}); M.auxOps++; }
            }
        }

        M.timeMs = (System.nanoTime() - t0) / 1_000_000.0;
        return new Result(mst, total, M);
    }
}
