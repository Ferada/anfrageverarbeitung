package relationenalgebra;

import java.util.*;

import main.*;

public class Insert extends TableOperation {
  public Insert (Collection <String> values, String name) {
    this (values, null, name);
  }

  public Insert (Collection <String> values, Collection <String> columns, String name) {
    super (name);

    this.values = values;
    this.columns = columns;
  }

  public String toString () {
    StringBuilder builder = new StringBuilder ("insert into ");
    builder.append (name);

    boolean first = true;

    if (!(columns == null)) {
      builder.append (" (");
      for (String string : columns) {
	if (first)
	  first = false;
	else
	  builder.append (", ");
	builder.append (string);
      }
      builder.append (")");
    }

    builder.append (" values (");
    first = true;
    for (String string : values) {
      if (first)
	first = false;
      else
	builder.append (", ");
      builder.append ("\"");
      builder.append (string);
      builder.append ("\"");
    }
    builder.append (")");

    return builder.toString ();
  }

  public Table execute (Database database) {
    Table table = database.getTable (name);

    Map <String, String> map = new HashMap <String, String> ();
    Iterator <String> iv = values.iterator (), ic = columns.iterator ();
    while (iv.hasNext () && ic.hasNext ())
      map.put (ic.next (), iv.next ());

    Collection <String> row = new ArrayList <String> ();
    for (ColumnName column : table.columns)
      row.add (map.containsKey (column.column) ? map.get (column.column) : "");

    table.add (row);

    return null;
  }

  protected Collection <String> columns, values;
}
