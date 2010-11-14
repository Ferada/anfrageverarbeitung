package relationenalgebra;

import java.util.*;

import main.*;

public class Join extends CrossProduct {
  public Join (AndExpression expression, ITreeNode first, ITreeNode second) {
    super (first, second);
    this.expression = expression;
  }

  public String toString () {
    return "Join (" + expression + ") on " + first + ", " + second;
  }

  protected void executeRow (Table result, Collection <String> firstRow, Collection <String> secondRow) {
    // Database.trace ("Join");

    Collection <String> row = new ArrayList <String> (firstRow);
    row.addAll (secondRow);

    if (expression == null || expression.evaluate (result, row) != null)
      result.add (row);
  }

  protected AndExpression expression;
}
