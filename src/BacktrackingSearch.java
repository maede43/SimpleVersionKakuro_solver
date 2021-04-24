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
//        System.out.format(".");
        if (problem.isSatisfied()) {
            return ExitStatus.SOLUTION;
        }
        Variable var = problem.getUnassignedVariable();
        ArrayList<Integer> domain = var.getDomain();
        for (Integer value : domain) {
            if (problem.setValueIfIsConsistent(var, value)) {
                ExitStatus result;
                result = back_track();
                if (result == ExitStatus.SOLUTION)
                    return ExitStatus.SOLUTION;
                problem.removeValue(var, value);
            }
        }
        return ExitStatus.FAILURE;
    }
}

enum ExitStatus {
    SOLUTION,
    FAILURE
}