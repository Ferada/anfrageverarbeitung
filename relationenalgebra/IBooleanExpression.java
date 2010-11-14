package relationenalgebra;

import java.util.*;

import main.*;

public interface IBooleanExpression {
  /** Tests whether this expression could return a value on the given
      table.  Cases where this returns false may be one half of a join
      clause. */
  public boolean applicable (Table table);
  /** Return value is either an object with some meaning to the whole
      expression, or null, which is considered like boolean false. */
  public Object evaluate (Table table, Collection <String> row);
}
