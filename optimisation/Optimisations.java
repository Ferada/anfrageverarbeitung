package optimisation;

import java.util.*;

import org.apache.log4j.*;

import relationenalgebra.*;
import main.*;

public class Optimisations {
  public Optimisations (Database database) {
    this.database = database;
  }

  public ITreeNode optimise (int level, ITreeNode node) {
    ModifyVisitor compact = new CompactVisitor ();
    ModifyVisitor constant = new ConstantVisitor ();
    ModifyVisitor join = new JoinVisitor ();
    ModifyVisitor split = new SplitVisitor ();
    ModifyVisitor moveDown = new MoveDownVisitor (this);
    ModifyVisitor moveProjections = new MoveProjectionsVisitor (this);
    ModifyVisitor compactSelections = new CompactSelectionsVisitor ();

    Object result = node;
    log.trace ("original expression");
    traceExpression (result);

    if (level >= 1) {
      result = split.dispatch (result);
      log.trace ("splitting selections");
      traceExpression (result);
    }

    if (level >= 2) {
      result = moveDown.dispatch (result);
      log.trace ("moving selections downward");
      traceExpression (result);
    }

    if (level >= 5) {
      result = compactSelections.dispatch (result);
      log.trace ("compacting sequences of selections");
      traceExpression (result);
    }

    if (level >= 3) {
      result = join.dispatch (result);
      log.trace ("using joins instead of cross-products");
      traceExpression (result);
    }

    if (level >= 4) {
      result = moveProjections.dispatch (result);
      log.trace ("moving projections");
      traceExpression (result);
    }

    if (level >= 7) {
      result = constant.dispatch (result);
      log.trace ("replacing constant expressions with constants");
      traceExpression (result);
    }

    if (level >= 6) {
      result = compact.dispatch (result);
      log.trace ("compacting expression tree");
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

  private static void traceDot (Object message) {
    DotPrinter printer = new DotPrinter (System.err);
    printer.print (message);
  }

  private static void traceExpression (Object message) {
    log.trace ("" + message + (Database.printSQL ? ";" : ""));
    if (Database.printDot) traceDot (message);
  }

  private Database database;

  private static Logger log = Logger.getLogger (Optimisations.class);
}
