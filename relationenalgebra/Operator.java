package relationenalgebra;

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
