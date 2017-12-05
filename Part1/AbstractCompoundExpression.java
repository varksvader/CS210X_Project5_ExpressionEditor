package Part1;

import java.util.LinkedList;
import java.util.List;

public abstract class AbstractCompoundExpression implements CompoundExpression {

    private CompoundExpression parent;
    String data;
    List<Expression> children = new LinkedList<>();

    @Override
    public CompoundExpression getParent() {
        return parent;
    }

    @Override
    public void setParent(CompoundExpression parent) {
        this.parent = parent;
    }

    @Override
    public Expression deepCopy() {
        return null;
    }

    @Override
    public void flatten() {
        for (int i = 0; i < children.size(); i++) {
            if (data.equals("*") && children.get(i) instanceof MultiplicativeExpression) {
                children.addAll(((MultiplicativeExpression) children.get(i)).children);
                children.remove(children.get(i));
                if (i > 0) {
                    i--;
                }
            } else if (data.equals("+") && children.get(i) instanceof AdditiveExpression) {
                children.addAll(((AdditiveExpression) children.get(i)).children);
                children.remove(children.get(i));
                if (i > 0) {
                    i--;
                }
            }
            children.get(i).flatten();
        }
    }

    @Override
    public String convertToString(int indentLevel) {
        System.out.println(children.size());
        StringBuffer buf = new StringBuffer("");
        Expression.indent(buf, indentLevel);

        String str = buf.toString() + data + "\n";
        for (Expression child : children) {
            str += child.convertToString(indentLevel + 1);
        }
        return str;
    }



    @Override
    public void addSubexpression(Expression subexpression) {
        children.add(subexpression);
    }



}
