package optimisation;

import java.util.*;

import relationenalgebra.*;
import main.*;

import static main.Database.trace;
import static main.Database.traceExpression;

public class MoveProjectionsVisitor extends ModifyVisitor {
  public MoveProjectionsVisitor (Optimisations optimisations) {
    this.optimisations = optimisations;
  }

  public Object visit (Projection x) {
    while (x.child instanceof Projection) {
      Projection projection = (Projection) x.child;
      x.child = projection.child;
    }

    if (x.child instanceof Selection) {
      Selection result = (Selection) x.child;

      Collection <ColumnName> names = optimisations.columnNames (result.expression);

      /* move projection downwards; the projection has the selection
	 columns or more, so no problem */
      if (x.columns.containsAll (names)) {
	x.child = result.child;
	result.child = x;
	return dispatch (result);
      }
      /* move projection downwards by adding all names from the selection */
      else {
	names.addAll (x.columns);

	result.child = (ITreeNode) dispatch (new Projection (names, result.child));

	return x;
      }
    }
    else if (x.child instanceof CrossProduct) {
      Collection <ColumnName> firstNames = new ArrayList <ColumnName> (x.columns),
	secondNames = new ArrayList <ColumnName> (firstNames);

      if (x.child instanceof Join) {
	Join result = (Join) x.child;

	Collection <ColumnName> affected = optimisations.columnNames (result.expression);

	if (!x.columns.containsAll (affected)) {
	  firstNames.addAll (affected);
	  secondNames.addAll (affected);

	  firstNames.retainAll (optimisations.columnNames (result.first));
	  secondNames.retainAll (optimisations.columnNames (result.second));

	  if (!x.columns.containsAll (firstNames))
	    result.first = new Projection (firstNames, result.first);
	  if (!x.columns.containsAll (secondNames))
	    result.second = new Projection (secondNames, result.second);
	  
	  result.first = (ITreeNode) dispatch (result.first);
	  result.second = (ITreeNode) dispatch (result.second);

	  return x;
	}
      }
      /* this is executed if it's only a cross-product or if the join
	 expression contains only names from the projection names */
      {
	CrossProduct result = (CrossProduct) x.child;

	firstNames.retainAll (optimisations.columnNames (result.first));
	secondNames.retainAll (optimisations.columnNames (result.second));

	result.first = (ITreeNode) dispatch (new Projection (firstNames, result.first));
	result.second = (ITreeNode) dispatch (new Projection (secondNames, result.second));

	return result;
      }
    }

    x.child = (ITreeNode) dispatch (x.child);
    return x;
  }

  private Optimisations optimisations;
}
