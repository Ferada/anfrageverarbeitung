package relationenalgebra;

import java.util.*;

import main.*;

public class PrimaryExpression extends AbstractBooleanExpression {
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

  /** Do the actual work for the evaluate methods.  Go through the
      columns until the right one matches our name (or return a constant
      value).  Returns null if none matches is (which is then converted
      to an exception in the evaluate methods). */
  private Object doEvaluate (AbstractTable table, Collection <String> values) {
    if (constant)
      return value;

    // Database.trace ("PrimaryExpression, table.name = " + table.name);
    // Database.trace ("table.columns = " + table.columns);
    // Database.trace ("name = " + name);

    if (table.name != null && name.relation != null) {
      /* this is not an error anymore, since the two table case also
	 tries on mismatching tables */
      // assert (table.name.equals (name.relation));
      if (!table.name.equals (name.relation))
	return null;
    }

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

    return null;
  }

  public Object evaluate (AbstractTable table, Collection <String> row) {
    Object result = doEvaluate (table, row);
    if (result == null)
      throw new RuntimeException ("couldn't evaluate primary expression " + this);
    return result;
  }

  /** Evaluate the expression on both tables/rows and see which one works. */
  public Object evaluate (AbstractTable table1, AbstractTable table2,
			  Collection <String> row1, Collection <String> row2) {
    Object result = doEvaluate (table1, row1);
    if (result == null)
      result = doEvaluate (table2, row2);
    if (result == null)
      throw new RuntimeException ("couldn't evaluate primary expression " + this);
    return result;
  }

  public String toString () {
    if (constant)
      return "\"" + value + "\"";
    return name.toString ();
  }

  /** If true than value is valid, else name. */
  public boolean constant;
  protected String value;
  public ColumnName name;
}
