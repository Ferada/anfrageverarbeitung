package main;

import java.io.*;
import java.util.*;

import relationenalgebra.*;

/** Stores rows of data and prints them nicely. */
public class Table extends AbstractTable implements Serializable {
  /** Creates a new empty table. */
  public Table (String name, Collection <ColumnName> columns) {
    super (name, columns);

    this.rows = new ArrayList <Collection <String>> ();
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
    super (name, columns);

    rows = table.rows;
    length = rows.size ();
  }

  /** Creates a deep copy. */
  public Table (Table table) {
    columns = new ArrayList <ColumnName> ();
    for (ColumnName name : table.columns)
      columns.add (new ColumnName (name));

    rows = new ArrayList <Collection <String>> ();
    for (Collection <String> row : table.rows)
      rows.add (new ArrayList <String> (row));

    length = table.length;
    costs = table.costs;
  }

  public Iterator <Collection <String>> iterator () {
    return rows.iterator ();
  }

  public Table manifest () {
    return this;
  }

  public void add (Collection <String> row) {
    rows.add (row);
    ++length;
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
    builder.append (", costs ");
    builder.append (costs);
    builder.append (")");

    return builder.toString ();
  }

  protected Collection <Collection <String>> rows;

  private static final long serialVersionUID = 234998883L;
}
