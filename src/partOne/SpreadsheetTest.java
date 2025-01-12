package partOne;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class SpreadsheetTest {

    @Test
    void xCell() {
        Spreadsheet sheet = new Spreadsheet(5, 5);
        assertEquals(0, sheet.xCell("A1"), "Expected column A to map to index 0");
        assertEquals(25, sheet.xCell("Z1"), "Expected column Z to map to index 25");
        assertEquals(-1, sheet.xCell("AA1"), "Expected invalid column AA to return -1");
    }

    @Test
    void yCell() {
        Spreadsheet sheet = new Spreadsheet(5, 5);
        assertEquals(0, sheet.yCell("A1"), "Expected row 1 to map to index 0");
        assertEquals(9, sheet.yCell("A10"), "Expected row 10 to map to index 9");
        assertEquals(-1, sheet.yCell("A"), "Expected invalid format without row number to return -1");
    }

    @Test
    void eval() {
        Spreadsheet sheet = new Spreadsheet(5, 5);
        Cell cell = new Cell("50", sheet);
        sheet.set(0, 0, cell); // A1
        assertEquals("50", sheet.eval(0, 0), "Expected evaluation of numeric cell to match its value");

        Cell formulaCell = new Cell("=A1*2", sheet);
        sheet.set(1, 0, formulaCell); // B1
        assertEquals("100.0", sheet.eval(1, 0), "Expected formula evaluation to compute correctly");
    }

    @Test
    void evalAll() {
        Spreadsheet sheet = new Spreadsheet(2, 2);
        sheet.set(0, 0, new Cell("5", sheet)); // A1
        sheet.set(1, 0, new Cell("10", sheet)); // B1

        String[][] expected = {
                {"5", "10"},
                {null, null}
        };
        assertArrayEquals(expected, sheet.evalAll(), "Expected evaluation of all cells to match input values");
    }

    @Test
    void depth() {
        Spreadsheet sheet = new Spreadsheet(3, 3);
        sheet.set(0, 0, new Cell("5", sheet)); // A1
        sheet.set(1, 0, new Cell("=A1+1", sheet)); // B1
        sheet.set(2, 0, new Cell("=B1*2", sheet)); // C1

        int[][] expectedDepth = {
                {0, 1, 2},
                {0, 0, 0},
                {0, 0, 0}
        };
        assertArrayEquals(expectedDepth, sheet.depth(), "Expected depth calculations to reflect formula dependencies");
    }
}
