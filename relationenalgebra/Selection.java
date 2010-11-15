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
    if (Database.printSQL) {
      if (expression == null)
	return child.toString ();
      else
	return child + " where " + expression;
    }
    else
      return "(SELECTION " + expression + " " + child + ")";
  }

  public Table execute (Database database) {
    if (expression == null)
      return this.child.execute (database);

    Table child = this.child.execute (database), result = new Table (null, child.columns);

    for (Collection <String> row : child)
      if (expression.evaluate (child, row) != null)
	result.add (row);

    if (Database.calculateCosts)
      result.costs = child.costs + result.length * result.columns.size ();

    return result;
  }

  protected AndExpression expression;
  protected ITreeNode child;
}
