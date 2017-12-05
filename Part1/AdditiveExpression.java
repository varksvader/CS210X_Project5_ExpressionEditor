package Part1;

import java.util.LinkedList;

public class AdditiveExpression extends SimpleCompoundExpression {

    public AdditiveExpression() {
        this.data = "+";
        children = new LinkedList<>();
    }

}
