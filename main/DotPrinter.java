package main;

import java.util.*;
import java.io.*;

import relationenalgebra.*;
import optimisation.*;

/** Prints a syntax tree in Graphviz syntax. */
public class DotPrinter extends Visitor {
  public DotPrinter (PrintStream stream) {
    this.stream = stream;
    ids = new HashMap <Object, String> ();
  }

  public void print (Object object) {
    stream.println ("digraph {");
    dispatch (object);
    stream.println ("}");
  }
  
  public void visit (Projection x) {
    StringBuilder builder = new StringBuilder ("&pi; (");
    boolean first = true;
    for (ColumnName name : x.columns) {
      if (first) first = false;
      else builder.append (", ");
      builder.append (name);
    }
    builder.append (")");

    label (x, builder.toString ());
    edge (x, x.child);
    super.visit (x);
  }

  public void visit (Selection x) {
    label (x, "&sigma; " + x.expression.toString ());
    edge (x, x.child);
    super.visit (x);
  }

  public void visit (CrossProduct x) {
    label (x, "&#10799;");
    edge (x, x.first);
    edge (x, x.second);
    super.visit (x);
  }

  public void visit (Join x) {
    label (x, "&#8904; " + x.expression);
    edge (x, x.first);
    edge (x, x.second);
    super.visit (x);
  }

  public void visit (Relation x) {
    label (x, x.toString ());
  }

  private void label (Object object, String label) {
    stream.println (id (object) + " [label=\"" + label.replaceAll ("\"", "\\\\\"") + "\"];");
  }

  private void edge (Object obj1, Object obj2) {
    stream.println (id (obj1) + " -> " + id (obj2) + ";");
  }

  private String id (Object object) {
    if (ids.containsKey (object))
      return ids.get (object);
    String result = object.getClass ().getName ().replace ('.', '_') + counter++;
    ids.put (object, result);
    return result;
  }

  private PrintStream stream;
  private Map <Object, String> ids;
  private int counter;
}
