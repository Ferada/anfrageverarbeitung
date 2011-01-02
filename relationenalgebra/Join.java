package relationenalgebra;

import java.util.*;

import main.*;

public class Join extends CrossProduct {
  public Join (IBooleanExpression expression, ITreeNode first, ITreeNode second) {
    super (first, second);
    this.expression = expression;
  }

  public String toString () {
    if (Database.printSQL)
      return "" + first + ", " + second + " where " + expression;
    else
      return "(JOIN " + expression + " " + first + " " + second + ")";
  }

  /** Only adds a row if either the expression is null, or it matches with the two rows. */
  protected void executeRow (Table result, AbstractTable table1, AbstractTable table2,
			    Collection <String> row1, Collection <String> row2) {
    if (expression == null ||
	expression.evaluate (table1, table2, row1, row2) != null) {
      Collection <String> row = new ArrayList <String> (row1);
      row.addAll (row2);
      result.add (row);
    }
  }

  public IBooleanExpression expression;
}
