package optimisation;

import java.util.*;

import relationenalgebra.*;
import main.*;

import static main.Database.traceExpression;
import static main.Database.trace;

public class Optimisations {
  public Optimisations (Database database) {
    this.database = database;
  }

  public ITreeNode optimise (int level, ITreeNode node) {
    ModifyVisitor compact = new CompactVisitor ();
    ModifyVisitor tautology = new TautologyVisitor ();
    ModifyVisitor join = new JoinVisitor ();
    ModifyVisitor split = new SplitVisitor ();
    ModifyVisitor moveDown = new MoveDownVisitor (this);
    ModifyVisitor moveProjections = new MoveProjectionsVisitor (this);
    ModifyVisitor compactSelections = new CompactSelectionsVisitor ();

    Object result = node;
    trace ("original expression");
    traceExpression (result);

    if (level >= 1) {
      result = split.dispatch (result);
      trace ("splitting selections");
      traceExpression (result);
    }

    if (level >= 2) {
      result = moveDown.dispatch (result);
      trace ("moving selections downward");
      traceExpression (result);
    }

    if (level >= 3) {
      result = join.dispatch (result);
      trace ("using joins instead of cross-products");
      traceExpression (result);
    }

    if (level >= 5) {
      result = compactSelections.dispatch (result);
      trace ("compacting sequences of selections");
      traceExpression (result);
    }

    if (level >= 4) {
      result = moveProjections.dispatch (result);
      trace ("moving projections");
      traceExpression (result);
    }

    // result = tautology.dispatch (result);
    // traceExpression (result);

    if (level >= 6) {
      result = compact.dispatch (result);
      trace ("compacting expression tree");
      traceExpression (result);
    }

    return (ITreeNode) result;
  }

  /** Returns a collection with all directly referenced node names. */
  public Collection <ColumnName> columnNames (Object x) {
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
