// Ex2Sheet.java
package assignments.ex2;

import java.io.*;

/**
 * Implementation of the Ex2Sheet class, representing a spreadsheet.
 */
public class Ex2Sheet implements Sheet {
    private final Cell[][] table; // 2D array representing the cells of the spreadsheet.

    /**
     * Constructor to initialize the spreadsheet with specified dimensions.
     * @param width Number of xs.
     * @param height Number of rows.
     */
    public Ex2Sheet(int width, int height) {
        table = new SCell[width][height]; // Create a 2D array of cells.
        for (int i = 0; i < width; i++) { // Loop through each x.
            for (int j = 0; j < height; j++) { // Loop through each row.
                table[i][j] = new SCell(Ex2Utils.EMPTY_CELL); // Initialize each cell as empty.
            }
        }
        eval(); // Evaluate the spreadsheet initially.
    }

    /**
     * Default constructor using predefined dimensions from Ex2Utils.
     */
    public Ex2Sheet() {
        this(Ex2Utils.WIDTH, Ex2Utils.HEIGHT);
    }

    /**
     * Get the value of a cell at specified coordinates as a string.
     * @param x x index.
     * @param y Row index.
     * @return The string representation of the cell's value.
     */
    @Override
    public String value(int x, int y) {
        Cell cell = get(x, y);
        return (cell != null) ? cell.toString() : Ex2Utils.EMPTY_CELL;
    }

    /**
     * Get the cell object at specified coordinates.
     * @param x x index.
     * @param y Row index.
     * @return The cell object, or null if out of bounds.
     */
    @Override
    public Cell get(int x, int y) {
        return isIn(x, y) ? table[x][y] : null;
    }

    /**
     * Get a cell object by its string-based coordinates (e.g., "A1").
     * @param cords String representation of the coordinates.
     * @return The cell object, or null if out of bounds.
     */
    @Override
    public Cell get(String cords) {
        cords = cords.toUpperCase(); // Normalize input to uppercase.
        int col = cords.charAt(0) - 'A'; // Extract x index.
        int row = Integer.parseInt(cords.substring(1)); // Extract row index.
        return get(col, row);
    }

    /**
     * Get the width (number of xs) of the spreadsheet.
     * @return Number of xs.
     */
    @Override
    public int width() {
        return table.length;
    }

    /**
     * Get the height (number of rows) of the spreadsheet.
     * @return Number of rows.
     */
    @Override
    public int height() {
        return table[0].length;
    }

    /**
     * Set the content of a cell at specified coordinates.
     * @param x x index.
     * @param y Row index.
     * @param s The new content for the cell.
     */
    @Override
    public void set(int x, int y, String s) {
        if (isIn(x, y)) { // Check if coordinates are within bounds.
            table[x][y] = new SCell(s); // Update the cell with new content.
            eval(); // Re-evaluate the spreadsheet.
        }
    }

    /**
     * Evaluate all cells in the spreadsheet and update their values and types.
     */
    @Override
    public void eval() {
        int[][] depthArray = depth(); // Compute the dependency depth of all cells.
        for (int x = 0; x < width(); x++) {
            for (int y = 0; y < height(); y++) {
                Cell cell = get(x, y);
                if (cell instanceof SCell) {
                    SCell scell = (SCell) cell;
                    if (depthArray[x][y] == Ex2Utils.ERR) { // Handle circular references.
                        scell.setType(Ex2Utils.ERR_CYCLE_FORM);
                        scell.setComputedValue(Ex2Utils.ERR_CYCLE);
                    } else {
                        scell.setOrder(depthArray[x][y]); // Set the order of evaluation.
                        if (scell.getType() == Ex2Utils.FORM) {
                            scell.computeValue(this); // Compute the value if it is a formula.
                        }
                    }
                }
            }
        }
    }

    /**
     * Evaluate a specific cell and return its computed value as a string.
     * @param x x index.
     * @param y Row index.
     * @return The computed value of the cell as a string.
     */
    @Override
    public String eval(int x, int y) {
        Cell cell = get(x, y);
        if (cell instanceof SCell) {
            SCell scell = (SCell) cell;
            scell.computeValue(this); // Compute the cell value dynamically.
            return scell.toString();
        }
        return Ex2Utils.EMPTY_CELL; // Return empty if not an SCell.
    }

    /**
     * Check if the given coordinates are within the spreadsheet bounds.
     * @param x x index.
     * @param y Row index.
     * @return True if the coordinates are within bounds, false otherwise.
     */
    @Override
    public boolean isIn(int x, int y) {
        return x >= 0 && x < width() && y >= 0 && y < height();
    }

