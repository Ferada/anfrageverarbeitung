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
    if (Database.printSQL)
      if (alias == null)
	return name;
      else
	return name + " as " + alias;
    else
      return "(AS " + alias + " " + name + ")";
  }

  public Table execute (Database database) {
    Table table = database.getTable (name);
    Table result;

    // Database.trace ("Relation.execute, alias = " + alias);

    /* if alias is null, we just create a unnamed temporary table to
       achieve consistency */
    if (alias == null)
      result = new Table (name, table);
    else {
      Collection <ColumnName> aliasColumns = new ArrayList <ColumnName> ();
      for (ColumnName original : table.columns) {
	assert (original.relation.equals (name));
	/* rewrite column names to the new alias */
	aliasColumns.add (new ColumnName (alias, original.column));
      }

      result = new Table (alias, aliasColumns, table);
    }

    return result;
  }

  protected String name, alias;
}
