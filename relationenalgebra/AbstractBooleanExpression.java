package relationenalgebra;

import java.util.*;

import main.*;

/** Implements default behaviour for the single argument case. */ 
public abstract class AbstractBooleanExpression implements IBooleanExpression {
  public Object evaluate (AbstractTable table, Collection <String> row) {
    return evaluate (table, null, row, null);
  }
}
