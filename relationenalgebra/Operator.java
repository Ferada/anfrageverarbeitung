package relationenalgebra;

/** Defines all available operations for the EqualityExpression.
    @see EqualityExpression */
public enum Operator {
  EQUAL ("="),
  UNEQUAL ("!="),
  LESS ("<"),
  GREATER (">"),
  LESS_EQUAL ("<="),
  GREATER_EQUAL (">=");

  private final String glyph;

  Operator (String glyph) {
    this.glyph = glyph;
  }

  public String toString () {
    return glyph;
  }

  public static Operator getOperator (int i) {
    return Operator.values ()[i];
  }
}
