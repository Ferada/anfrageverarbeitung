package relationenalgebra;

import main.*;

/** Objects with this interface form an expression tree, which can be
    evaluated against a database.  A node is cloneable and implements a
    shallow strategy to allow sharing between different terms in order
    to use less memory when optimizing expressions. */
public interface ITreeNode extends Cloneable {
  public AbstractTable execute (Database database);
}
