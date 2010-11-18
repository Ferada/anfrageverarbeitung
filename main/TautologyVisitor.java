package main;

import java.util.*;

import relationenalgebra.*;

public class TautologyVisitor extends Visitor {
  public Object visit (EqualityExpression x) {
    if (x.first == null || x.second == null)
      return x;
    if (x.first.constant && x.second.constant)
      return new ConstantBooleanExpression (x.evaluate (null, null, null, null) != null);
    return super.visit (x);
  }
}
