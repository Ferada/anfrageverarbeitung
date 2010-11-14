package relationenalgebra;

import java.util.*;

import main.*;

public class PrimaryExpression implements IBooleanExpression {
  /** Constructs a table/relation reference. */
  public PrimaryExpression (ColumnName columnName) {
    constant = false;
    name = columnName;
  }

  /** Constructs a constant/literal expression. */
  public PrimaryExpression (String value) {
    constant = true;
    this.value = value;
  }

  public Object evaluate (Table table, Collection <String> values) {
    if (constant)
      return value;

    // Database.trace ("PrimaryExpression, table.name = " + table.name);
    // Database.trace ("table.columns = " + table.columns);
    // Database.trace ("name = " + name);

    if (table.name != null && name.relation != null)
      assert (table.name.equals (name.relation));

    // String columnName = name;
    // if (table.name == null && relation != null)
    //   columnName = relation + "." + name;

    Iterator <ColumnName> columnIterator = table.columns.iterator ();
    Iterator <String> row = values.iterator ();

    ColumnName tableColumn;
    String tableValue;

    for (;columnIterator.hasNext () && row.hasNext ();) {
      tableColumn = columnIterator.next ();
      tableValue = row.next ();

      if (tableColumn.equals(name))
	return tableValue;
    }

    throw new RuntimeException ("PrimaryExpression didn't work as it should.");
  }

  public boolean applicable (Table table) {
    if (constant)
      return true;

    for (ColumnName tableColumn : table.columns)
      if (tableColumn.equals (name))
	return true;

    return false;
  }

  public String toString () {
    if (constant)
      return "\"" + value + "\"";
    return name.toString ();
  }

  /** If true than value is valid, else name. */
  protected boolean constant;
  protected String value;
  protected ColumnName name;
}
