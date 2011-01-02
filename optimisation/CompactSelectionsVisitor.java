package optimisation;

import java.util.*;

import relationenalgebra.*;

public class CompactSelectionsVisitor extends ModifyVisitor {
  public Object visit (Selection x) {
    while (x.child instanceof Selection) {
      Selection selection = (Selection) x.child;
      AndExpression child = (AndExpression) selection.expression,
	expression = (AndExpression) x.expression;
      expression.expressions.addAll (child.expressions);
      x.child = selection.child;
    }
    x.child = (ITreeNode) dispatch (x.child);
    return x;
  }
}
