package main;

import java.util.*;

import relationenalgebra.*;

import static main.Database.trace;

public class MoveDownVisitor extends ModifyVisitor {
  public MoveDownVisitor (Database database) {
    this.database = database;
  }

  public Object visit (Selection x) {
    Collection <ColumnName> selected = columnNames (x.expression),
      child = columnNames (x.child);
    trace ("selected = " + selected);
    trace ("child = " + child);
    trace ("child.containsAll (selected) = " + child.containsAll (selected));

    return x;
  }

  /** Returns a collection with all directly referenced node names. */
  private Collection <ColumnName> columnNames (Object x) {
    final Collection <ColumnName> result = new HashSet <ColumnName> ();

    Visitor v = new Visitor () {
      public void visit (Relation x) {
	Table table = x.execute (database);
	result.addAll (table.columns);
      }

      public void visit (PrimaryExpression x) {
	if (!x.constant)
	  result.add (x.name);
      }
    };

    v.dispatch (x);

    return result;
  }

  private Database database;
}
