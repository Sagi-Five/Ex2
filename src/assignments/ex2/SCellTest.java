// TestSCell.java
package assignments.ex2;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the SCell class.
 */
class SCellTest {

    @Test
    void testSetDataAndGetData() {
        SCell cell = new SCell("Hello");
        assertEquals("Hello", cell.getData());

        cell.setData("=A1+B2");
        assertEquals("=A1+B2", cell.getData());
    }

    @Test
    void testSetComputedValue() {
        SCell cell = new SCell("=A1+B2");
        cell.setComputedValue("42");
        assertEquals("42", cell.toString());
    }

    @Test
    void testTypeDetection() {
        SCell cell = new SCell("123");
        assertEquals(Ex2Utils.NUMBER, cell.getType());

        cell.setData("Hello");
        assertEquals(Ex2Utils.TEXT, cell.getType());

        cell.setData("=A1+B2");
        assertEquals(Ex2Utils.FORM, cell.getType());
    }

    @Test
    void testComputeValue() {
        Ex2Sheet sheet = new Ex2Sheet(5, 5);
        sheet.set(0, 0, "2");
        sheet.set(1, 0, "3");

        SCell cell = new SCell("=A0+B0");
        cell.computeValue(sheet);

        assertEquals("5.0", cell.toString());
    }

    @Test
    void testInvalidFormula() {
        SCell cell = new SCell("=A1++B1");
        assertEquals(Ex2Utils.ERR_FORM, cell.toString());
    }
}