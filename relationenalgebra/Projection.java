package relationenalgebra;

import java.util.*;

import main.*;

public class Projection implements IOneChildNode {
  public Projection (Collection <String> names, ITreeNode child) {
    this.child = child;
    this.names = names;
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
    for (String name : names) {
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
    int size = names.size ();
    int[] indices = new int[size];
    int i = 0, j;

    Database.trace ("Projection");
    Database.trace ("names = " + names);
    Database.trace ("table = " + table);
    Database.trace ("");

    for (String name : names) {
      j = 0;
      Database.trace ("name = " + name);
      for (String column : table.columns) {
	String columnName = column;
	if (table.name != null)
	  columnName = table.name + columnName;

	Database.trace ("columnName = " + columnName);
	/* exact match */
	if (name.equals(columnName)) break;
	/* lax variant if column name contains a dot */
	String[] parts = columnName.split ("\\.");
	if (parts.length == 2 && name.equals (parts[1])) break;
	++j;
      }

      indices[i++] = j;
    }

    Table result = new Table (null, names);
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
  protected Collection <String> names;
}
