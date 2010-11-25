package main;

import java.util.*;

import relationenalgebra.*;

public class SplitVisitor extends ModifyVisitor {
  public Object visit (Selection x) {
    if (x.expression != null && x.expression.getClass () == AndExpression.class) {
      AndExpression and = (AndExpression) x.expression;
      if (and.expressions.size () > 1) {
	Iterator <? extends IBooleanExpression> it = and.expressions.iterator ();
	IBooleanExpression first = it.next ();
	it.remove ();
	Collection <IBooleanExpression> newExpressions = new ArrayList <IBooleanExpression> ();
	newExpressions.add (first);
	return new Selection (new AndExpression (newExpressions), (ITreeNode) dispatch (x));
      }
    }
    return x;
  }
}
