import java.util.Arrays;

public class Problem {
    int nRows;
    int nColumns;
    Cell[][] board;
    int variablesNo;
    int valuedVariablesNo;

    public Problem(int rows, int columns, int[][] puzzle) {
        this.nRows = rows;
        this.nColumns = columns;
        board = new Cell[nRows][nColumns];
        variablesNo = 0;
        valuedVariablesNo = 0;
        for (int i = 0; i < nRows; i++) {
            for (int j = 0; j < nColumns; j++) {
                /* Constraint cell*/
                if (puzzle[i][j] > 0) {
                    Direction dir = null;
                    if (i == 0)
                        dir = Direction.VERTICAL;
                    else if (j == 0)
                        dir = Direction.HORIZONTAL;
                    else if (puzzle[i - 1][j] == -1) // upper cell is black
                        dir = Direction.VERTICAL;
                    else if (puzzle[i][j - 1] == -1) // left cell is black
                        dir = Direction.HORIZONTAL;

                    if (dir != null)
                        board[i][j] = new Constraint(i, j, puzzle[i][j], dir);
                    else
                        System.out.println("something goes wrong! (constraint's dir recognition)");
                }
                /* Variable cell */
                else if (puzzle[i][j] == 0) {
                    board[i][j] = new Variable(i, j, findRowConstraint(i, j), findColumnConstraint(i, j));
                    variablesNo++;
                }
                /* Black cell */
                else { // -1
                    board[i][j] = new Cell(i, j);
                }
            }
        }
        for (int i = 0; i < nRows; i++) {
            for (int j = 0; j < nColumns; j++) {
                if (board[i][j] instanceof Variable) {
                    Variable left;
                    Variable up;
                    if (puzzle[i][j - 1] > 0)
                        left = findRightmostVar(i, puzzle);
                    else
                        left = (Variable) board[i][j - 1];
                    if (puzzle[i - 1][j] > 0)
                        up = findBottommostVar(j, puzzle);
                    else {
                        up = (Variable) board[i - 1][j];
                    }
//                    System.out.println(board[i][j] + " ===> left : \n   " + left + " ===> up : \n   "  + up + "\n*********");
                    ((Variable) board[i][j]).setLeft(left);
                    ((Variable) board[i][j]).setUp(up);
                }
            }
        }
    }

    public void printBoard() {
        for (int i = 0; i < nRows; i++) {
            for (int j = 0; j < nColumns; j++) {
                System.out.println("************ i : " + i + "  j : " + j + " *****************");
                System.out.println(board[i][j]);
            }
        }
    }

    private Constraint findRowConstraint(int x, int y) {
        Constraint rowCons = null;
        for (int j = y - 1; j >= 0; j--) {
            if (board[x][j] instanceof Constraint)
                rowCons = (Constraint) board[x][j];
        }
        return rowCons;
    }

    private Constraint findColumnConstraint(int x, int y) {
        Constraint colCons = null;
        for (int i = x - 1; i >= 0; i--) {
            if (board[i][y] instanceof Constraint)
                colCons = (Constraint) board[i][y];
        }
        return colCons;
    }

    private Variable findRightmostVar(int x, int[][] puzzle) {
        Variable rightmost = null;
        for (int j = nColumns - 1; j >= 0; j--) {
            if (puzzle[x][j] == 0) {
                rightmost = (Variable) board[x][j];
                break;
            }
        }
        return rightmost;
    }

    private Variable findBottommostVar(int y, int[][] puzzle) {
        Variable bottommost = null;
        for (int i = nRows - 1; i >= 0; i--) {
            if (puzzle[i][y] == 0) {
                bottommost = (Variable) board[i][y];
                break;
            }
        }
        return bottommost;
    }

    @Override
    public String toString() {
        return "variablesNo=" + variablesNo +
                ", valuedVariablesNo=" + valuedVariablesNo;
    }

    public boolean isSatisfied() {
        return (variablesNo == valuedVariablesNo);
    }

    public void addValue(Cell var, int val) {
        valuedVariablesNo++;


    }

    public void removeValue() {
        valuedVariablesNo--;


    }
}
