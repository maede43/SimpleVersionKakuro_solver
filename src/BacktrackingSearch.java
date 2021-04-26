import java.util.ArrayList;

public class BacktrackingSearch {
    private final Problem problem;

    public BacktrackingSearch(Problem problem) {
        this.problem = problem;
    }

    public void search() {
        if (back_track() == ExitStatus.SOLUTION)
            problem.printSolution();
        else
            System.out.println("FAILURE");
    }

    private ExitStatus back_track() {
//        problem.printSolution();
        if (problem.isSatisfied()) {
            return ExitStatus.SOLUTION;
        }
//        problem.printVariables();
        Variable var = problem.MRV();
        ArrayList<Integer> domain = var.getDomain();
        for (Integer value : domain) {
            if (problem.setValueIfIsConsistent(var, value)) {
                if (forwardChecking(var, value)) {
                    ExitStatus result = back_track();
                    if (result == ExitStatus.SOLUTION)
                        return ExitStatus.SOLUTION;
                }
                forwardChecking_undo(var);
                problem.removeValue(var, value);
            }
        }
        return ExitStatus.FAILURE;
    }

    // return : boolean allIsNotEmpty
    private boolean forwardChecking(Variable var, int value) {
        Cell cell;
        Constraint rCons = var.getRowConstraint();
        Constraint cCons = var.getColumnConstraint();
        int upperBound = cCons.upperBound_calculate(cCons.getValue() - cCons.getAlreadySum(), cCons.numberOfUnassignedVar());
        int lowerBound = cCons.lowerBound_calculate(cCons.getValue() - cCons.getAlreadySum(), cCons.numberOfUnassignedVar());
        for (int i = 0; i < problem.nRows; i++) {
            cell = problem.board[i][var.y];
            if (i != var.x && cell instanceof Variable && ((Variable) cell).getValue() == 0) {
                ((Variable) cell).update_domain(value, lowerBound, upperBound);
                if (((Variable) cell).domainIsEmpty())
                    return false;
            }
        }
        upperBound = rCons.upperBound_calculate(rCons.getValue() - rCons.getAlreadySum(), rCons.numberOfUnassignedVar());
        lowerBound = rCons.lowerBound_calculate(rCons.getValue() - rCons.getAlreadySum(), rCons.numberOfUnassignedVar());
        for (int j = 0; j < problem.nColumns; j++) {
            cell = problem.board[var.x][j];
            if (j != var.y && cell instanceof Variable && ((Variable) cell).getValue() == 0) {
                ((Variable) cell).update_domain(value, lowerBound, upperBound);
                if (((Variable) cell).domainIsEmpty())
                    return false;
            }
        }
        return true;
    }

    private void forwardChecking_undo(Variable var) {
        Cell cell;
        for (int i = 0; i < problem.nRows; i++) {
            cell = problem.board[i][var.y];
            if (i != var.x && cell instanceof Variable && ((Variable) cell).getValue() == 0) {
                ((Variable) cell).undo_domain();
            }
        }
        for (int j = 0; j < problem.nColumns; j++) {
            cell = problem.board[var.x][j];
            if (j != var.y && cell instanceof Variable && ((Variable) cell).getValue() == 0) {
                ((Variable) cell).undo_domain();
            }
        }
    }
}

enum ExitStatus {
    SOLUTION,
    FAILURE
}