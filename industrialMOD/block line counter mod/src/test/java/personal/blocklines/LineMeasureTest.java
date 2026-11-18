package personal.blocklines;

import org.junit.Test;
import static org.junit.Assert.*;

public class LineMeasureTest {
    @Test public void inclusiveRowSkipsGapsAndCountsAllBlockTypes() {
        LineMeasure m = new LineMeasure(); m.toggle();
        m.select(new LineMeasure.Point(1, 64, 7)); m.select(new LineMeasure.Point(8, 64, 7));
        m.tick((x,y,z) -> x == 3 || x == 5 ? 0 : 1, 256);
        assertEquals(LineMeasure.Stage.DONE, m.stage()); assertEquals(6, m.count());
    }
    @Test public void reversedNegativeZAndSamePointWork() {
        LineMeasure m = new LineMeasure(); m.toggle();
        m.select(new LineMeasure.Point(-12, 4, 2)); m.select(new LineMeasure.Point(-12, 4, -5));
        m.tick((x,y,z) -> z == -1 ? 0 : 1, 256); assertEquals(7, m.count());
        m.select(new LineMeasure.Point(0, 1, 0)); m.select(new LineMeasure.Point(0, 1, 0));
        m.tick((x,y,z) -> 1, 256); assertEquals(1, m.count());
    }
    @Test public void rejectsHeightAndDiagonalButKeepsFirstSelection() {
        LineMeasure m = new LineMeasure(); m.toggle(); m.select(new LineMeasure.Point(0, 64, 0));
        m.select(new LineMeasure.Point(9, 65, 0)); assertEquals(LineMeasure.Stage.SECOND, m.stage());
        m.select(new LineMeasure.Point(9, 64, 9)); assertEquals(LineMeasure.Stage.SECOND, m.stage());
        m.select(new LineMeasure.Point(9, 64, 0)); m.tick((x,y,z) -> 1, 256); assertEquals(10, m.count());
    }
    @Test public void largeScanHasBoundedWorkAndDoesNotRescanAfterResult() {
        LineMeasure m = new LineMeasure(); m.toggle();
        m.select(new LineMeasure.Point(0, 1, 0)); m.select(new LineMeasure.Point(999, 1, 0));
        int[] reads = {0}; LineMeasure.Blocks blocks = (x,y,z) -> { reads[0]++; return 1; };
        m.tick(blocks, 256); assertEquals(256, reads[0]); assertEquals(LineMeasure.Stage.COUNTING, m.stage());
        for (int i=0; i<4; i++) m.tick(blocks, 256);
        assertEquals(1000, reads[0]); assertEquals(1000, m.count());
        m.tick(blocks, 256); assertEquals(1000, reads[0]);
    }
    @Test public void unloadedChunkProducesErrorInsteadOfPartialCount() {
        LineMeasure m = new LineMeasure(); m.toggle();
        m.select(new LineMeasure.Point(0, 1, 0)); m.select(new LineMeasure.Point(31, 1, 0));
        m.tick((x,y,z) -> x >= 16 ? -1 : 1, 256);
        assertEquals(LineMeasure.Stage.ERROR, m.stage()); assertEquals(0, m.count());
    }
    @Test public void toggleClearsAndDisablesAndNextToggleStartsFresh() {
        LineMeasure m = new LineMeasure(); m.toggle();
        m.select(new LineMeasure.Point(0, 1, 0)); m.select(new LineMeasure.Point(1, 1, 0));
        m.tick((x,y,z) -> 1, 256); m.toggle();
        assertFalse(m.enabled()); assertEquals(0, m.count()); assertEquals("", m.label());
        m.toggle(); assertEquals(LineMeasure.Stage.FIRST, m.stage());
        m.reset(); assertFalse(m.enabled());
    }
    @Test public void rejectsOversizedAndOverflowingRangesWithoutReadingWorld() {
        LineMeasure m = new LineMeasure(); m.toggle();
        m.select(new LineMeasure.Point(Integer.MIN_VALUE, 0, 0));
        m.select(new LineMeasure.Point(Integer.MAX_VALUE, 0, 0));
        assertEquals(LineMeasure.Stage.SECOND, m.stage());
        m.tick((x,y,z) -> { fail("No world reads expected"); return 0; }, 256);
    }
}
