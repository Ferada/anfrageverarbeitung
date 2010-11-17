package relationenalgebra;

import java.util.*;

import main.*;

public class Delete extends TableOperation {
  public Delete (AndExpression expression, String name) {
    super (name);
    this.expression = expression;
  }

  public String toString () {
    StringBuilder builder = new StringBuilder ();

    if (Database.printSQL) {
      builder.append ("delete from ");
      builder.append (name);

      if (!(expression == null)) {
	builder.append (" where ");
	builder.append (expression);
      }
    }
    else {
      builder.append ("(DELETE ");
      builder.append (name);
      builder.append (" ");
      builder.append (expression);
      builder.append (")");
    }

    return builder.toString ();
  }

  public Table execute (Database database) {
    Table table = database.getTable (name);

    Iterator <Collection <String>> row = table.iterator ();
    for (;row.hasNext ();) {
      Collection <String> values = row.next ();
      if (expression == null || expression.evaluate (table, values) != null)
	row.remove ();
    }

    return null;
  }

  protected AndExpression expression;
}
