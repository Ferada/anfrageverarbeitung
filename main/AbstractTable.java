package main;

import java.io.*;
import java.util.*;

import relationenalgebra.*;

/** Groups common behaviour/attributes for Table and LazyTable. */
public abstract class AbstractTable implements Iterable <Collection <String>>, Serializable {
  public AbstractTable (String name, Collection <ColumnName> columns) {
    this.name = name;
    this.columns = columns;
    length = 0;
    costs = 0;
  }

  /** Returns values row by row.  Should only be called once. */
  public abstract Iterator <Collection <String>> iterator ();

  /** Ensures the table is fully evaluated and returns an object with
      the resulting rows.  The return value may the same object if it
      was of type Table to begin with.  Don't mix iterator and manifest
      calls.  Can be called multiple times, but probably won't return a
      new object. */
  public abstract Table manifest ();

  /** A name for this table.  Temporary tables don't have one. */
  public String name;
  public Collection <ColumnName> columns;
  public int length;
  public int costs;
}
