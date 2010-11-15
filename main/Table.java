package main;

import java.io.*;
import java.util.*;
import relationenalgebra.*;

public class Table implements Serializable, Iterable <Collection <String>> {
  public Table (String name, Collection <ColumnName> columns) {
    this.name = name;
    this.columns = columns;
    this.rows = new ArrayList <Collection <String>> ();
    costs = 0;
    length = 0;

    assert (this.columns != null);
  }

  public Table (String name, Table table) {
    this (name, table.columns, table);
  }

  public Table (String name, Collection <ColumnName> columns, Table table) {
    this.name = name;
    this.columns = columns;
    rows = table.rows;
    costs = -1;
    length = rows.size ();
  }

  public void add (Collection <String> row) {
    rows.add (row);
    ++length;
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
    for (ColumnName column : columns) {
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
    builder.append ((i == 1) ? " row" : " rows");
    if (costs != -1) {
      builder.append (", costs ");
      builder.append (costs);
    }
    builder.append (")");

    return builder.toString ();
  }

  /** A name for this table.  Temporary tables might not have one. */
  public String name;
  public Collection <ColumnName> columns;
  protected Collection <Collection <String>> rows;
  public int length;
  public int costs;

  private static final long serialVersionUID = 234998883L;
}
