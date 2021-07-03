import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

public class Problem {
    int nRows;
    int nColumns;
    Cell[][] board;
    int variablesNo;
    int assignedVariablesNo;
    ArrayList<Variable> unassignedVariables = new ArrayList<>();

    public Problem(int rows, int columns, int[][] puzzle) {
        this.nRows = rows;
        this.nColumns = columns;
        board = new Cell[nRows][nColumns];
        variablesNo = 0;
        assignedVariablesNo = 0;
        for (int i = 0; i < nRows; i++) {
            for (int j = 0; j < nColumns; j++) {
                /* Constraint cell*/
                if (puzzle[i][j] > 0) {
                    int numberOfRelatedVar = 0;
                    Direction dir = null;
                    if (i == 0) {
                        dir = Direction.VERTICAL;
                        numberOfRelatedVar = numberOfVar_VERTICAL(i, j, puzzle);
                    } else if (j == 0) {
                        dir = Direction.HORIZONTAL;
                        numberOfRelatedVar = numberOfVar_HORIZONTAL(i, j, puzzle);
                    } else if (puzzle[i - 1][j] == -1) { // upper cell is black
                        dir = Direction.VERTICAL;
                        numberOfRelatedVar = numberOfVar_VERTICAL(i, j, puzzle);
                    } else if (puzzle[i][j - 1] == -1) { // left cell is black
                        dir = Direction.HORIZONTAL;
                        numberOfRelatedVar = numberOfVar_HORIZONTAL(i, j, puzzle);
                    }

                    if (dir != null) {
                        board[i][j] = new Constraint(i, j, puzzle[i][j], dir, numberOfRelatedVar);
                    } else
                        System.out.println("something goes wrong! (constraint's dir recognition)");
                }
                /* Variable cell */
                else if (puzzle[i][j] == 0) {
                    board[i][j] = new Variable(i, j, findRowConstraint(i, j), findColumnConstraint(i, j));
                    variablesNo++;
                    unassignedVariables.add((Variable)board[i][j]);
                }
                /* Black cell */
                else { // -1
                    board[i][j] = new Cell(i, j);
                }
            }
        }
    }

    private int numberOfVar_HORIZONTAL(int x, int y, int[][] puzzle) {
        int count = 0;
        for (int j = y + 1; j < nColumns; j++) {
            if (puzzle[x][j] == 0)
                count++;
        }
        return count;
    }

