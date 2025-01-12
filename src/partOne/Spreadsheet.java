package partOne;

public class Spreadsheet {
    private final int width;
    private final int height;
    private final Cell[][] cells;

    public Spreadsheet(int width, int height) {
        if (width > 26 || height > 100) { // Limit the spreadsheet dimensions to 26 columns and 100 rows
            throw new IllegalArgumentException("partOne.Spreadsheet dimensions exceed allowed limits: 26x100");
        }
        this.width = width;
        this.height = height;
        this.cells = new Cell[width][height]; // Initialize a 2D array to store cells
    }

    public void set(int x, int y, Cell c) {
        cells[x][y] = c; // Assign a cell to a specific position in the spreadsheet
    }

    public Cell get(int x, int y) {
        return cells[x][y]; // Retrieve a cell from a specific position
    }

    public int xCell(String c) {
        if (c.length() < 2 || !Character.isDigit(c.charAt(1))) { // Validate reference format
            return -1;
        }
        char col = c.charAt(0);
        if (col < 'A' || col > 'Z') { // Ensure column reference is within 'A' to 'Z'
            return -1;
        }
        return col - 'A'; // Convert column letter to zero-based index
    }

    public int yCell(String c) {
        try {
            if (Integer.parseInt(c.substring(1)) - 1 >= 0 && Integer.parseInt(c.substring(1)) - 1 < 100) {
                return Integer.parseInt(c.substring(1)) - 1;
            }
            else
                return -1; // Convert row number (1-based) to zero-based index
        } catch (NumberFormatException e) {
            return -1; // Handle invalid row numbers
        }
    }

    public int width() {
        return width;
    }

    public int height() {
        return height;
    }

    public String eval(int x, int y) {
        Cell cell = get(x, y);
        if (cell == null) {
            return null; // Return null if the cell is empty
        } else {
            return cell.evaluateAsString(); // Evaluate and return the cell's value as a string
        }
    }

    public String[][] evalAll() {
        String[][] result = new String[height][width]; // Initialize result array
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                Cell cell = cells[j][i];
                if (cell == null) {
                    result[i][j] = null; // Handle empty cells
                } else {
                    result[i][j] = eval(j, i); // Evaluate each cell
                }
            }
        }
        return result;
    }

    public int[][] depth() {
        int[][] result = new int[height][width]; // Initialize result array for cell depths
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                Cell cell = cells[j][i];
                if (cell == null) {
                    result[i][j] = 0; // Depth is 0 for empty cells
                } else {
                    result[i][j] = calculateDepth(cell); // Calculate depth of non-empty cells
                }
            }
        }
        return result;
    }

    private int calculateDepth(Cell cell) {
        if (cell == null || cell.isNumber(cell.getValue()) || cell.isText(cell.getValue())) {
            return 0; // Depth is 0 for numbers and plain text cells
        }

        String expression = cell.getValue().substring(1); // Strip leading '=' from formula
        int maxDepth = 0;

        for (String token : expression.split("[+\\-*/()]") ) { // Split expression by operators and parentheses
            token = token.trim();
            if (!token.isEmpty() && token.matches("[A-Z]+\\d+")) { // Check if token is a valid cell reference
                int x = xCell(token);
                int y = yCell(token);

                if (x >= 0 && y >= 0) {
                    Cell referencedCell = get(x, y);

                    if (referencedCell != null) {
                        if (referencedCell == cell) { // Detect self-reference cycles
                            return -1; // Cycle detected, return -1
                        }

                        int depth = calculateDepth(referencedCell); // Recursively calculate depth for referenced cells
                        maxDepth = Math.max(maxDepth, depth); // Update maximum depth
                    }
                }
            }
        }

        return maxDepth + 1; // Add 1 for the current cell's depth
    }
}