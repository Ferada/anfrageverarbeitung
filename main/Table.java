package main;

import java.io.*;
import java.util.*;
import relationenalgebra.*;

/** Stores rows of data and prints them nicely. */
public class Table implements Serializable, Iterable <Collection <String>> {
  /** Creates a new empty table. */
  public Table (String name, Collection <ColumnName> columns) {
    this.name = name;
    this.columns = columns;
    this.rows = new ArrayList <Collection <String>> ();
    costs = 0;
    length = 0;

    assert (this.columns != null);
  }

  /** Creates a new table, sharing both columns and rows with the
      original one. */
  public Table (String name, Table table) {
    this (name, table.columns, table);
  }

  /** Creates a new table, sharing rows with the original one.  The new
      columns have to of the same length as the original ones, e.g. for
      renaming columns after Relation operator. */
  public Table (String name, Collection <ColumnName> columns, Table table) {
    this.name = name;
    this.columns = columns;
    rows = table.rows;
    costs = 0;
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
    if (Database.calculateCosts) {
      builder.append (", costs ");
      builder.append (costs);
    }
    builder.append (")");

    return builder.toString ();
  }

  /** A name for this table.  Temporary tables don't have one. */
  public String name;
  public Collection <ColumnName> columns;
  protected Collection <Collection <String>> rows;
  public int length;
  public int costs;

  private static final long serialVersionUID = 234998883L;
}
