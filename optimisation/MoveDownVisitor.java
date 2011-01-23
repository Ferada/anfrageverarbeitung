package optimisation;

import java.util.*;

import org.apache.log4j.*;

import relationenalgebra.*;
import main.*;

/** Moves Selection objects as far down the expression tree as possible. */
public class MoveDownVisitor extends ModifyVisitor {
  public MoveDownVisitor (Optimisations optimisations) {
    this.optimisations = optimisations;
    done = new HashSet <ITreeNode> ();
  }

  public Object visit (Selection x) {
    if (done.contains (x))
      return x;

    traceExpression (x);

    /* switch selections, continuing with this selection though */
    if (x.child instanceof Selection) {
      Selection result = (Selection) x.child;
      x.child = result.child;
      result.child = (ITreeNode) dispatch (x);
      done.add (x);
      return dispatch (result);
    }
    else if (x.child instanceof Projection) {
      Projection result = (Projection) x.child;
      x.child = result.child;
      result.child = (ITreeNode) dispatch (x);
      done.add (x);
      return dispatch (result);
    }
    else if (x.child instanceof CrossProduct) {
      CrossProduct result = (CrossProduct) x.child;

      Collection <ColumnName> selected = optimisations.columnNames (x.expression),
	names = optimisations.columnNames (result.first);

      log.trace ("selected = " + selected);
      log.trace ("names = " + names);
      log.trace ("names.containsAll (selected) = " + names.containsAll (selected));

      if (names.containsAll (selected)) {
	x.child = result.first;
	result.first = (ITreeNode) dispatch (x);
	done.add (x);
	return dispatch (result);
      }

      names = optimisations.columnNames (result.second);

      log.trace ("names = " + names);
      log.trace ("names.containsAll (selected) = " + names.containsAll (selected));

      if (names.containsAll (selected)) {
	x.child = result.second;
	result.second = (ITreeNode) dispatch (x);
	done.add (x);
	return dispatch (result);
      }
    }

    x.child = (ITreeNode) dispatch (x.child);

    done.add (x);
    return x;
  }

  private static void traceDot (Object message) {
    DotPrinter printer = new DotPrinter (System.err);
    printer.print (message);
  }

  private static void traceExpression (Object message) {
    log.trace ("" + message + (Database.printSQL ? ";" : ""));
    if (Database.printDot) traceDot (message);
  }

  private Optimisations optimisations;
  /** Marks already visited objects, which shouldn't be visited again
      because of loops (e.g. two selections which could be in any order). */
  private Set <ITreeNode> done;

  private static Logger log = Logger.getLogger (MoveDownVisitor.class);
}
