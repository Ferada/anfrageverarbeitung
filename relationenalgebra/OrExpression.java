package relationenalgebra;

import java.util.*;

import main.*;

public class OrExpression extends AbstractBooleanExpression {
  public OrExpression (Collection <? extends IBooleanExpression> expressions) {
    this.expressions = expressions;
  }

  public Object evaluate (AbstractTable table1, AbstractTable table2,
			  Collection <String> row1, Collection <String> row2) {
    Object result;

    /* short-circuit the OR expression */
    for (IBooleanExpression expression : expressions) {
      result = expression.evaluate (table1, table2, row1, row2);
      if (result != null) return result;
    }

    return null;
  }

  public String toString () {
    StringBuilder builder = new StringBuilder ();
    boolean first = true;

    if (Database.printSQL) {
      boolean braced = expressions.size () != 1;
      if (braced) builder.append ("(");

      for (IBooleanExpression expression : expressions) {
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
