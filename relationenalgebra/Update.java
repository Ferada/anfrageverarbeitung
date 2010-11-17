package relationenalgebra;

import java.util.*;

import main.*;

public class Update extends Delete {
  public Update (Map <String, String> map, AndExpression where, String name) {
    super (where, name);
    this.map = map;
  }

  public String toString () {
    StringBuilder builder = new StringBuilder ();
    boolean first = true;

    if (Database.printSQL) {
      builder.append ("update ");
      builder.append (name);
      builder.append (" set ");
	
      for (Map.Entry <String, String> entry : map.entrySet ()) {
	if (first)
	  first = false;
	else
	  builder.append (", ");
	builder.append (entry.getKey ());
	builder.append (" = ");
	builder.append (entry.getValue ());
      }

      if (expression != null) {
	builder.append (" where ");
	builder.append (expression);
      }
    }
    else {
      builder.append ("(UPDATE (");

      for (Map.Entry <String, String> entry : map.entrySet ()) {
	if (first)
	  first = false;
	else
	  builder.append (" ");
	builder.append ("(");
	builder.append (entry.getKey ());
	builder.append (" ");
	builder.append (entry.getValue ());
	builder.append (")");
      }

      builder.append (") ");

      if (expression != null)
	builder.append (expression);

      builder.append (")");
    }

    return builder.toString ();
  }

  /** Updates a row if the expression is null or matches the row. */
  public Table execute (Database database) {
    Table table = database.getTable (name);

    for (Collection <String> values : table)
      if (expression == null || expression.evaluate (table, values) != null) {
	Iterator <String> iv = values.iterator ();
	Iterator <ColumnName> ic = table.columns.iterator ();
	Collection <String> newValues = new ArrayList <String> ();

	/* goes through the row and its associated columns and replaces
	   every matching column with the new content */
	for (;iv.hasNext () && ic.hasNext ();) {
	  String value = iv.next ();
	  ColumnName column = ic.next ();

	  if (map.containsKey (column))
	    newValues.add (map.get (column));
	  else
	    newValues.add (value);
	}

	/* fills the original row with new content */
	values.clear ();
	values.addAll (newValues);
      }

    return null;
  }

  protected Map <String, String> map;
}
