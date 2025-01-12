package partOne;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class CellTest {

    @Test
    void isText() {
        Spreadsheet sheet = new Spreadsheet(5, 5);
        Cell cell = new Cell("Hello World", sheet); // Text with spaces
        assertTrue(cell.isText(cell.getValue()), "Expected text with spaces to be recognized as text");

        cell.setValue("42"); // Numeric value
        assertFalse(cell.isText(cell.getValue()), "Expected numeric value not to be recognized as text");

        cell.setValue("=(A1+A10)"); // Formula
        assertFalse(cell.isText(cell.getValue()), "Expected formula not to be recognized as text");
    }

    @Test
    void isNumber() {
        Spreadsheet sheet = new Spreadsheet(5, 5);
        Cell cell = new Cell("12345", sheet);
        assertTrue(cell.isNumber(cell.getValue()), "Expected integer value to be recognized as a number");

        cell.setValue("3.14159"); // Decimal
        assertTrue(cell.isNumber(cell.getValue()), "Expected decimal value to be recognized as a number");

        cell.setValue("text123"); // Mixed
        assertFalse(cell.isNumber(cell.getValue()), "Expected alphanumeric string not to be recognized as a number");

        cell.setValue("-1000"); // Negative number
        assertTrue(cell.isNumber(cell.getValue()), "Expected negative number to be recognized as a number");
    }

    @Test
    void isForm() {
        Spreadsheet sheet = new Spreadsheet(5, 5);
        Cell cell = new Cell("=A1+B1", sheet);
        assertTrue(cell.isForm(cell.getValue()), "Expected valid formula to be recognized");

        cell.setValue("InvalidFormula"); // Invalid format
        assertFalse(cell.isForm(cell.getValue()), "Expected invalid formula not to be recognized");
    }

    @Test
    void computeForm() {
        Spreadsheet sheet = new Spreadsheet(5, 5);
        sheet.set(0, 0, new Cell("4", sheet)); // A1
        sheet.set(1, 0, new Cell("5", sheet)); // B1

        Cell formulaCell = new Cell("=A1+B1", sheet);
        assertEquals(9.0, formulaCell.computeForm(formulaCell.getValue()), "Expected sum of A1 and B1 to be computed");

        formulaCell.setValue("=A1*B1");
        assertEquals(20.0, formulaCell.computeForm(formulaCell.getValue()), "Expected product of A1 and B1 to be computed");
    }

    @Test
    void evaluateAsString() {
        Spreadsheet sheet = new Spreadsheet(5, 5);
        Cell cell = new Cell("42", sheet);
        assertEquals("42", cell.evaluateAsString(), "Expected numeric value to be evaluated as string");

        cell.setValue("Hello");
        assertEquals("Hello", cell.evaluateAsString(), "Expected text value to be evaluated as string");

        cell.setValue("=A1+B1");
        sheet.set(0, 0, new Cell("3", sheet)); // A1
        sheet.set(1, 0, new Cell("7", sheet)); // B1
        assertEquals("10.0", cell.evaluateAsString(), "Expected formula to be computed and returned as string");
    }
}
