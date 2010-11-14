package relationenalgebra;

import java.util.*;

import main.*;

public class EqualityExpression implements IBooleanExpression {
  public EqualityExpression (PrimaryExpression expression) {
    this.first = expression;
  }

  public EqualityExpression (PrimaryExpression first, PrimaryExpression second, Operator operator) {
    this.first = first;
    this.second = second;
    this.operator = operator;
  }

  public Object evaluate (Table table, Collection <String> row) {
    // Database.trace ("columns = " + table.columns);
    // Database.trace ("row = " + row);
    // Database.trace ("this.first = " + this.first);
    // Database.trace ("this.second = " + this.second);

    String first = (String) this.first.evaluate (table, row);

    // Database.trace ("first = " + first);

    if (this.second == null)
      return first;

    String second = (String) this.second.evaluate (table, row);

    // Database.trace ("second = " + second);

    boolean equal = first.equals (second);

    // Database.trace ("equal = " + equal);
    // Database.trace ("operator = " + operator);

    switch (operator) {
    case EQUAL:
    case LESS_EQUAL:
    case GREATER_EQUAL:
      return equal ? first : null;
    case UNEQUAL:
      return equal ? null : first;
    }

    boolean less = first.compareTo (second) < 0;

    switch (operator) {
    case LESS:
    case LESS_EQUAL:
      return less ? first : null;
    case GREATER:
    case GREATER_EQUAL:
      return less ? null : first;
    }

    /* can't happen ... lol */
    assert (false);

    return null;
  }

  public String toString () {
    if (second == null) {
      return first.toString ();
    }
    else {
      return "(" + first + " " + operator + " " + second + ")";
    }
  }

  protected PrimaryExpression first, second;
  protected Operator operator;
}
