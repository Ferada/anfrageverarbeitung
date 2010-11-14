package relationenalgebra;

import main.*;

public class Delete extends TableOperation {
  public Delete (AndExpression where, String name) {
    super (name);
    this.where = where;
  }

  public String toString () {
    StringBuilder builder = new StringBuilder ("delete from ");
    builder.append (name);

    if (!(where == null)) {
      builder.append (" where ");
      builder.append (where);
    }

    return builder.toString ();
  }

  public Table execute (Database database) {
    Database.trace ("not implemented (yet)");

    return null;
  }

  protected AndExpression where;
}
