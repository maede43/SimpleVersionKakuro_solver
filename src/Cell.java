import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

// A cell can be Constraint, Variable or Black
// default -> Black cells
public class Cell {
    protected int i;
    protected int j;

    public Cell(int i, int j) {
        this.i = i;
        this.j = j;
    }

    @Override
    public String toString() {
        return "Cell{" +
                "i=" + i +
                ", j=" + j +
                '}';
    }
}

class Constraint extends Cell {
    private final int constraintValue;
    private final Direction direction; // vertical or horizontal
    private final Set<Integer> valueOfRelatedVariables = new HashSet<>();
    private final int numberOfRelatedVar;
    private int already_sum;   // sum of value of related variables

    public Constraint(int i, int j, int constraintValue, Direction direction, int numberOfRelatedVar) {
        super(i, j);
        this.constraintValue = constraintValue;
        this.direction = direction;
        this.numberOfRelatedVar = numberOfRelatedVar;
        already_sum = 0;
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
                '}';
    }
}

class Variable extends Cell {
    private int value;
    private ArrayList<Integer> domain = new ArrayList<>();
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
        for (int k = 1; k < 10; k++)
            domain.add(k);
    }

    public void setValue(int value) {
        this.value = value;
    }

    public ArrayList<Integer> getDomain() {
        return domain;
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
}