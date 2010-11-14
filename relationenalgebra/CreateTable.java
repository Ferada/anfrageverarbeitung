package relationenalgebra;

import java.util.*;

import main.*;

public class CreateTable extends Delete {
  public CreateTable (Collection <String> columns, String name) {
    super (null, name);
    this.columns = new ArrayList <ColumnName> ();
    for (String column : columns)
      this.columns.add (new ColumnName (name, column));
  }

  public String toString () {
    StringBuilder builder = new StringBuilder ("create table ");
    builder.append (name);
    builder.append (" (");

    boolean first = true;
    for (ColumnName column : columns) {
      if (first)
	first = false;
      else
	builder.append (", ");
      builder.append (column.column);
      builder.append (" varchar");
    }
    builder.append (")");

    return builder.toString ();
  }

  public Table execute (Database database) {
    database.add (new Table (name, columns));
    return null;
  }

  protected Collection <ColumnName> columns;
}
