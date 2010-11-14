package relationenalgebra;

import java.util.*;

import main.*;

public interface IBooleanExpression {
  /** Return value is either an object with some meaning to the whole
      expression, or null, which is considered like boolean false. */
  public Object evaluate (Table table, Collection <String> row);
}
