package relationenalgebra;

import java.io.*;

/** Implements column name matching.  Works better than a simple string
    with a possible separator inside. */
public class ColumnName implements Serializable {
  public ColumnName (String relation, String column) {
    if ((this.column = column) == null)
      throw new NullPointerException ("column name may not be null");
    this.relation = relation;
  }

  /** Creates a deep copy.  Strings are immutable, so this works okay. */
  public ColumnName (ColumnName name) {
    this.relation = name.relation;
    this.column = name.column;
  }

  public String toString () {
    if (relation == null)
      return column;
    return relation + "." + column;
  }

  /** Checks if other matches this column reference.  A column can match
      exactly (name and relation are both equal), or weakly when only
      the name is equal to the other objects name and its relation
      attribute is null.
      @see hashCode */
  public boolean equals (Object object) {
    if (this == object)
      return true;
    else if (object instanceof String)
      return column.equals (object);
    else if (!(object instanceof ColumnName))
      return false;

    ColumnName other = (ColumnName) object;
    if (other.relation == null || relation == null)
      return column.equals (other.column);
    else
      return relation.equals (other.relation) && column.equals (other.column);
  }

  /** Matches the behaviour of equals (as per contract).  Since two
      objects can be equal regardless of the relation attribute, it
      can't be included in the hash, hence only column is hashed.
      @see equals */
  public int hashCode () {
    return column.hashCode ();
  }

  public String relation, column;

  private static final long serialVersionUID = 398969909L;
}
