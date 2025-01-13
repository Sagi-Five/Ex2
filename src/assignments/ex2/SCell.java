// SCell.java
package assignments.ex2;

/**
 * Implementation of SCell class, representing a single cell in the spreadsheet.
 */
public class SCell implements Cell {
    private String line; // Raw content of the cell.
    private int type; // Type of the cell (e.g., text, number, formula).
    private int order; // Evaluation order for dependency resolution.
    private String computedValue; // The computed value of the cell, if applicable.

    /**
     * Constructor to initialize the cell with the given value.
     * @param s The value to set for the cell.
     */
    public SCell(String s) {
        setData(s);
    }

    /**
     * Get the raw content of the cell.
     * @return The raw content as a string.
     */
    @Override
    public String getData() {
        return line;
    }

    /**
     * Set the raw content of the cell and determine its type.
     * @param s The new content for the cell.
     */
    @Override
    public void setData(String s) {
        line = s;
        computedValue = null; // Reset computed value.
        if (s.startsWith("=")) { // Formula detection.
            if (isValidFormula(s)) { // Validate the formula.
                type = Ex2Utils.FORM;
            } else { // Invalid formula.
                type = Ex2Utils.ERR_FORM_FORMAT;
                computedValue = Ex2Utils.ERR_FORM;
            }
        } else if (isNumber(s)) { // Check if the input is a valid number.
            type = Ex2Utils.NUMBER;
            computedValue = s;
        } else { // Otherwise, treat it as text.
            type = Ex2Utils.TEXT;
        }
    }

