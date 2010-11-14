package relationenalgebra;

import java.util.*;

import main.*;

public class Join extends CrossProduct {
  public Join (AndExpression expression, ITreeNode first, ITreeNode second) {
    super (first, second);
    this.expression = expression;
  }

  public String toString () {
    return "" + first + ", " + second + " where " + expression;
  }

  protected void subClassResponsibility () {
    testOnlyFirst = expression.applicable (firstTable);
    testOnlySecond = expression.applicable (secondTable);
  }

  protected void executeRow (Table result, Collection <String> firstRow, Collection <String> secondRow) {
    // Database.trace ("Join");

    if (expression == null ||
	(testOnlyFirst && expression.evaluate (firstTable, firstRow) != null) ||
	(testOnlySecond && expression.evaluate (secondTable, secondRow) != null)) {
      Collection <String> row = new ArrayList <String> (firstRow);
      row.addAll (secondRow);
      result.add (row);
    }
    else {
      Collection <String> row = new ArrayList <String> (firstRow);
      row.addAll (secondRow);
      if (expression.evaluate (result, row) != null)
	result.add (row);
    }
  }

  protected AndExpression expression;
  protected boolean testOnlyFirst, testOnlySecond;
}
