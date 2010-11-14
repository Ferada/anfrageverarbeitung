package relationenalgebra;

import main.*;

public abstract class TableOperation implements ITreeNode {
  public TableOperation (String name) {
    this.name = name;
  }

  public abstract Table execute (Database database);

  protected String name;
}
