// Ex2Sheet.java
package assignments.ex2;

import java.io.*;

/**
 * Implementation of the Ex2Sheet class, representing a spreadsheet.
 */
public class Ex2Sheet implements Sheet {
    private final Cell[][] table;

    /**
     * Constructor to initialize the spreadsheet with specified dimensions.
     * @param width Number of columns.
     * @param height Number of rows.
     */
    public Ex2Sheet(int width, int height) {
        table = new SCell[width][height];
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                table[i][j] = new SCell(Ex2Utils.EMPTY_CELL);
            }
        }
        eval();
    }

    /**
     * Default constructor using predefined dimensions.
     */
    public Ex2Sheet() {
        this(Ex2Utils.WIDTH, Ex2Utils.HEIGHT);
    }

    @Override
    public String value(int x, int y) {
        Cell cell = get(x, y);
        return (cell != null) ? cell.toString() : Ex2Utils.EMPTY_CELL;
    }

    @Override
    public Cell get(int x, int y) {
        return isIn(x, y) ? table[x][y] : null;
    }

    @Override
    public Cell get(String cords) {
        cords = cords.toUpperCase();
        int col = cords.charAt(0) - 'A';
        int row = Integer.parseInt(cords.substring(1));
        return get(col, row);
    }

    @Override
    public int width() {
        return table.length;
    }

    @Override
    public int height() {
        return table[0].length;
    }

    @Override
    public void set(int x, int y, String s) {
        if (isIn(x, y)) {
            table[x][y] = new SCell(s);
            eval();
        }
    }

    @Override
    public void eval() {
        int[][] depthArray = depth();
        for (int x = 0; x < width(); x++) {
            for (int y = 0; y < height(); y++) {
                Cell cell = get(x, y);
                if (cell instanceof SCell) {
                    SCell scell = (SCell) cell;
                    if (depthArray[x][y] == Ex2Utils.ERR) {
                        scell.setType(Ex2Utils.ERR_CYCLE_FORM);
                        scell.setComputedValue(Ex2Utils.ERR_CYCLE);
                    } else {
                        scell.setOrder(depthArray[x][y]);
                        if (scell.getType() == Ex2Utils.FORM) {
                            scell.computeValue(this);
                        }
                    }
                }
            }
        }
    }

    @Override
    public String eval(int x, int y) {
        Cell cell = get(x, y);
        if (cell instanceof SCell) {
            SCell scell = (SCell) cell;
            scell.computeValue(this);
            return scell.toString();
        }
        return Ex2Utils.EMPTY_CELL;
    }

    @Override
    public boolean isIn(int x, int y) {
        return x >= 0 && x < width() && y >= 0 && y < height();
    }

    @Override
    public int[][] depth() {
        int[][] depths = new int[width()][height()];
        boolean[][] visited = new boolean[width()][height()];
        boolean[][] inPath = new boolean[width()][height()];

        for (int x = 0; x < width(); x++) {
            for (int y = 0; y < height(); y++) {
                if (!visited[x][y]) {
                    depths[x][y] = calculateDepth(x, y, visited, inPath, depths);
                }
            }
        }

        return depths;
    }

    private int calculateDepth(int x, int y, boolean[][] visited, boolean[][] inPath, int[][] depths) {
        if (inPath[x][y]) {
            return Ex2Utils.ERR;
        }
        if (visited[x][y]) {
            return depths[x][y];
        }

        inPath[x][y] = true;
        visited[x][y] = true;

        Cell cell = get(x, y);
        if (!(cell instanceof SCell)) {
            inPath[x][y] = false;
            return 0;
        }

        SCell scell = (SCell) cell;
        if (scell.getType() != Ex2Utils.FORM) {
            inPath[x][y] = false;
            return 0;
        }

        int maxDepth = 0;
        String data = scell.getData().substring(1).replaceAll("\\s", "");
        String[] tokens = data.split("[+\\-*/()]");

        for (String token : tokens) {
            token = token.trim().toUpperCase();
            if (token.matches("[A-Z]+\\d+")) {
                int col = token.charAt(0) - 'A';
                int row = Integer.parseInt(token.substring(1));
                if (isIn(col, row)) {
                    int depDepth = calculateDepth(col, row, visited, inPath, depths);
                    if (depDepth == Ex2Utils.ERR) {
                        inPath[x][y] = false;
                        return Ex2Utils.ERR;
                    }
                    maxDepth = Math.max(maxDepth, depDepth);
                }
            }
        }

        inPath[x][y] = false;
        depths[x][y] = maxDepth + 1;
        return depths[x][y];
    }

    @Override
    public void save(String fileName) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            writer.write("I2CS ArielU: SpreadSheet (Ex2) assignment\n");
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

    @Override
    public void load(String fileName) throws IOException {
        for (int i = 0; i < table.length; i++) {
            for (int j = 0; j < table[0].length; j++) {
                table[i][j] = new SCell(Ex2Utils.EMPTY_CELL);
            }
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            reader.readLine(); // Skip the header line
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",", 3);
                if (parts.length >= 3) {
                    int x = Integer.parseInt(parts[0]);
                    int y = Integer.parseInt(parts[1]);
                    String data = parts[2];
                    set(x, y, data);
                }
            }
        }
    }
}
