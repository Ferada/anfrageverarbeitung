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
    String first = (String) this.first.evaluate (table1, table2, row1, row2);

    /* passing through */
    if (this.second == null)
      return first;

    String second = (String) this.second.evaluate (table1, table2, row1, row2);

    boolean equal = first.equals (second);

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

  public PrimaryExpression first, second;
  public Operator operator;
}
