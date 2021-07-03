import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

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
        ArrayList<Integer> domain = LCV(var);
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

    private ArrayList<Integer> LCV(Variable var) {
        ArrayList<Integer> domain = var.getDomain();
        ArrayList<Integer> number = new ArrayList<>();
        Cell cell;
        int num;
        Constraint rCons = var.getRowConstraint();
        Constraint cCons = var.getColumnConstraint();
        int upperBound_c = cCons.upperBound_calculate(cCons.getValue() - cCons.getAlreadySum(), cCons.numberOfUnassignedVar());
        int lowerBound_c = cCons.lowerBound_calculate(cCons.getValue() - cCons.getAlreadySum(), cCons.numberOfUnassignedVar());
        int upperBound_r = rCons.upperBound_calculate(rCons.getValue() - rCons.getAlreadySum(), rCons.numberOfUnassignedVar());
        int lowerBound_r = rCons.lowerBound_calculate(rCons.getValue() - rCons.getAlreadySum(), rCons.numberOfUnassignedVar());
        for (Integer value : domain) {
            num = Integer.MAX_VALUE;
            for (int i = 0; i < problem.nRows; i++) {
                cell = problem.board[i][var.y];
                if (i != var.x && cell instanceof Variable && ((Variable) cell).getValue() == 0) {
                    num = Math.min(num, ((Variable) cell).getNumberOfLegalDomain(value, lowerBound_c, upperBound_c));
                }
            }
            for (int j = 0; j < problem.nColumns; j++) {
                cell = problem.board[var.x][j];
                if (j != var.y && cell instanceof Variable && ((Variable) cell).getValue() == 0) {
                    num = Math.min(num, ((Variable) cell).getNumberOfLegalDomain(value, lowerBound_r, upperBound_r));
                }
            }
            number.add(num);
        }
//        System.out.println("domain : " + domain);
//        System.out.println("number : " + number);
        domain.sort(Comparator.comparingInt(number::indexOf));
//        Collections.sort(domain, new Comparator<Integer>() {
//            public int compare(Integer left, Integer right) {
//                return Integer.compare(number.indexOf(left), number.indexOf(right));
//            }
//        });
//        System.out.println(number);
//        System.out.println(domain);
//        System.out.println("**************************");
        Collections.reverse(domain);
        return domain;
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