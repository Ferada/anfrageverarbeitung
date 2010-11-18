package main;

import java.util.*;

import relationenalgebra.*;

public class JoinVisitor extends Visitor {
  public Object visit (Selection x) {
    if (x.child instanceof CrossProduct) {
      CrossProduct cross = (CrossProduct) x.child;
      return new Join ((IBooleanExpression) dispatch (x.expression),
		       (ITreeNode) dispatch (cross.first),
		       (ITreeNode) dispatch (cross.second));
    }
    return super.visit (x);
  }
}
