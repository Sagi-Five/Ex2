// TestEx2Sheet.java
package assignments.ex2;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the Ex2Sheet class.
 */
class Ex2SheetTest {

    @Test
    void testSetAndGet() {
        Ex2Sheet sheet = new Ex2Sheet(5, 5);
        sheet.set(2, 2, "Hello");
        assertEquals("Hello", sheet.get(2, 2).getData());

        sheet.set(3, 3, "=A1+B2");
        assertEquals("=A1+B2", sheet.get(3, 3).getData());
    }

    @Test
    void testEvalSingleCell() {
        Ex2Sheet sheet = new Ex2Sheet(5, 5);
        sheet.set(0, 0, "2");
        sheet.set(1, 0, "3");
        sheet.set(2, 0, "=A0+B0");

        assertEquals("5.0", sheet.eval(2, 0));
    }

    @Test
    void testEvalAllCells() {
        Ex2Sheet sheet = new Ex2Sheet(5, 5);
        sheet.set(0, 0, "2");
        sheet.set(1, 0, "3");
        sheet.set(2, 0, "=A0+B0");

        sheet.eval();

        assertEquals("2", sheet.value(0, 0));
        assertEquals("3", sheet.value(1, 0));
        assertEquals("5.0", sheet.value(2, 0));
    }

    @Test
    void testSaveAndLoad() throws Exception {
        Ex2Sheet sheet = new Ex2Sheet(5, 5);
        sheet.set(0, 0, "2");
        sheet.set(1, 0, "3");
        sheet.set(2, 0, "=A0+B0");

        String fileName = "test_sheet.txt";
        sheet.save(fileName);

        Ex2Sheet loadedSheet = new Ex2Sheet(5, 5);
        loadedSheet.load(fileName);

        assertEquals("2", loadedSheet.value(0, 0));
        assertEquals("3", loadedSheet.value(1, 0));
        assertEquals("5.0", loadedSheet.eval(2, 0));
    }

    @Test
    void testDepthCalculation() {
        Ex2Sheet sheet = new Ex2Sheet(5, 5);
        sheet.set(0, 0, "2");
        sheet.set(1, 0, "=A0");
        sheet.set(2, 0, "=B0");

        int[][] depth = sheet.depth();

        assertEquals(0, depth[0][0]);
        assertEquals(1, depth[1][0]);
        assertEquals(2, depth[2][0]);
    }

}
