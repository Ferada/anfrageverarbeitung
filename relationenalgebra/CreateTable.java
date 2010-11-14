package relationenalgebra;

import java.util.*;

import main.*;

public class CreateTable extends Delete {
  public CreateTable (Collection <String> columns, String name) {
    super (null, name);
    this.columns = columns;
  }

  public String toString () {
    StringBuilder builder = new StringBuilder ("create table ");
    builder.append (name);
    builder.append (" (");

    boolean first = true;
    for (String string : columns) {
      if (first)
	first = false;
      else
	builder.append (", ");
      builder.append (string);
      builder.append (" varchar");
    }
    builder.append (")");

    return builder.toString ();
  }

  public Table execute (Database database) {
    database.add (new Table (name, columns));
    return null;
  }

  protected Collection <String> columns;
}
