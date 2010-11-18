package relationenalgebra;

import java.util.*;

import main.*;

/** Constant value, created when pre-evaluating expressions. */
public class ConstantBooleanExpression extends AbstractBooleanExpression {
  public ConstantBooleanExpression (boolean value) {
    this.value = value ? marker : null;
  }

  public Object evaluate (AbstractTable table1, AbstractTable table2,
				   Collection <String> row1, Collection <String> row2) {
    return value;
  }

  public String toString () {
    return Database.printSQL ? ("" + (value != null)) : "(CONSTANT " + (value != null) + ")";
  }

  private final Object value;
  private static final Object marker = new Object ();
}
