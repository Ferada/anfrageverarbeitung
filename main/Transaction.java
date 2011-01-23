package main;

import java.io.*;
import java.util.*;

import org.apache.log4j.*;

/** Implements the transaction client-wise. */
public class Transaction {
  public Transaction (Scheduler scheduler, Database database, int timestamp) {
    this.scheduler = scheduler;
    this.database = database;
    this.timestamp = timestamp;
    tables = new HashMap <String, Table> ();
  }

  /** Convenience function. */
  public boolean commit () {
    return scheduler.endTransaction (this);
  }

  /** Convenience function. */
  public void abort () {
    scheduler.abortTransaction (this);
  }

  public Scheduler scheduler;
  public Database database;

  /** Temporary tables. */
  public Map <String, Table> tables;

  public int timestamp;
  public String name;

  /** Thread local current transaction variable. */
  public static final ThreadLocal <Transaction> current = new ThreadLocal <Transaction> ();

  public Table getTable (String name) {
    if (tables.containsKey (name))
      return tables.get (name);
    else {
      Table table = scheduler.copyTable (name, this);
      tables.put (name, table);
      return table;
    }
  }

  public void addTable (Table table) {
    if (tables.containsKey (table.name))
      log.trace ("overwriting table " + table.name + " with new table");
    tables.put (table.name, table);
  }

  public void removeTable (Table table) {
    tables.put (table.name, null);
  }

  public String toString () {
    return (name != null) ? ("Transaction <" + name + ">") : super.toString ();
  }

  static Logger log = Logger.getLogger (Transaction.class);
}
