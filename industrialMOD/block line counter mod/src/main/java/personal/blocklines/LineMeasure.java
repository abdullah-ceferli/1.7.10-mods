package personal.blocklines;

/** Bounded incremental scan of one horizontal, axis-aligned row. */
public final class LineMeasure {
    public static final int MAX_LENGTH = 65536;
    public interface Blocks {
        /** -1 = unavailable chunk, 0 = air, 1 = any non-air block. */
        int at(int x, int y, int z);
    }
    public enum Stage { OFF, FIRST, SECOND, COUNTING, DONE, ERROR }
    public static final class Point {
        public final int x, y, z;
        public Point(int x, int y, int z) { this.x = x; this.y = y; this.z = z; }
    }
    private Stage stage = Stage.OFF;
    private Point first;
    private int stepX, stepZ, length, visited, count;
    private String label = "";

    public void toggle() {
        if (enabled()) reset();
        else { stage = Stage.FIRST; label = "Right-click first block"; }
    }
    public void reset() {
        stage = Stage.OFF; first = null; length = visited = count = 0; label = "";
    }
    public void select(Point point) {
        if (!enabled()) return;
        if (stage != Stage.SECOND) {
            first = point; visited = count = 0; stage = Stage.SECOND;
            label = "Right-click second block (same height)";
            return;
        }
        if (point.y != first.y) { label = "Choose the same height (Y = " + first.y + ")"; return; }
        if (point.x != first.x && point.z != first.z) { label = "Choose a straight row along X or Z"; return; }
        long distance = Math.abs((long)point.x - first.x) + Math.abs((long)point.z - first.z) + 1;
        if (distance > MAX_LENGTH) { label = "Maximum line length: " + MAX_LENGTH; return; }
        length = (int)distance;
        stepX = Integer.compare(point.x, first.x); stepZ = Integer.compare(point.z, first.z);
        visited = count = 0; stage = Stage.COUNTING; label = "Counting blocks...";
    }
    public void tick(Blocks blocks, int budget) {
        if (stage != Stage.COUNTING) return;
        for (int work = 0; work < budget && visited < length; work++, visited++) {
            int present = blocks.at(first.x + stepX * visited, first.y, first.z + stepZ * visited);
            if (present < 0) {
                count = 0; stage = Stage.ERROR;
                label = "Line contains unloaded chunks. Move closer.";
                return;
            }
            if (present > 0) count++;
        }
        if (visited == length) { stage = Stage.DONE; label = "Blocks: " + count; }
    }
    public boolean enabled() { return stage != Stage.OFF; }
    public Stage stage() { return stage; }
    public int count() { return count; }
    public int visited() { return visited; }
    public String label() { return label; }
}
