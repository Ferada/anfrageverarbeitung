package relationenalgebra;

import java.util.*;

import main.*;

public class Selection extends AbstractOneChildNode {
  public Selection (AndExpression expression, ITreeNode child) {
    this.expression = expression;
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

  public AbstractTable execute (Database database) {
    if (expression == null)
      return this.child.execute (database);

    final AbstractTable child = this.child.execute (database);
    final LazyTable result = new LazyTable (null, child.columns);
    final int size = result.columns.size ();

    final Iterator <Collection <String>> it = child.iterator();

    Iterator <Collection <String>> resultIterator = new Iterator <Collection <String>> () {
      public boolean hasNext () {
	for (;it.hasNext ();)
	  if (expression.evaluate (child, row = it.next ()) != null)
	    return true;

	if (!calculatedCosts) {
	  result.costs += child.costs;
	  calculatedCosts = true;
	}
	return false;
      }

      public Collection <String> next () {
	result.costs += size;
	return row;
      }

      public void remove () {
	throw new UnsupportedOperationException ();
      }

      private Collection <String> row;
      private boolean calculatedCosts = false;
    };

    result.iterator = resultIterator;

    return result;
  }

  protected AndExpression expression;
}
