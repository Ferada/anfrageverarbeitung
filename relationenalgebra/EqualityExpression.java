package relationenalgebra;

import java.util.*;

import main.*;

/** Compares two expressions with each other.  Comparison operators are
    the ones in the Operator enumeration.
    @see Operator */
public class EqualityExpression extends AbstractBooleanExpression {
  /** Not really comparing, just passing through. */
  public EqualityExpression (PrimaryExpression expression) {
    this.first = expression;
  }

  /** The first expression is compared with an operator against the
      second expression. */
  public EqualityExpression (PrimaryExpression first, PrimaryExpression second, Operator operator) {
    this.first = first;
    this.second = second;
    this.operator = operator;
  }

  public Object evaluate (AbstractTable table1, AbstractTable table2, Collection <String> row1, Collection <String> row2) {
    // Database.trace ("columns = " + table.columns);
    // Database.trace ("row = " + row);
    // Database.trace ("this.first = " + this.first);
    // Database.trace ("this.second = " + this.second);

    String first = (String) this.first.evaluate (table1, table2, row1, row2);

    // Database.trace ("first = " + first);

    /* passing through */
    if (this.second == null)
      return first;

    String second = (String) this.second.evaluate (table1, table2, row1, row2);

    // Database.trace ("second = " + second);

    boolean equal = first.equals (second);

    // Database.trace ("equal = " + equal);
    // Database.trace ("operator = " + operator);

    /* test for equality */
    switch (operator) {
    case EQUAL:
    case LESS_EQUAL:
    case GREATER_EQUAL:
      return equal ? first : null;
    case UNEQUAL:
      return equal ? null : first;
    }

    boolean less = first.compareTo (second) < 0;

    /* test for less or greater */
    switch (operator) {
    case LESS:
    case LESS_EQUAL:
      return less ? first : null;
    case GREATER:
    case GREATER_EQUAL:
      return less ? null : first;
    }

    /* something went wrong */
    throw new RuntimeException ("couldn't evaluate EqualityExpression " + this);
  }

  public String toString () {
    if (Database.printSQL) {
      if (second == null)
	return first.toString ();
      else
	return "(" + first + " " + operator + " " + second + ")";
    }
    else {
      if (second == null)
	return "(PRIMARY " + first + ")";
      else
	return "(PRIMARY " + operator + " " + first + " " + second + ")";
    }
  }

  protected PrimaryExpression first, second;
  protected Operator operator;
}
