import java.util.ArrayList;

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

class Constraint extends Cell{
    private int constraintValue;
    private Direction direction; // vertical or horizontal
    public Constraint(int i, int j, int constraintValue, Direction direction) {
        super(i, j);
        this.constraintValue = constraintValue;
        this.direction = direction;
    }
    public int getValue() {
        return constraintValue;
    }

    @Override
    public String toString() {
        return "Constraint{" +
                "cValue=" + constraintValue +
                ", dir=" + direction +
                '}';
    }
}

class Variable extends Cell{
    private int value;
    private ArrayList<Integer> domain = new ArrayList<>();
    private Constraint rowConstraint;
    private Constraint columnConstraint;
    private Variable left;
    private Variable up;

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

    public void setLeft(Variable left) {
        this.left = left;
    }

    public void setUp(Variable up) {
        this.up = up;
    }

    public ArrayList<Integer> getDomain() {
        return domain;
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