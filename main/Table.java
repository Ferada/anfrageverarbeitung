package main;

import java.io.*;
import java.util.*;
import relationenalgebra.*;

public class Table implements Serializable, Iterable <Collection <String>> {
  public Table (String name, Collection <String> columns) {
    this.name = name;
    this.columns = columns;
    this.rows = new ArrayList <Collection <String>> ();

    assert (this.columns != null);
  }

  public Table (String name, Table table) {
    this.name = name;
    columns = table.columns;
    rows = table.rows;
  }

  public void add (Collection <String> row) {
    rows.add (row);
  }

  public Iterator <Collection <String>> iterator () {
    return rows.iterator ();
  }

  public String toString () {
    StringBuilder builder = new StringBuilder (name == null ? "" : (" " + name));
    StringBuilder aux = new StringBuilder ();

    if (name != null) {
      aux.append ('\n');
      for (int i = 0; i < name.length (); ++i)
	aux.append ("-");
      aux.append ('\n');
    }
    builder.append (aux);

    aux = new StringBuilder ();

    boolean first = true;
    builder.append (" ");
    for (String column : columns) {
      if (first)
	first = false;
      else
	builder.append (" | ");
      if (name != null) {
	builder.append (name);
	builder.append ('.');
      }
      builder.append (column);
    }
    builder.append ('\n');

    for (int i = 0; i < builder.length () - 1; ++i)
      aux.append ('-');
    aux.append ('\n');
    builder.append (aux);

    int i = 0;
    for (Collection <String> row : rows) {
      ++i;
      first = true;
      builder.append (" ");
      for (String value : row) {
	if (first)
	  first = false;
	else
	  builder.append (" | ");
	builder.append (value);
      }
      builder.append ('\n');
    }

    builder.append ("(");
    builder.append (i);
    builder.append ((i == 1) ? " row)" : " rows)");

    return builder.toString ();
  }

  /** A name for this table.  Temporary tables might not have one. */
  public String name;
  public Collection <String> columns;
  protected Collection <Collection <String>> rows;
}
