package relationenalgebra;

import main.*;

/** Objects with this interface form an expression tree, which can be
    evaluated against a database. */
public interface ITreeNode {
  public Table execute (Database database);
}
