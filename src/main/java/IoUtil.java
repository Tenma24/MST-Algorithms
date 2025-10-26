import java.io.*;
import java.nio.file.*;
import java.util.*;

public class IoUtil {

    public static class Dataset {
        public final String name;
        public final Graph graph;
        public Dataset(String name, Graph g) { this.name = name; this.graph = g; }
    }

    public static List<Dataset> readInputJson(String path) throws Exception {
        String s = Files.readString(Path.of(path));
        List<Dataset> out = new ArrayList<>();
        int idxArr = s.indexOf("\"graphs\"");
        if (idxArr < 0) throw new IllegalArgumentException("No \"graphs\" array in input");
        int lb = s.indexOf('[', idxArr), rb = findMatchingBracket(s, lb);
        String items = s.substring(lb + 1, rb);

        int pos = 0;
        while (true) {
            int objL = items.indexOf('{', pos);
            if (objL < 0) break;
            int objR = findMatchingBrace(items, objL);
            String obj = items.substring(objL + 1, objR);

            String idStr = extractValue(obj, "\"id\"");
            String nodesArr = extractArray(obj, "\"nodes\"");
            String edgesArr = extractArray(obj, "\"edges\"");

            List<String> names = parseStringArray(nodesArr);
            Map<String,Integer> map = new HashMap<>();
            for (int i = 0; i < names.size(); i++) map.put(names.get(i), i);

            List<Edge> edges = parseEdges(edgesArr, map);

            Graph g = new Graph(names, edges);
            out.add(new Dataset("id=" + idStr, g));

            pos = objR + 1;
        }
        return out;
    }

    private static int findMatchingBracket(String s, int l) {
        int bal = 0;
        for (int i = l; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c == '[') bal++;
            else if (c == ']') { bal--; if (bal == 0) return i; }
        }
        return -1;
    }
    private static int findMatchingBrace(String s, int l) {
        int bal = 0;
        for (int i = l; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c == '{') bal++;
            else if (c == '}') { bal--; if (bal == 0) return i; }
        }
        return -1;
    }

    private static String extractValue(String obj, String key) {
        int p = obj.indexOf(key);
        if (p < 0) return "";
        int colon = obj.indexOf(':', p);
        int comma = obj.indexOf(',', colon + 1);
        if (comma < 0) comma = obj.length();
        return obj.substring(colon + 1, comma).replaceAll("[^0-9A-Za-z_\\-\\.]", "").trim();
    }

    private static String extractArray(String obj, String key) {
        int p = obj.indexOf(key);
        if (p < 0) return "[]";
        int lb = obj.indexOf('[', p);
        int rb = findMatchingBracket(obj, lb);
        return obj.substring(lb, rb + 1);
    }

    private static List<String> parseStringArray(String arr) {
        List<String> out = new ArrayList<>();
        int i = 0;
        while (true) {
            int q1 = arr.indexOf('"', i);
            if (q1 < 0) break;
            int q2 = arr.indexOf('"', q1 + 1);
            if (q2 < 0) break;
            out.add(arr.substring(q1 + 1, q2));
            i = q2 + 1;
        }
        return out;
    }

    private static List<Edge> parseEdges(String arr, Map<String,Integer> map) {
        List<Edge> out = new ArrayList<>();
        int pos = 0;
        while (true) {
            int l = arr.indexOf('{', pos);
            if (l < 0) break;
            int r = findMatchingBrace(arr, l);
            String obj = arr.substring(l + 1, r);
            String from = extractString(obj, "\"from\"");
            String to   = extractString(obj, "\"to\"");
            int w = Integer.parseInt(extractValue(obj, "\"weight\""));
            Integer u = map.get(from), v = map.get(to);
            if (u == null || v == null) throw new IllegalArgumentException("Unknown node name");
            out.add(new Edge(u, v, w));
            pos = r + 1;
        }
        return out;
    }

    private static String extractString(String obj, String key) {
        int p = obj.indexOf(key);
        if (p < 0) return "";
        int q1 = obj.indexOf('"', obj.indexOf(':', p));
        int q2 = obj.indexOf('"', q1 + 1);
        return obj.substring(q1 + 1, q2);
    }

    public static void writeSummaryCsv(String path, List<String> lines) throws Exception {
        try (PrintWriter pw = new PrintWriter(new FileWriter(path, false))) {
            for (String ln : lines) pw.println(ln);
        }
    }

    public static void writeOutputJson(String path, String json) throws Exception {
        try (PrintWriter pw = new PrintWriter(new FileWriter(path, false))) {
            pw.println(json);
        }
    }
}
