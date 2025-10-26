import java.util.*;

public class Runner {

    public static void main(String[] args) throws Exception {
        String in = "input.json", outJson = "output.json", outCsv = "summary.csv";
        for (int i = 0; i < args.length; i++) {
            switch (args[i]) {
                case "--in":      in = args[++i]; break;
                case "--outJson": outJson = args[++i]; break;
                case "--outCsv":  outCsv = args[++i]; break;
                case "-h":
                case "--help": printHelp(); return;
                default: System.err.println("Unknown arg: " + args[i]); printHelp(); return;
            }
        }

        List<IoUtil.Dataset> datasets = IoUtil.readInputJson(in);

        List<String> csv = new ArrayList<>();
        String header = "dataset,V,E,algo,totalCost,time_ms,op_main,op_extra";
        csv.add(header);
        System.out.println(header);

        StringBuilder out = new StringBuilder();
        out.append("{\"results\":[\n");

        for (int di = 0; di < datasets.size(); di++) {
            IoUtil.Dataset ds = datasets.get(di);
            Graph g = ds.graph;

            PrimMST.Result rp = PrimMST.run(g);
            KruskalMST.Result rk = KruskalMST.run(g);

            boolean sameCost = (rp.totalCost == rk.totalCost);
            boolean mstSizeOkP = (rp.mst.size() == Math.max(0, g.V - 1)) || g.V == 0;
            boolean mstSizeOkK = (rk.mst.size() == Math.max(0, g.V - 1)) || g.V == 0;
            boolean acyclicP = isAcyclic(g.V, rp.mst);
            boolean acyclicK = isAcyclic(g.V, rk.mst);
            boolean connectedP = Graph.isConnectedMST(g.V, rp.mst);
            boolean connectedK = Graph.isConnectedMST(g.V, rk.mst);

            String lineP = String.join(",",
                    ds.name, Integer.toString(g.V), Integer.toString(g.edges.size()),
                    "Prim", Integer.toString(rp.totalCost),
                    String.format("%.3f", rp.m.timeMs),
                    Long.toString(rp.m.comparisons), Long.toString(rp.m.auxOps));

            String lineK = String.join(",",
                    ds.name, Integer.toString(g.V), Integer.toString(g.edges.size()),
                    "Kruskal", Integer.toString(rk.totalCost),
                    String.format("%.3f", rk.m.timeMs),
                    Long.toString(rk.m.comparisons), Long.toString(rk.m.auxOps));

            csv.add(lineP); csv.add(lineK);
            System.out.println(lineP);
            System.out.println(lineK);

            out.append("  {\n");
            out.append("    \"dataset\":\"").append(ds.name).append("\",\n");
            out.append("    \"vertices\":").append(g.V).append(", \"edges\":").append(g.edges.size()).append(",\n");
            out.append("    \"prim\":{\n");
            out.append("      \"total_cost\":").append(rp.totalCost).append(",\n");
            out.append("      \"time_ms\":").append(String.format(java.util.Locale.US, "%.3f", rp.m.timeMs)).append(",\n");
            out.append("      \"operations\": {\"comparisons\":").append(rp.m.comparisons)
                    .append(",\"aux\":").append(rp.m.auxOps).append("},\n");
            out.append("      \"mst_edges\":[");
            for (int i = 0; i < rp.mst.size(); i++) {
                Edge e = rp.mst.get(i);
                out.append("{\"u\":\"").append(ds.graph.names.get(e.u)).append("\",")
                        .append("\"v\":\"").append(ds.graph.names.get(e.v)).append("\",")
                        .append("\"w\":").append(e.w).append("}");
                if (i + 1 < rp.mst.size()) out.append(",");
            }
            out.append("]\n    },\n");

            out.append("    \"kruskal\":{\n");
            out.append("      \"total_cost\":").append(rk.totalCost).append(",\n");
            out.append("      \"time_ms\":").append(String.format(java.util.Locale.US, "%.3f", rk.m.timeMs)).append(",\n");
            out.append("      \"operations\": {\"comparisons\":").append(rk.m.comparisons)
                    .append(",\"aux\":").append(rk.m.auxOps).append("},\n");
            out.append("      \"mst_edges\":[");
            for (int i = 0; i < rk.mst.size(); i++) {
                Edge e = rk.mst.get(i);
                out.append("{\"u\":\"").append(ds.graph.names.get(e.u)).append("\",")
                        .append("\"v\":\"").append(ds.graph.names.get(e.v)).append("\",")
                        .append("\"w\":").append(e.w).append("}");
                if (i + 1 < rk.mst.size()) out.append(",");
            }
            out.append("]\n    },\n");

            out.append("    \"check\":{")
                    .append("\"sameCost\":").append(sameCost).append(",")
                    .append("\"mstSizeOkPrim\":").append(mstSizeOkP).append(",")
                    .append("\"mstSizeOkKruskal\":").append(mstSizeOkK).append(",")
                    .append("\"acyclicPrim\":").append(acyclicP).append(",")
                    .append("\"acyclicKruskal\":").append(acyclicK).append(",")
                    .append("\"connectedPrim\":").append(connectedP).append(",")
                    .append("\"connectedKruskal\":").append(connectedK).append("}\n");

            out.append("  }");
            if (di + 1 < datasets.size()) out.append(",\n"); else out.append("\n");
        }
        out.append("]}");

        IoUtil.writeSummaryCsv(outCsv, csv);
        IoUtil.writeOutputJson(outJson, out.toString());
    }

    private static boolean isAcyclic(int V, List<Edge> mst) {
        DisjointSet dsu = new DisjointSet(V);
        for (Edge e : mst) {
            if (!dsu.union(e.u, e.v)) return false;
        }
        return true;
    }

    private static void printHelp() {
        System.out.println("Usage:");
        System.out.println("  --in input.json   --outJson output.json   --outCsv summary.csv");
    }
}
