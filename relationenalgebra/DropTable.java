package relationenalgebra;

import main.*;

public class DropTable extends TableOperation {
  public DropTable (String name) {
    super (name);
  }

  public String toString () {
    return "drop " + name;
  }

  public Table execute (Database database) {
    database.remove (database.getTable (name));
    return null;
  }
}
