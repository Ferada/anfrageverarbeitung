package relationenalgebra;

import java.util.*;

import main.*;

public class Relation implements ITreeNode {
  public Relation (String name, String alias) {
    if ((this.name = name) == null)
      throw new NullPointerException ("relation name may not be null");
    this.alias = alias;
  }

  public String toString () {
    if (alias == null)
      return name;
    else
      return name + " as " + alias;
  }

  public Table execute (Database database) {
    Table table = database.getTable (name);

    // Database.trace ("Relation.execute, alias = " + alias);
    if (alias == null)
      return new Table (name, table);

    Collection <ColumnName> aliasColumns = new ArrayList <ColumnName> ();
    for (ColumnName original : table.columns) {
      assert (original.relation.equals (name));
      aliasColumns.add (new ColumnName (alias, original.column));
    }

    return new Table (alias, aliasColumns, table);
  }

  protected String name, alias;
}
