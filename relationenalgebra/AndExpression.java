package relationenalgebra;

import java.util.*;

import main.*;

public class AndExpression implements IBooleanExpression {
  public AndExpression (Collection <OrExpression> expressions) {
    this.expressions = expressions;
  }

  public Object evaluate (Table table, Collection <String> row) {
    Object result = null;

    /* short-circuit the AND expression */
    for (OrExpression expression : expressions) {
      result = expression.evaluate (table, row);
      if (result == null) return null;
    }

    return result;
  }

  public boolean applicable (Table table) {
    boolean result = true;

    /* short-circuit the AND expression */
    for (OrExpression expression : expressions) {
      result = result && expression.applicable (table);
      if (!result) return false;
    }

    return result;
  }

  public String toString () {
    StringBuilder builder = new StringBuilder ();
    boolean first = true;

    if (Database.printSQL) {
      for (OrExpression expression : expressions) {
	if (first)
	  first = false;
	else
	  builder.append (" and ");
	builder.append (expression);
      }
    }
    else {
      builder.append ("(AND ");
      for (OrExpression expression : expressions) {
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

  protected Collection <OrExpression> expressions;
}
