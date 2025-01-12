package partOne;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class CellTest {

    @Test
    void isText() {
        Spreadsheet sheet = new Spreadsheet(5, 5); // Create a spreadsheet with 5x5 dimensions
        Cell cell = new Cell("Hello", sheet); // Create a text cell
        assertTrue(cell.isText(cell.getValue())); // Check if the cell contains text

        cell.setValue("123"); // Set a numeric value
        assertFalse(cell.isText(cell.getValue())); // Ensure it's not identified as text

        cell.setValue("=A1+B1"); // Set a formula
        assertFalse(cell.isText(cell.getValue())); // Ensure it's not identified as text
    }

    @Test
    void isNumber() {
        Spreadsheet sheet = new Spreadsheet(5, 5); // Create a spreadsheet with 5x5 dimensions
        Cell cell = new Cell("123", sheet); // Create a cell with a numeric value
        assertTrue(cell.isNumber(cell.getValue())); // Verify the cell is identified as a number

        cell.setValue("Hello"); // Set a text value
        assertFalse(cell.isNumber(cell.getValue())); // Ensure it's not identified as a number

        cell.setValue("=A1+B1"); // Set a formula
        assertFalse(cell.isNumber(cell.getValue())); // Ensure it's not identified as a number
    }

    @Test
    void isForm() {
        Spreadsheet sheet = new Spreadsheet(5, 5); // Create a spreadsheet with 5x5 dimensions
        Cell cell = new Cell("=A1+B1", sheet); // Create a formula cell
        assertTrue(cell.isForm(cell.getValue())); // Verify the cell contains a valid formula

        cell.setValue("123"); // Set a numeric value
        assertFalse(cell.isForm(cell.getValue())); // Ensure it's not identified as a formula

        cell.setValue("Hello"); // Set a text value
        assertFalse(cell.isForm(cell.getValue())); // Ensure it's not identified as a formula
    }

    @Test
    void computeForm() {
        Spreadsheet sheet = new Spreadsheet(5, 5); // Create a spreadsheet with 5x5 dimensions
        Cell cell1 = new Cell("5", sheet); // Create a cell with numeric value
        Cell cell2 = new Cell("10", sheet); // Create another cell with numeric value
        sheet.set(0, 0, cell1); // Set the first cell in position A1
        sheet.set(1, 0, cell2); // Set the second cell in position B1

        Cell formulaCell = new Cell("=A1+B1", sheet); // Create a formula cell
        assertEquals(15.0, formulaCell.computeForm(formulaCell.getValue())); // Verify formula result
    }

    @Test
    void evaluateAsString() {
        Spreadsheet sheet = new Spreadsheet(5, 5); // Create a spreadsheet with 5x5 dimensions
        Cell cell = new Cell("123", sheet); // Create a cell with numeric value
        assertEquals("123", cell.evaluateAsString()); // Verify evaluation returns the numeric value

        cell.setValue("=1+2"); // Set a formula
        assertEquals("3.0", cell.evaluateAsString()); // Verify evaluation computes the formula result

        cell.setValue("Hello"); // Set a text value
        assertEquals("Hello", cell.evaluateAsString()); // Verify evaluation returns the text
    }
}