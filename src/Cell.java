public class Cell {
    private String value; // The value of the cell as a string
    private final Spreadsheet parentSheet; // Reference to the parent spreadsheet

    public Cell(String value, Spreadsheet parentSheet) {
        this.value = value;
        this.parentSheet = parentSheet;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public boolean isText(String text) {
        return !((isNumber(text) || isForm(text))); // Text is anything that's not a number or formula
    }

    public boolean isNumber(String text) {
        if (text == null || text.isEmpty()) {
            return false;
        }

        int i = 0;
        boolean hasDecimal = false;

        if (text.charAt(0) == '-') { // Check for negative sign
            i++;
        }

        while (i < text.length()) {
            char c = text.charAt(i);
            if (c == '.') {
                if (hasDecimal) {
                    return false; // Only one decimal point allowed
                }
                hasDecimal = true;
            } else if (c < '0' || c > '9') {
                return false; // Non-digit character invalidates the number
            }
            i++;
        }

        return i > 0; // Ensure there's at least one digit
    }

    public boolean isForm(String text) {
        if (text == null || text.isEmpty() || !text.startsWith("=")) {
            return false;
        }

        String trimmedExpression = text.substring(1).replaceAll("\\s", ""); // Remove spaces after '='

        if (trimmedExpression.contains("()")) { // Reject empty parentheses
            return false;
        }

        if (!areParenthesesBalanced(trimmedExpression)) { // Check for balanced parentheses
            return false;
        }

        for (int i = 1; i < trimmedExpression.length(); i++) {
            char prev = trimmedExpression.charAt(i - 1);
            char current = trimmedExpression.charAt(i);

            if (isOperator(prev) && isOperator(current)) { // Reject consecutive operators
                return false;
            }
        }

        for (int i = 0; i < trimmedExpression.length(); i++) {
            char c = trimmedExpression.charAt(i);
            if (!(Character.isDigit(c) || isOperator(c) || c == '(' || c == ')' || c == '.' || isCellReferencePart(c))) {
                return false; // Ensure valid characters in formula
            }
        }

        return true;
    }

    private boolean areParenthesesBalanced(String expression) {
        int balance = 0;
        for (char c : expression.toCharArray()) {
            if (c == '(') {
                balance++;
            } else if (c == ')') {
                balance--;
                if (balance < 0) { // More closing than opening parentheses
                    return false;
                }
            }
        }
        return balance == 0; // Ensure all parentheses are matched
    }

    private boolean isOperator(char c) {
        return c == '+' || c == '-' || c == '*' || c == '/'; // Recognize basic arithmetic operators
    }

    private boolean isCellReferencePart(char c) {
        return Character.isLetter(c) || Character.isDigit(c); // Letters and digits are valid in cell references
    }

    public Double computeForm(String form) {
        if (!isForm(form)) {
            throw new IllegalArgumentException("Invalid formula");
        }

        String expression = form.substring(1); // Remove leading '='
        return evaluateExpression(expression); // Evaluate the formula recursively
    }

    private Double evaluateExpression(String expression) {
        expression = expression.replaceAll("\\s", ""); // Remove whitespace
        return parseExpression(expression); // Parse and evaluate the expression
    }

    private Double parseExpression(String expression) {
        if (expression.isEmpty()) {
            throw new IllegalArgumentException("Empty expression");
        }

        if (expression.startsWith("(") && expression.endsWith(")")) { // Evaluate parenthesized expressions
            return parseExpression(expression.substring(1, expression.length() - 1));
        }

        for (int i = expression.length() - 1; i >= 0; i--) {
            char c = expression.charAt(i);
            if (c == '+' || c == '-' || c == '*' || c == '/') { // Handle arithmetic operators
                Double left = parseExpression(expression.substring(0, i));
                Double right = parseExpression(expression.substring(i + 1));
                if (c == '+') {
                    return left + right;
                } else if (c == '-') {
                    return left - right;
                } else if (c == '*') {
                    return left * right;
                } else {
                    if (right == 0) {
                        throw new ArithmeticException("Division by zero"); // Handle division by zero
                    }
                    return left / right;
                }
            }
        }

        if (Character.isLetter(expression.charAt(0))) { // Handle cell references
            Cell referencedCell = parentSheet.get(parentSheet.xCell(expression), parentSheet.yCell(expression));
            if (referencedCell == null) {
                throw new IllegalArgumentException("Referenced cell not found: " + expression);
            }
            return Double.parseDouble(referencedCell.evaluateAsString()); // Evaluate referenced cell
        }

        return Double.parseDouble(expression); // Parse as a numeric value
    }

    public String evaluateAsString() {
        if (isNumber(value)) {
            return value; // Return number values directly
        } else {
            if (isForm(value)) {
                return String.valueOf(computeForm(value)); // Compute and return formula results
            } else {
                return value; // Return text as-is
            }
        }
    }
}