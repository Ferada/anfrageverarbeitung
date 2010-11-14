package relationenalgebra;

import java.util.*;

import main.*;

public class Selection implements IOneChildNode {
  public Selection (AndExpression expression, ITreeNode child) {
    this.expression = expression;
    this.child = child;
  }

  public ITreeNode getChild () {
    return child;
  }

  public void setChild (ITreeNode child) {
    this.child = child;
  }

  public String toString () {
    if (expression == null)
      return child.toString ();
    else
      return child + " where " + expression.toString ();
  }

  public Table execute (Database database) {
    if (expression == null)
      return this.child.execute (database);

    Table child = this.child.execute (database), result = new Table (null, child.columns);

    for (Collection <String> row : child)
      if (expression.evaluate (child, row) != null)
	result.add (row);

    return result;
  }

  protected AndExpression expression;
  protected ITreeNode child;
}
