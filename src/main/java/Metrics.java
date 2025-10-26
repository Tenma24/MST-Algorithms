public class Metrics {
    public long comparisons = 0;
    public long auxOps = 0;
    public double timeMs = 0.0;

    public void reset() { comparisons = auxOps = 0; timeMs = 0.0; }
}
