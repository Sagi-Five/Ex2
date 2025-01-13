// SCell.java
package assignments.ex2;

/**
 * Implementation of SCell class, representing a single cell in the spreadsheet.
 */
public class SCell implements Cell {
    private String line;
    private int type;
    private int order;
    private String computedValue;

    /**
     * Constructor to initialize the cell with the given value.
     * @param s The value to set for the cell.
     */
    public SCell(String s) {
        setData(s);
    }

    @Override
    public String getData() {
        return line;
    }

    @Override
    public void setData(String s) {
        line = s;
        computedValue = null;
        if (s.startsWith("=")) {
            if (isValidFormula(s)) {
                type = Ex2Utils.FORM;
            } else {
                type = Ex2Utils.ERR_FORM_FORMAT;
                computedValue = Ex2Utils.ERR_FORM;
            }
        } else if (isNumber(s)) {
            type = Ex2Utils.NUMBER;
            computedValue = s;
        } else {
            type = Ex2Utils.TEXT;
        }
    }

    private boolean isNumber(String s) {
        try {
            Double.parseDouble(s);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private boolean isValidFormula(String s) {
        String formula = s.substring(1).replaceAll("\\s", "").toUpperCase();

        if (formula.isEmpty()) {
            return false;
        }

        int balance = 0;
        for (char c : formula.toCharArray()) {
            if (c == '(') {
                balance++;
            } else if (c == ')') {
                balance--;
                if (balance < 0) {
                    return false;
                }
            }
        }

        if (balance != 0) {
            return false;
        }

        return formula.matches("([A-Z]+\\d+|\\d+\\.\\d+|\\d+|\\(.*\\))([+\\-*/]([A-Z]+\\d+|\\d+\\.\\d+|\\d+|\\(.*\\)))*");
    }

    @Override
    public int getType() {
        return type;
    }

    @Override
    public void setType(int t) {
        type = t;
    }

    @Override
    public int getOrder() {
        return order;
    }

    @Override
    public void setOrder(int t) {
        order = t;
    }

    public void setComputedValue(String value) {
        computedValue = value;
    }

    public void computeValue(Ex2Sheet sheet) {
        if (type != Ex2Utils.FORM) {
            return;
        }

        try {
            String formula = line.substring(1).replaceAll("\\s", "").toUpperCase();
            computedValue = String.valueOf(evaluateFormula(formula, sheet));
        } catch (Exception e) {
            type = Ex2Utils.ERR_FORM_FORMAT;
            computedValue = Ex2Utils.ERR_FORM;
        }
    }

    private double evaluateFormula(String formula, Ex2Sheet sheet) {
        while (formula.contains("(")) {
            int openIndex = formula.lastIndexOf('(');
            int closeIndex = formula.indexOf(')', openIndex);
            if (closeIndex == -1) {
                throw new IllegalArgumentException("Unmatched parentheses in formula");
            }
            String subExpression = formula.substring(openIndex + 1, closeIndex);
            double subResult = evaluateSimpleFormula(subExpression, sheet);
            formula = formula.substring(0, openIndex) + subResult + formula.substring(closeIndex + 1);
        }
        return evaluateSimpleFormula(formula, sheet);
    }

    private double evaluateSimpleFormula(String formula, Ex2Sheet sheet) {
        double result = 0;
        double currentNumber = 0;
        char lastOperator = '+';
        boolean hasNumber = false;

        for (int i = 0; i < formula.length(); i++) {
            char c = formula.charAt(i);

            if (Character.isDigit(c) || c == '.') {
                StringBuilder number = new StringBuilder();
                while (i < formula.length() && (Character.isDigit(formula.charAt(i)) || formula.charAt(i) == '.')) {
                    number.append(formula.charAt(i));
                    i++;
                }
                i--;
                currentNumber = Double.parseDouble(number.toString());
                hasNumber = true;
            } else if (Character.isLetter(c)) {
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
            } else if ("+-*/".indexOf(c) != -1) {
                if (hasNumber) {
                    result = applyOperator(result, currentNumber, lastOperator);
                    hasNumber = false;
                }
                lastOperator = c;
            }
        }

        if (hasNumber) {
            result = applyOperator(result, currentNumber, lastOperator);
        }

        return result;
    }

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
            return result;
        }
    }

    @Override
    public String toString() {
        return (computedValue != null) ? computedValue : line;
    }
}
