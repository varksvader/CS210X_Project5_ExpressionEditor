package Part1;

public class ParentheticalExpression extends AbstractCompoundExpression {

    private Expression child;

    public ParentheticalExpression(Expression child) {
        this.child = child;
    }

    public void setChild(Expression child) {
        this.child = child;
    }

}
