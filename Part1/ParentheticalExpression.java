package Part1;

import java.util.LinkedList;

public class ParentheticalExpression extends AbstractCompoundExpression {

    public ParentheticalExpression() {
        data = "()";
        children = new LinkedList<>();
    }

}
