package relationenalgebra;

import java.util.*;

import main.*;

/** Expressions which can be evaluated to a truth value.  Is cloneable;
    the same comments as for ITreeNode apply.
    @see ITreeNode */
public interface IBooleanExpression extends Cloneable {
  /** Return value is either an object with some meaning to the whole
      expression, or null, which is considered like boolean false. */
  public Object evaluate (AbstractTable table, Collection <String> row);

  /** Same thing for two separate tables/rows. */
  public abstract Object evaluate (AbstractTable table1, AbstractTable table2,
				   Collection <String> row1, Collection <String> row2);
}
