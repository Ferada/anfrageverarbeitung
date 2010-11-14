package relationenalgebra;

import java.util.*;

import main.*;

public class Projection implements IOneChildNode {
  public Projection (Collection <ColumnName> columns, ITreeNode child) {
    this.child = child;
    this.columns = columns;
  }

  public ITreeNode getChild () {
    return child;
  }

  public void setChild (ITreeNode child) {
    this.child = child;
  }

  public String toString () {
    StringBuilder builder = new StringBuilder ("select ");

    boolean first = true;
    for (ColumnName name : columns) {
      if (first)
	first = false;
      else
	builder.append (", ");
      builder.append (name);
    }

    builder.append (" from ");
    builder.append (child);

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

    for (ColumnName name : columns) {
      j = 0;
      for (ColumnName column : table.columns) {
	if (column.equals (name)) break;
	++j;
      }

      indices[i++] = j;
    }

    Table result = new Table (null, columns);
    String[] foo = new String[1];

    for (Collection <String> row : table) {
      Collection <String> newRow = new ArrayList <String> (size);
      String[] oldRow = row.toArray (foo);

      for (i = 0; i < indices.length; ++i)
	newRow.add (oldRow[indices[i]]);

      result.add (newRow);
    }

    return result;
  }

  protected ITreeNode child;
  protected Collection <ColumnName> columns;
}
