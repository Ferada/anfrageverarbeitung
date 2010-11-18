package relationenalgebra;

import java.util.*;

import main.*;

public class AndExpression extends AbstractBooleanExpression {
  public AndExpression (Collection <? extends IBooleanExpression> expressions) {
    this.expressions = expressions;
  }

  public Object evaluate (AbstractTable table1, AbstractTable table2,
			  Collection <String> row1, Collection <String> row2) {
    Object result = null;

    /* short-circuit the AND expression */
    for (IBooleanExpression expression : expressions) {
      result = expression.evaluate (table1, table2, row1, row2);
      if (result == null) return null;
    }

    return result;
  }

  public String toString () {
    StringBuilder builder = new StringBuilder ();
    boolean first = true;

    if (Database.printSQL) {
      for (IBooleanExpression expression : expressions) {
	if (first)
	  first = false;
	else
	  builder.append (" and ");
	builder.append (expression);
      }
    }
    else {
      builder.append ("(AND ");
      for (IBooleanExpression expression : expressions) {
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

  public Collection <? extends IBooleanExpression> expressions;
}
