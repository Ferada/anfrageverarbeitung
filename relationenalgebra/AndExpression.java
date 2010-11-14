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

  public String toString () {
    StringBuilder builder = new StringBuilder ();

    boolean first = true;
    for (OrExpression expression : expressions) {
      if (first)
	first = false;
      else
	builder.append (" and ");
      builder.append (expression);
    }

    return builder.toString ();
  }

  protected Collection <OrExpression> expressions;
}
