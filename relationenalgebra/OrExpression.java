package relationenalgebra;

import java.util.*;

import main.*;

public class OrExpression implements IBooleanExpression {
  public OrExpression (Collection <EqualityExpression> expressions) {
    this.expressions = expressions;
  }

  public Object evaluate (Table table, Collection <String> row) {
    Object result;

    /* short-circuit the OR expression */
    for (EqualityExpression expression : expressions) {
      result = expression.evaluate (table, row);
      if (result != null) return result;
    }

    return null;
  }

  public boolean applicable (Table table) {
    boolean result;

    /* short-circuit the OR expression */
    for (EqualityExpression expression : expressions) {
      result = expression.applicable (table);
      if (result) return result;
    }

    return false;
  }

  public String toString () {
    StringBuilder builder = new StringBuilder ();
    boolean first = true;

    if (Database.printSQL) {
      boolean braced = expressions.size () != 1;
      if (braced) builder.append ("(");

      for (EqualityExpression expression : expressions) {
	if (first)
	  first = false;
	else
	  builder.append (" or ");
	builder.append (expression);
      }

      if (braced) builder.append (")");
    }
    else {
      builder.append ("(OR ");
      for (EqualityExpression expression : expressions) {
	if (first)
	  first = false;
	else
	  builder.append (" ");
	builder.append (expression);
      }
      builder.append (")");
    }

    return builder.toString ();
  }

  protected Collection <EqualityExpression> expressions;
}