    private int numberOfVar_VERTICAL(int x, int y, int[][] puzzle) {
        int count = 0;
        for (int i = x + 1; i < nRows; i++) {
            if (puzzle[i][y] == 0)
                count++;
        }
        return count;
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

    @Override
    public String toString() {
        return "variablesNo=" + variablesNo +
                ", assignedVariablesNo=" + assignedVariablesNo;
    }

    public Variable getUnassignedVariable() {
        for (int i = 0; i < nRows; i++) {
            for (int j = 0; j < nColumns; j++) {
                if (board[i][j] instanceof Variable && ((Variable) board[i][j]).getValue() == 0)
                    return (Variable) board[i][j];
            }
        }
        return null;
    }

    public Variable MRV() {
        int min_domain = Integer.MAX_VALUE;
        Variable minRemainingValue = null;
        for (Variable var : unassignedVariables) {
            if (var.getDomain().size() < min_domain) {
                min_domain = var.getDomain().size();
                minRemainingValue = var;
            }
        }
        return minRemainingValue;
    }

    public boolean isSatisfied() {
        return (variablesNo == assignedVariablesNo);
    }

    public boolean setValueIfIsConsistent(Variable var, int val) {
        Constraint rowConstraint = var.getRowConstraint();
        Constraint columnConstraint = var.getColumnConstraint();
        boolean allDiff = (rowConstraint.isDiff(val) && columnConstraint.isDiff(val));
        boolean allSum_r = false; // row
        boolean allSum_c = false; // column
        int remaining_r = rowConstraint.getValue() - rowConstraint.getAlreadySum();
        int remaining_c = columnConstraint.getValue() - columnConstraint.getAlreadySum();
        if (rowConstraint.numberOfUnassignedVar() > 1)
            allSum_r = (val < remaining_r);
        else if (rowConstraint.numberOfUnassignedVar() == 1)
            allSum_r = (val == remaining_r);
        if (columnConstraint.numberOfUnassignedVar() > 1)
            allSum_c = (val < remaining_c);
        else if (columnConstraint.numberOfUnassignedVar() == 1)
            allSum_c = (val == remaining_c);

        if (allDiff && allSum_r && allSum_c) {
            assignedVariablesNo++;
            rowConstraint.addValue(val);
            columnConstraint.addValue(val);
            var.setValue(val);
            unassignedVariables.remove(var);
            return true;
        } else
            return false;
    }

    public void removeValue(Variable var, int val) {
        Constraint rowConstraint = var.getRowConstraint();
        Constraint columnConstraint = var.getColumnConstraint();
        assignedVariablesNo--;
        rowConstraint.removeValue(val);
        columnConstraint.removeValue(val);
        unassignedVariables.add(var);
        var.setValue(0);
    }

    public void printSolution() {
        for (int i = 0; i < nRows; i++) {
            for (int j = 0; j < nColumns; j++) {
                if (board[i][j] instanceof Variable)
                    System.out.format("%3d", ((Variable) board[i][j]).getValue());
                else if (board[i][j] instanceof Constraint)
                    System.out.format("%3d", ((Constraint) board[i][j]).getValue());
                else
                    System.out.format("   ");
            }
            System.out.println();
        }
    }

    public void printVariables() {
        for (int i = 0; i < nRows; i++) {
            for (int j = 0; j < nColumns; j++) {
                if (board[i][j] instanceof Variable) {
                    ArrayList<Integer> domain = ((Variable) board[i][j]).getDomain();
                    int dom = 0;
                    for (Integer d : domain)
                        dom = 10 * dom + d;
                    System.out.format("[%2d, %9d]", ((Variable) board[i][j]).getValue(), dom);
                } else if (board[i][j] instanceof Constraint)
                    System.out.format("[%2d           ]", ((Constraint) board[i][j]).getValue());
                else
                    System.out.format("[             ]");
            }
            System.out.println();
        }
    }

    public void AC_3() {
        Queue<PairOfVar> queue = new LinkedList<>();
        // initialize the queue
        for (int i = 1; i < nRows; i++) {
            for (int j = 1; j < nColumns; j++) {
                if (board[i][j] instanceof Variable) {
                    Variable var = (Variable) board[i][j];
                    for (Variable v : GET_NEIGHBORS(var)) {
                        queue.add(new PairOfVar(var, v));
                    }
                }
            }
        }
        while (!queue.isEmpty()) {
            PairOfVar p = queue.poll();
            if (REMOVE_INCONSISTENT_VALUES(p)) {
                for (Variable v : GET_NEIGHBORS(p.var1)) {
                    queue.add(new PairOfVar(v, p.var1));
                }
            }
        }

    }

    private boolean REMOVE_INCONSISTENT_VALUES(PairOfVar p) {
        boolean removed = true;
        Variable var1 = p.var1;
        Variable var2 = p.var2;
        for (Integer x : var1.getDomain()) {
            removed = true;
            for (Integer y : var2.getDomain()) {
                if (!x.equals(y)) {
                    removed = false;
                    break;
                }
            }
            if (removed) {
                var1.removeFromDomain(x);
            }
        }
        return removed;
    }

    private ArrayList<Variable> GET_NEIGHBORS(Variable var) {
        ArrayList<Variable> neighbors = new ArrayList<>();
        Cell cell;
        for (int k = 0; k < nRows; k++) {
            cell = board[k][var.y];
            if (k != var.x && cell instanceof Variable && ((Variable) cell).getValue() == 0) {
                neighbors.add((Variable)cell);
            }
        }
        for (int l = 0; l < nColumns; l++) {
            cell = board[var.x][l];
            if (l != var.y && cell instanceof Variable && ((Variable) cell).getValue() == 0) {
                neighbors.add((Variable)cell);
            }
        }
        return neighbors;
    }
}

class PairOfVar{
    Variable var1;
    Variable var2;
    public PairOfVar(Variable var1, Variable var2){
        this.var1 = var1;
        this.var2 = var2;
    }
}