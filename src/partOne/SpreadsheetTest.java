package partOne;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class SpreadsheetTest {

    @Test
    void xCell() {
        Spreadsheet sheet = new Spreadsheet(5, 5); // Create a spreadsheet with 5x5 dimensions
        assertEquals(0, sheet.xCell("A1")); // Verify column A corresponds to index 0
        assertEquals(1, sheet.xCell("B1")); // Verify column B corresponds to index 1
        assertEquals(-1, sheet.xCell("1A")); // Verify invalid format returns -1
    }

    @Test
    void yCell() {
        Spreadsheet sheet = new Spreadsheet(5, 5); // Create a spreadsheet with 5x5 dimensions
        assertEquals(0, sheet.yCell("A1")); // Verify row 1 corresponds to index 0
        assertEquals(9, sheet.yCell("A10")); // Verify row 10 corresponds to index 9
        assertEquals(-1, sheet.yCell("A")); // Verify invalid format returns -1
    }

    @Test
    void eval() {
        Spreadsheet sheet = new Spreadsheet(5, 5); // Create a spreadsheet with 5x5 dimensions
        Cell cell = new Cell("123", sheet); // Create a cell with numeric value
        sheet.set(0, 0, cell); // Set the cell in position A1
        assertEquals("123", sheet.eval(0, 0)); // Verify evaluation returns the cell value

        Cell formulaCell = new Cell("=1+2", sheet); // Create a formula cell
        sheet.set(1, 0, formulaCell); // Set the formula cell in position B1
        assertEquals("3.0", sheet.eval(1, 0)); // Verify evaluation computes the formula result
    }

    @Test
    void evalAll() {
        Spreadsheet sheet = new Spreadsheet(2, 2); // Create a 2x2 spreadsheet
        Cell cell1 = new Cell("5", sheet); // Create a cell with value 5
        Cell cell2 = new Cell("10", sheet); // Create another cell with value 10
        sheet.set(0, 0, cell1); // Set the first cell in position A1
        sheet.set(1, 0, cell2); // Set the second cell in position B1

        String[][] expected = {
                {"5", "10"}, // Expected values for the first row
                {null, null}   // Second row is empty
        };
        assertArrayEquals(expected, sheet.evalAll()); // Verify all cell evaluations
    }

    @Test
    void depth() {
        Spreadsheet sheet = new Spreadsheet(5, 5); // Create a spreadsheet with 5x5 dimensions
        Cell cell1 = new Cell("5", sheet); // Create a cell with value 5
        Cell cell2 = new Cell("=A1", sheet); // Create a formula referencing A1
        Cell cell3 = new Cell("=B1+1", sheet); // Create a formula referencing B1

        sheet.set(0, 0, cell1); // Set cell1 in position A1
        sheet.set(1, 0, cell2); // Set cell2 in position B1
        sheet.set(2, 0, cell3); // Set cell3 in position C1

        int[][] expectedDepth = {
                {0, 1, 2, 0, 0}, // Depths for the first row
                {0, 0, 0, 0, 0}, // Remaining rows are empty
                {0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0}
        };

        assertArrayEquals(expectedDepth, sheet.depth()); // Verify calculated depths
    }
}
