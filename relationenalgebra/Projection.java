package relationenalgebra;

import java.util.*;

import main.*;

import static main.Database.trace;

public class Projection extends AbstractOneChildNode {
  public Projection (Collection <ColumnName> columns, ITreeNode child) {
    this.child = child;
    this.columns = columns;
    size = columns.size ();
    indices = new int[size];
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

  public AbstractTable execute (Database database) {
    final AbstractTable table = child.execute (database);

    trace ("Projection");
    // Database.trace ("columns = " + columns);
    // Database.trace ("table = " + table);
    // Database.trace ("");

    int i = 0;

    /* for every projection column, calculate its index inside the table
       columns, e.g. if the first projected column is the second table
       column, indices[0] is 1 */
    for (ColumnName name : columns) {
      int j = 0;
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

    /* defines toArray target type */
    final Iterator <Collection <String>> it = table.iterator ();

    final LazyTable result = new LazyTable (null, columns);

    Iterator <Collection <String>> resultIterator = new Iterator <Collection <String>> () {
      public boolean hasNext () {
	boolean has = it.hasNext ();

	/* only calculate costs if the child finished and its costs is
	   complete; also do it only once */
	if (!has && !calculatedCosts) {
	  result.costs += table.costs;
	  calculatedCosts = true;
	}
	return has;
      }

      public Collection <String> next () {
	Collection <String> newRow = new ArrayList <String> (size);
	String[] oldRow = it.next ().toArray (dummy);

	for (int i = 0; i < size; ++i)
	  newRow.add (oldRow[indices[i]]);

	result.costs += size;

	return newRow;
      }

      public void remove () {
	throw new UnsupportedOperationException ();
      }

      private boolean calculatedCosts = false;
    };

    result.iterator = resultIterator;

    return result;
  }

  public Collection <ColumnName> columns;
  private final String[] dummy = new String[1];
  private final int size;
  private final int[] indices;
}
