package relationenalgebra;

import java.io.*;

public class ColumnName implements Serializable {
  public ColumnName (String relation, String column) {
    if ((this.column = column) == null)
      throw new NullPointerException ("column name may not be null");
    this.relation = relation;
  }

  public String toString () {
    if (relation == null)
      return column;
    return relation + "." + column;
  }

  /** Checks if other matches this column reference. */
  public boolean equals (Object object) {
    if (this == object)
      return true;
    else if (!(object instanceof ColumnName))
      return false;

    ColumnName other = (ColumnName) object;
    if (relation == null) {
      if (other.relation == null)
	return column.equals (other.column);
      else
	return false;
    }
    else {
      if (other.relation == null)
	return column.equals (other.column);
      else
	return relation.equals (other.relation) && column.equals (other.column);
    }
  }

  public String relation, column;

  private static final long serialVersionUID = 398969909L;
}
