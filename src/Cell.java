import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.Stack;

// A cell can be Constraint, Variable or Black
// default -> Black cells
public class Cell {
    protected int x;
    protected int y;

    public Cell(int x, int y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public String toString() {
        return "Cell{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }
}

class Constraint extends Cell {
    private final int constraintValue;
    private final Direction direction; // vertical or horizontal
    private final Set<Integer> valueOfRelatedVariables = new HashSet<>();
    private final int numberOfRelatedVar;
    private int already_sum;   // sum of value of related variables
    private int lowerBound; // smallest value that a related variable can have
    private int upperBound; // biggest value that a variable can have

    public Constraint(int i, int j, int constraintValue, Direction direction, int numberOfRelatedVar) {
        super(i, j);
        this.constraintValue = constraintValue;
        this.direction = direction;
        this.numberOfRelatedVar = numberOfRelatedVar;
        already_sum = 0;
        upperBound = upperBound_calculate(constraintValue, numberOfRelatedVar);
        lowerBound = lowerBound_calculate(constraintValue, numberOfRelatedVar);
    }

    public int upperBound_calculate(int currentValue, int numberOfUnassignedVar) {
        int sum = (numberOfUnassignedVar * (numberOfUnassignedVar - 1)) / 2;  // sum of : 1, 2, 3, ..., (numberOfRelatedVar-1)
        return Math.min(currentValue - sum, 9);
    }

    public int lowerBound_calculate(int currentValue, int numberOfUnassignedVar) {
        int sum = ((20 - numberOfUnassignedVar) * (numberOfUnassignedVar - 1)) / 2;  // sum of : 9, 8, 7, ..., (9-(numberOfRelatedVar-1)+1)
        return Math.max(currentValue - sum, 1);
    }

    public int getLowerBound() {
        return lowerBound;
    }

    public int getUpperBound() {
        return upperBound;
    }

    public int getValue() {
        return constraintValue;
    }

    public boolean isDiff(int value) {
        return !(valueOfRelatedVariables.contains(value));
    }

    public int numberOfUnassignedVar() {
        return (numberOfRelatedVar - valueOfRelatedVariables.size());
    }

    public void addValue(int val) {
        valueOfRelatedVariables.add(val);
        already_sum += val;
    }

    public void removeValue(int val) {
        valueOfRelatedVariables.remove(val);
        already_sum -= val;
    }

    public int getAlreadySum() {
        return already_sum;
    }

    @Override
    public String toString() {
        return "Constraint{" +
                "cValue=" + constraintValue +
                ", dir=" + direction +
//                ", numberOfRelatedVar=" + numberOfRelatedVar +
//                ", lowerBound=" + lowerBound +
//                ", upperBound=" + upperBound +
                '}';
    }
}

class Variable extends Cell {
    private int value;
    private Domain domain = new Domain();
    private Stack<Domain> domain_history = new Stack<>();
    private final Constraint rowConstraint;
    private final Constraint columnConstraint;

    public Variable(int i, int j, Constraint rowConstraint, Constraint columnConstraint) {
        super(i, j);
        this.value = 0;
        this.rowConstraint = rowConstraint;
        this.columnConstraint = columnConstraint;
        if (rowConstraint == null)
            System.out.println("something goes wrong! (rowConstraint is null))");
        if (columnConstraint == null)
            System.out.println("something goes wrong! (columnConstraint is null))");

        // nodeConsistency and initializing domain
        nodeConsistency();
    }

    public void setValue(int value) {
        this.value = value;
    }

    public ArrayList<Integer> getDomain() {
        return domain.getDomain();
    }

    public int getValue() {
        return value;
    }

    public Constraint getRowConstraint() {
        return rowConstraint;
    }

    public Constraint getColumnConstraint() {
        return columnConstraint;
    }

    public void nodeConsistency() {
        int lowerBound;
        int upperBound;
        lowerBound = Math.max(rowConstraint.getLowerBound(), columnConstraint.getLowerBound());
        upperBound = Math.min(rowConstraint.getUpperBound(), columnConstraint.getUpperBound());
        ArrayList<Integer> d = new ArrayList<>();
        for (int k = lowerBound; k <= upperBound; k++)
            d.add(k);
        domain.setDomain(d);
    }

    public void update_domain(int value, int lowerBound, int upperBound) {
//        System.out.println("lowerBound : " + lowerBound + " upperBound : " + upperBound);
        domain_history.push(domain.copy());
        domain.getDomain().remove((Integer) value);
        domain.getDomain().removeIf(d -> d > upperBound || d < lowerBound);
    }

    public void undo_domain() {
        if (!domain_history.isEmpty())
            domain = domain_history.pop();
    }

//    public void printStack() {
//        System.out.println("stack : {");
//        for (Domain d : domain_history) {
//            System.out.println(d.getDomain());
//        }
//        System.out.println("}");
//    }

    public boolean domainIsEmpty() {
        return domain.getDomain().isEmpty();
    }

    @Override
    public String toString() {
        return "Variable{" +
                "value=" + value +
                ", domain=" + domain +
//                "i=" + i +
//                ", j=" + j +
                ", rowConstraint=" + rowConstraint +
                ", columnConstraint=" + columnConstraint +
                '}';
    }

    // use for lcv
    public int getNumberOfLegalDomain(int value, int lowerBound, int upperBound) {
        Domain temp = domain.copy();
        temp.getDomain().remove((Integer) value);
        temp.getDomain().removeIf(d -> d > upperBound || d < lowerBound);
        return temp.getDomain().size();
    }

    public void removeFromDomain(int value) {
        domain.getDomain().remove((Integer) value);
    }

}

class Domain {
    private ArrayList<Integer> domain;

    public Domain() {
        domain = new ArrayList<>();
    }
    public Domain(ArrayList<Integer> domain) {
        this.domain = domain;
    }

    public void setDomain(ArrayList<Integer> domain) {
        this.domain = domain;
    }

    public ArrayList<Integer> getDomain() {
        return domain;
    }
    public Domain copy() {
        return new Domain(new ArrayList<>(domain));
    }
}
