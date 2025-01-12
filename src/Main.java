
public class Main {
    public static void main(String[] args) {
        Spreadsheet sheet = new Spreadsheet(5, 5);

        sheet.set(0, 0, new Cell("=1+2", sheet)); // A1
        sheet.set(1, 0, new Cell("3", sheet));   // B1
        sheet.set(0, 1, new Cell("=A1+B1", sheet)); // A2

        System.out.println("A1: " + sheet.eval(0, 0)); // "3.0"
        System.out.println("B1: " + sheet.eval(1, 0)); // "3"
        System.out.println("A2: " + sheet.eval(0, 1)); // "6.0"

        System.out.println("Eval All:");
        String[][] evaluatedCells = sheet.evalAll();
        for (String[] row : evaluatedCells) {
            for (String cellValue : row) {
                System.out.print(cellValue + "\t");
            }
            System.out.println();
        }

        System.out.println("Depth of all cells:");
        int[][] depths = sheet.depth();
        for (int[] row : depths) {
            for (int cellDepth : row) {
                System.out.print(cellDepth + "\t");
            }
            System.out.println();
        }
    }
}
