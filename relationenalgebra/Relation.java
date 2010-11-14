package relationenalgebra;

import main.*;

public class Relation implements ITreeNode {
  public Relation (String name, String alias) {
    if ((this.name = name) == null)
      throw new NullPointerException ("relation name can't be null");
    this.alias = alias;
  }

  public String toString () {
    if (alias == null)
      return name;
    else
      return name + " as " + alias;
  }

  public Table execute (Database database) {
    // Database.trace ("Relation.execute, alias = " + alias);
    return new Table (alias == null ? name : alias, database.getTable (name));
  }

  protected String name, alias;
}