    /**
     * Check if the given string represents a valid number.
     * @param s The string to check.
     * @return True if the string is a valid number, false otherwise.
     */
    private boolean isNumber(String s) {
        try {
            Double.parseDouble(s);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * Validate if a string is a syntactically correct formula.
     * @param s The formula to validate.
     * @return True if valid, false otherwise.
     */
    private boolean isValidFormula(String s) {
        String formula = s.substring(1).replaceAll("\\s", "").toUpperCase(); // Normalize input.

        if (formula.isEmpty()) {
            return false; // Empty formula is invalid.
        }

        int balance = 0; // Track parentheses balance.
        for (char c : formula.toCharArray()) {
            if (c == '(') {
                balance++;
            } else if (c == ')') {
                balance--;
                if (balance < 0) { // Unmatched closing parenthesis.
                    return false;
                }
            }
        }

        if (balance != 0) { // Ensure all parentheses are matched.
            return false;
        }

        // Check formula syntax with regex.
        return formula.matches("([A-Z]+\\d+|\\d+\\.\\d+|\\d+|\\(.*\\))([+\\-*/]([A-Z]+\\d+|\\d+\\.\\d+|\\d+|\\(.*\\)))*");
    }

    /**
     * Get the type of the cell.
     * @return The type of the cell.
     */
    @Override
    public int getType() {
        return type;
    }

    /**
     * Set the type of the cell.
     * @param t The new type for the cell.
     */
    @Override
    public void setType(int t) {
        type = t;
    }

    /**
     * Get the evaluation order of the cell.
     * @return The order of the cell.
     */
    @Override
    public int getOrder() {
        return order;
    }

    /**
     * Set the evaluation order of the cell.
     * @param t The new order.
     */
    @Override
    public void setOrder(int t) {
        order = t;
    }

    /**
     * Set the computed value of the cell.
     * @param value The computed value to set.
     */
    public void setComputedValue(String value) {
        computedValue = value;
    }

    /**
     * Compute the value of the cell if it is a formula.
     * @param sheet The spreadsheet context for evaluating dependencies.
     */
    public void computeValue(Ex2Sheet sheet) {
        if (type != Ex2Utils.FORM) {
            return; // Only compute formulas.
        }

        try {
            String formula = line.substring(1).replaceAll("\\s", "").toUpperCase(); // Normalize formula.
            computedValue = String.valueOf(evaluateFormula(formula, sheet)); // Evaluate formula.
        } catch (Exception e) {
            type = Ex2Utils.ERR_FORM_FORMAT; // Handle formula errors.
            computedValue = Ex2Utils.ERR_FORM;
        }
    }

    /**
     * Evaluate a formula string by resolving dependencies and computing values.
     * @param formula The formula string to evaluate.
     * @param sheet The spreadsheet context.
     * @return The computed value of the formula.
     */
    private double evaluateFormula(String formula, Ex2Sheet sheet) {
        while (formula.contains("(")) { // Resolve parentheses.
            int openIndex = formula.lastIndexOf('(');
            int closeIndex = formula.indexOf(')', openIndex);
            if (closeIndex == -1) {
                throw new IllegalArgumentException("Unmatched parentheses in formula");
            }
            String subExpression = formula.substring(openIndex + 1, closeIndex); // Extract inner expression.
            double subResult = evaluateSimpleFormula(subExpression, sheet); // Evaluate inner expression.
            formula = formula.substring(0, openIndex) + subResult + formula.substring(closeIndex + 1); // Replace.
        }
        return evaluateSimpleFormula(formula, sheet); // Evaluate the flattened formula.
    }

    /**
     * Evaluate a simple formula without parentheses.
     * @param formula The formula string to evaluate.
     * @param sheet The spreadsheet context.
     * @return The computed value of the formula.
     */
    private double evaluateSimpleFormula(String formula, Ex2Sheet sheet) {
        double result = 0;
        double currentNumber = 0;
        char lastOperator = '+'; // Start with addition.
        boolean hasNumber = false;

        for (int i = 0; i < formula.length(); i++) {
            char c = formula.charAt(i);

            if (Character.isDigit(c) || c == '.') { // Parse numbers.
                StringBuilder number = new StringBuilder();
                while (i < formula.length() && (Character.isDigit(formula.charAt(i)) || formula.charAt(i) == '.')) {
                    number.append(formula.charAt(i));
                    i++;
                }
                i--;
                currentNumber = Double.parseDouble(number.toString());
                hasNumber = true;
            } else if (Character.isLetter(c)) { // Parse cell references.
                StringBuilder cellRef = new StringBuilder();
                while (i < formula.length() && Character.isLetterOrDigit(formula.charAt(i))) {
                    cellRef.append(formula.charAt(i));
                    i++;
                }
                i--;
                String normalizedCellRef = cellRef.toString().toUpperCase();
                Cell cell = sheet.get(normalizedCellRef);
                if (cell == null || !(cell instanceof SCell)) {
                    throw new IllegalArgumentException("Invalid cell reference");
                }
                currentNumber = Double.parseDouble(cell.toString());
                hasNumber = true;
            } else if ("+-*/".indexOf(c) != -1) { // Handle operators.
                if (hasNumber) {
                    result = applyOperator(result, currentNumber, lastOperator);
                    hasNumber = false;
                }
                lastOperator = c; // Update the operator.
            }
        }

        if (hasNumber) { // Apply the last number.
            result = applyOperator(result, currentNumber, lastOperator);
        }

        return result;
    }

    /**
     * Apply an arithmetic operation.
     * @param result The current result.
     * @param currentNumber The number to apply the operation with.
     * @param operator The operator to use.
     * @return The updated result after applying the operation.
     */
    private double applyOperator(double result, double currentNumber, char operator) {
        if (operator == '+') {
            return result + currentNumber;
        } else if (operator == '-') {
            return result - currentNumber;
        } else if (operator == '*') {
            return result * currentNumber;
        } else if (operator == '/') {
            if (currentNumber == 0) {
                throw new ArithmeticException("Division by zero");
            }
            return result / currentNumber;
        } else {
            return result; // No operation.
        }
    }

    /**
     * Get the string representation of the cell.
     * @return The computed value if available, otherwise the raw content.
     */
    @Override
    public String toString() {
        return (computedValue != null) ? computedValue : line;
    }
}
