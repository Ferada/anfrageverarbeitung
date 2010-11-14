package relationenalgebra;

import java.util.*;

import main.*;

public class PrimaryExpression implements IBooleanExpression {
  /** Constructs a table/relation reference. */
  public PrimaryExpression (String relation, String name) {
    this.relation = relation;
    if ((this.name = name) == null)
      throw new NullPointerException ("primary expression name can't be null");
    constant = false;
  }

  /** Constructs a constant/literal expression. */
  public PrimaryExpression (String value) {
    constant = true;
    name = value;
  }

  public Object evaluate (Table table, Collection <String> values) {
    if (constant)
      return name;

    // Database.trace ("PrimaryExpression, table.name = " + table.name);
    // Database.trace ("table.columns = " + table.columns);
    // Database.trace ("name = " + name + ", relation = " + relation);

    if (table.name != null && relation != null)
      assert (table.name.equals (relation));

    String columnName = name;
    if (table.name == null && relation != null)
      columnName = relation + "." + name;

    // Database.trace ("columnName = " + columnName);

    Iterator <String> column = table.columns.iterator ();
    Iterator <String> row = values.iterator ();
    String name, value;

    for (;column.hasNext () && row.hasNext ();) {
      name = column.next ();
      value = row.next ();
      assert (name != null);
      assert (value != null);
      /* exact match */
      if (columnName.equals(name)) {
	// Database.trace ("return exact match, '" + columnName + "'='" + name + "', value = " + value + "\n");
	return value;
      }
      /* lax variant if column name contains a dot */
      String[] parts = name.split ("\\.");
      // Database.trace ("parts.length = " + parts.length + ", parts[1] = " + parts[1]);

      if (parts.length == 2 && columnName.equals (parts[1])) {
	// Database.trace ("return lax match, '" + columnName + "'='" + parts[1] + "', value = " + value + "\n");
	return value;
      }
    }
    assert (false);
    // Database.trace ("returning null, no match ...\n");

    return null;
  }

  public String toString () {
    if (constant)
      return "\"" + name + "\"";
    else if (relation == null)
      return name;
    else
      return relation + "." + name;
  }

  protected boolean constant;
  /* name is also used as value if this is a constant (aka literal) expression */
  protected String relation, name;
}
