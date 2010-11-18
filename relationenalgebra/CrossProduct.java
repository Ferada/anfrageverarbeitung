package relationenalgebra;

import java.util.*;

import main.*;

public class CrossProduct extends AbstractTwoChildNode {
  public CrossProduct (ITreeNode first, ITreeNode second) {
    this.first = first;
    this.second = second;
  }

  public String toString () {
    if (Database.printSQL)
      return first + ", " + second;
    else
      return "(CROSSPRODUCT " + first + " " + second + ")";
  }

  public AbstractTable execute (Database database) {
    AbstractTable table1 = this.first.execute (database);
    AbstractTable table2 = this.second.execute (database);
    Collection <ColumnName> columns = new ArrayList <ColumnName> (table1.columns);
    columns.addAll (table2.columns);

    // Database.trace ("CrossProduct");
    // Database.trace ("first = " + first);
    // Database.trace ("second = " + second);
    // Database.trace ("");

    Table result = new Table (null, columns);

    for (Collection <String> firstRow : table1)
      for (Collection <String> secondRow : table2)
	executeRow (result, table1, table2, firstRow, secondRow);

    // Database.trace ("table1.costs = " + table1.costs);
    // Database.trace ("table2.costs = " + table2.costs);
    // Database.trace ("result.costs = " +
    // 		    table1.costs + "+" +
    // 		    table2.costs + "+" +
    // 		    table1.length  + "*" + table2.length + "*" +
    // 		    "(" + table1.columns.size () + "+" + table2.columns.size () + ") = " +
    // 		    (table1.costs + table2.costs +
    // 		     (table1.length * table2.length *
    // 		      (table1.columns.size () + table2.columns.size ()))));

    result.costs = table1.costs + table2.costs +
      (table1.length * table2.length *
       (table1.columns.size () + table2.columns.size ()));

    return result;
  }

  /** Adds a new row by concatenating both argument rows.  Should be
      overwritten in subclasses (e.g. the generic Join operator).
      @see Join */
  protected void executeRow (Table result, AbstractTable table1, AbstractTable table2,
			    Collection <String> row1, Collection <String> row2) {
    Collection <String> row = new ArrayList <String> (row1);
    row.addAll (row2);
    result.add (row);
  }
}
