package relationenalgebra;

import java.util.*;

import main.*;

public class CrossProduct implements ITwoChildNode {
  public CrossProduct (ITreeNode first, ITreeNode second) {
    this.first = first;
    this.second = second;
  }

  public ITreeNode getChild () {
    return first;
  }

  public void setChild (ITreeNode child) {
    first = child;
  }

  public ITreeNode getSecondChild () {
    return second;
  }

  public void setSecondChild (ITreeNode child) {
    second = child;
  }

  public String toString () {
    if (Database.printSQL)
      return first + ", " + second;
    else
      return "(CROSSPRODUCT " + first + " " + second + ")";
  }

  public Table execute (Database database) {
    firstTable = this.first.execute (database);
    secondTable = this.second.execute (database);
    Collection <ColumnName> columns = new ArrayList <ColumnName> (firstTable.columns);
    columns.addAll (secondTable.columns);

    // Database.trace ("CrossProduct");
    // Database.trace ("first = " + first);
    // Database.trace ("second = " + second);
    // Database.trace ("");

    Table result = new Table (null, columns);

    subClassResponsibility ();

    for (Collection <String> firstRow : firstTable)
      for (Collection <String> secondRow : secondTable)
	executeRow (result, firstRow, secondRow);

    // Database.trace ("firstTable.costs = " + firstTable.costs);
    // Database.trace ("secondTable.costs = " + secondTable.costs);
    // Database.trace ("result.costs = " +
    // 		    firstTable.costs + "+" +
    // 		    secondTable.costs + "+" +
    // 		    firstTable.length  + "*" + secondTable.length + "*" +
    // 		    "(" + firstTable.columns.size () + "+" + secondTable.columns.size () + ") = " +
    // 		    (firstTable.costs + secondTable.costs +
    // 		     (firstTable.length * secondTable.length *
    // 		      (firstTable.columns.size () + secondTable.columns.size ()))));

    if (Database.calculateCosts)
      result.costs = firstTable.costs + secondTable.costs +
	(firstTable.length * secondTable.length *
	 (firstTable.columns.size () + secondTable.columns.size ()));

    return result;
  }

  protected void executeRow (Table result, Collection <String> firstRow, Collection <String> secondRow) {
    Collection <String> row = new ArrayList <String> (firstRow);
    row.addAll (secondRow);
    result.add (row);
  }

  protected void subClassResponsibility () {

  }

  protected ITreeNode first, second;
  protected Table firstTable, secondTable;
}
