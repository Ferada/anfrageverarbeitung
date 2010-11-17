package relationenalgebra;

import java.util.*;

import main.*;

public class Projection extends AbstractOneChildNode {
  public Projection (Collection <ColumnName> columns, ITreeNode child) {
    this.child = child;
    this.columns = columns;
  }

  public String toString () {
    StringBuilder builder = new StringBuilder ();
    boolean first = true;

    if (Database.printSQL) {
      builder.append ("select ");

      for (ColumnName name : columns) {
	if (first)
	  first = false;
	else
	  builder.append (", ");
	builder.append (name);
      }

      builder.append (" from ");
      builder.append (child);
    }
    else {
      builder.append ("(PROJECTION (");
      for (ColumnName name : columns) {
	if (first)
	  first = false;
	else
	  builder.append (" ");
	builder.append (name);
      }
      builder.append (") ");
      builder.append (child);
      builder.append (")");
    }

    return builder.toString ();
  }

  public Table execute (Database database) {
    Table table = child.execute (database);
    int size = columns.size ();
    int[] indices = new int[size];
    int i = 0, j;

    // Database.trace ("Projection");
    // Database.trace ("columns = " + columns);
    // Database.trace ("table = " + table);
    // Database.trace ("");

    /* for every projection column, calculate its index inside the table
       columns, e.g. if the first projected column is the second table
       column, indices[0] is 1 */
    for (ColumnName name : columns) {
      j = 0;
      for (ColumnName column : table.columns) {
	if (column.equals (name)) break;
	++j;
      }

      if (j >= table.columns.size ())
	throw new RuntimeException ("there is no column named " + name +
				    (table.name == null ?
				     " in temporary table" :
				     (" in table " + table.name)));

      indices[i++] = j;
    }

    Table result = new Table (null, columns);

    /* defines toArray target type */
    String[] dummy = new String[1];
    int rows = 0;

    /* goes through every row and selects all columns from the array
       representation of the row */
    for (Collection <String> row : table) {
      Collection <String> newRow = new ArrayList <String> (size);
      String[] oldRow = row.toArray (dummy);

      for (i = 0; i < size; ++i)
	newRow.add (oldRow[indices[i]]);

      result.add (newRow);
    }

    if (Database.calculateCosts)
      result.costs = table.costs + size * result.length;

    return result;
  }

  protected Collection <ColumnName> columns;
}
