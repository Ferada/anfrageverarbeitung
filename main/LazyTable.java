package main;

import java.io.*;
import java.util.*;

import relationenalgebra.*;

/** Implements lazy evaluation via a nested Iterator.  Although marked
    Serializable through AbstractTable, an exception will be thrown if
    you try, since it is an error to write a not fully evaluated table
    to disk. */
public class LazyTable extends AbstractTable {
  public LazyTable (String name, Collection <ColumnName> columns) {
    this (name, columns, null);
  }

  public LazyTable (String name, Collection <ColumnName> columns, Iterator <Collection <String>> iterator) {
    super (name, columns);

    assert (this.columns != null);

    /* debugging helpers */
    iterated = false;
  }

  public Iterator <Collection <String>> iterator () {
    if (iterated)
      throw new RuntimeException ("iterator () was already called earlier");
    iterated = true;
    return iterator;
  }

  public Table manifest () {
    if (table != null)
      return table;

    table = new Table (name, columns);
    for (Collection <String> row : this)
      table.add (row);
    table.costs = this.costs;

    return table;
  }

  public Iterator <Collection <String>> iterator;
  private Table table;
  private boolean iterated;

  private void writeObject(ObjectOutputStream stream) throws IOException
  {
    throw new NotSerializableException();
  }

  private Object readObject(ObjectInputStream stream) throws IOException,
    ClassNotFoundException
  {
    throw new NotSerializableException();
  }

  private static final long serialVersionUID = 234998884L;
}
