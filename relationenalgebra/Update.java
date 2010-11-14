package relationenalgebra;

import java.util.*;

public class Update extends Delete {
  public Update (Map <String, String> map, AndExpression where, String name) {
    super (where, name);
    this.map = map;
  }

  public String toString () {
    StringBuilder builder = new StringBuilder ();
    builder.append ("update ");
    builder.append (name);
    builder.append (" set ");
	
    boolean first = true;
    for (Map.Entry <String, String> entry : map.entrySet ()) {
      if (first)
	first = false;
      else
	builder.append (", ");
      builder.append (entry.getKey ());
      builder.append (" = ");
      builder.append (entry.getValue ());
    }

    if (!(where == null)) {
      builder.append (" where ");
      builder.append (where);
    }

    return builder.toString ();
  }

  protected Map <String, String> map;
}
