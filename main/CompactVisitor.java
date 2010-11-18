package main;

import java.util.*;

import relationenalgebra.*;

/** Compacts one element expressions together. */
public class CompactVisitor extends Visitor {
  public Object visit (AndExpression x) {
    /* whats with 0? */
    if (x.expressions.size () == 1)
      return dispatch (x.expressions.iterator ().next ());

    return super.visit (x);
  }

  public Object visit (OrExpression x) {
    /* whats with 0? */
    if (x.expressions.size () == 1)
      return dispatch (x.expressions.iterator ().next ());

    return super.visit (x);
  }

  public Object visit (EqualityExpression x) {
    if (x.second == null)
      return dispatch (x.first);
    return super.visit (x);
  }
}
