package main;

import java.io.*;
import java.util.*;

import relationenalgebra.*;

public abstract class AbstractTable implements Iterable <Collection <String>>, Serializable {
  public AbstractTable (String name, Collection <ColumnName> columns) {
    this.name = name;
    this.columns = columns;
    length = 0;
    costs = 0;
  }

  public abstract Iterator <Collection <String>> iterator ();
  public abstract Table manifest ();

  /** A name for this table.  Temporary tables don't have one. */
  public String name;
  public Collection <ColumnName> columns;
  public int length;
  public int costs;
}
