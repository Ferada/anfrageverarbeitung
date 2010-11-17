package relationenalgebra;

import java.util.*;

import main.*;

/** Expressions which can be evaluated to a truth value. */ 
public interface IBooleanExpression {
  /** Return value is either an object with some meaning to the whole
      expression, or null, which is considered like boolean false. */
  public Object evaluate (Table table, Collection <String> row);

  /** Same thing for two separate tables/rows. */
  public abstract Object evaluate (Table table1, Table table2,
				   Collection <String> row1, Collection <String> row2);
}