    /**
     * Compute the dependency depth of all cells in the spreadsheet.
     * @return A 2D array containing the depth of each cell.
     */
    @Override
    public int[][] depth() {
        int[][] depths = new int[width()][height()];
        boolean[][] visited = new boolean[width()][height()];
        boolean[][] inPath = new boolean[width()][height()];

        for (int x = 0; x < width(); x++) {
            for (int y = 0; y < height(); y++) {
                if (!visited[x][y]) {
                    depths[x][y] = findDependencyDepth(x, y, visited, inPath, depths);
                }
            }
        }

        return depths;
    }

    /**
     * Recursively calculate the dependency depth of a cell.
     * @param x x index of the cell.
     * @param y Row index of the cell.
     * @param hasBeenVisited Tracks visited cells to prevent redundant calculations.
     * @param currentPath Tracks cells currently in the recursion stack to detect cycles.
     * @param calculatedDepths The array storing calculated depths for each cell.
     * @return The calculated depth, or Ex2Utils.ERR in case of a circular reference.
     */
    private int findDependencyDepth(int x, int y, boolean[][] hasBeenVisited, boolean[][] currentPath, int[][] calculatedDepths) {
        if (currentPath[x][y]) {
            return Ex2Utils.ERR; // Circular reference detected.
        }
        if (hasBeenVisited[x][y]) {
            return calculatedDepths[x][y]; // Return previously calculated depth.
        }

        currentPath[x][y] = true;
        hasBeenVisited[x][y] = true;

        Cell currentCell = get(x, y);
        if (!(currentCell instanceof SCell)) {
            currentPath[x][y] = false;
            return 0; // Non-formula cells have a depth of 0.
        }

        SCell spreadsheetCell = (SCell) currentCell;
        if (spreadsheetCell.getType() != Ex2Utils.FORM) {
            currentPath[x][y] = false;
            return 0; // Only formula cells contribute to depth.
        }

        int maximumDepth = 0;
        String formulaContent = spreadsheetCell.getData().substring(1).replaceAll("\\s", ""); // Remove spaces and prefix.
        String[] formulaParts = formulaContent.split("[+\\-*/()]"); // Split the formula into components.

        for (int index = 0; index < formulaParts.length; index++) {
            String part = formulaParts[index].trim().toUpperCase();
            if (part.matches("[A-Z]+\\d+")) { // Check if part is a valid cell reference.
                int refx = part.charAt(0) - 'A';
                int refRow = Integer.parseInt(part.substring(1));
                if (isIn(refx, refRow)) {
                    int dependencyDepth = findDependencyDepth(refx, refRow, hasBeenVisited, currentPath, calculatedDepths);
                    if (dependencyDepth == Ex2Utils.ERR) {
                        currentPath[x][y] = false;
                        return Ex2Utils.ERR; // Propagate error for circular references.
                    }
                    if (dependencyDepth > maximumDepth) {
                        maximumDepth = dependencyDepth;
                    }
                }
            }
        }

        currentPath[x][y] = false;
        calculatedDepths[x][y] = maximumDepth + 1; // Add one to the maximum dependency depth.
        return calculatedDepths[x][y];
    }

    /**
     * Save the spreadsheet's content to a file.
     * @param fileName The file name to save the spreadsheet to.
     * @throws IOException If an I/O error occurs.
     */
    @Override
    public void save(String fileName) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            writer.write("I2CS ArielU: SpreadSheet (Ex2) assignment\n"); // Header line.
            for (int x = 0; x < width(); x++) {
                for (int y = 0; y < height(); y++) {
                    Cell cell = get(x, y);
                    if (cell != null && !cell.getData().equals(Ex2Utils.EMPTY_CELL)) {
                        writer.write(x + "," + y + "," + cell.getData() + "\n");
                    }
                }
            }
        }
    }

    /**
     * Load spreadsheet content from a file.
     * @param fileName The file name to load the spreadsheet from.
     * @throws IOException If an I/O error occurs.
     */
    @Override
    public void load(String fileName) throws IOException {
        for (int i = 0; i < table.length; i++) {
            for (int j = 0; j < table[0].length; j++) {
                table[i][j] = new SCell(Ex2Utils.EMPTY_CELL); // Reset all cells to empty.
            }
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            reader.readLine(); // Skip the header line.
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",", 3); // Split line into components.
                if (parts.length >= 3) {
                    int x = Integer.parseInt(parts[0]);
                    int y = Integer.parseInt(parts[1]);
                    String data = parts[2];
                    set(x, y, data); // Set the cell with loaded data.
                }
            }
        }
    }
}
