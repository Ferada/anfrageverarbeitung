package main;

import java.io.*;
import java.util.*;

import relationenalgebra.*;

/* locking order is always this before transactions! */
public class Scheduler {
  public Scheduler (Database database) {
    this.database = database;
    transactions = new HashSet <Transaction> ();
    timestamps = new HashMap <String, Timestamp> ();
  }

  public void join () {
    for (;;)
      synchronized (transactions) {
	if (transactions.isEmpty ())
	  return;
	try {
	  transactions.wait ();
	}
	catch (InterruptedException interrupted) {}
	catch (Exception exception) {
	  Database.trace ("exception caught, join aborted: " + exception);
	  return;
	}
      }
  }

  public synchronized Transaction start () {
    Transaction result = new Transaction (this, this.database, timestamp++);
    synchronized (transactions) {
      transactions.add (result);
      transactions.notify ();
    }
    return result;
  }

  protected void removeTransaction (Transaction transaction) {
    synchronized (transactions) {
      if (!transactions.remove (transaction))
	Database.trace ("couldn't remove " + transaction);
      transactions.notify ();
    }
  }

  /** Ends the transaction.  Returns false if it couldn't be committed.
      Should be called by Transaction only. */
  public synchronized boolean endTransaction (Transaction transaction) {
    removeTransaction (transaction);

    Map <String, Table> tables = transaction.tables;
    Set <String> keys = tables.keySet ();
    int timestamp = transaction.timestamp;
    for (String key : keys) {
      Timestamp tableTimestamp = getTimestamp (key);

      if (timestamp < tableTimestamp.read) {
	// Database.trace ("" + transaction + " aborted, " + timestamp + " < " + tableTimestamp.read);
	return false;
      }
    }

    /* commit */

    for (String key : keys) {
      Timestamp tableTimestamp = getTimestamp (key);
      if (timestamp >= tableTimestamp.write &&
	  timestamp >= tableTimestamp.read) {
	database.tables.put (key, tables.get (key));
	Database.trace ("wrote table " + key + " back to database");
      }
      else Database.trace ("dismissed old table copy " + key + " back to database");
    }

    return true;
  }

  /** Aborts the transaction.  Should be called by Transaction only. */
  public void abortTransaction (Transaction transaction) {
    removeTransaction (transaction);
  }

  /** Executes a single expression in a convenient way.  Use ClientThread
      otherwise. */
  public Table execute (ITreeNode node) {
    Transaction transaction = start ();
    Transaction.current.set (transaction);

    try {
      try {
	AbstractTable result = node.execute (database);
	transaction.commit ();
	if (result == null)
	  return null;
	return result.manifest ();
      }
      catch (AbortTransaction abort) {
	transaction.abort ();
	Database.trace ("" + transaction + " aborted: " + abort);
	return null;
      }
      catch (RuntimeException exception) {
	transaction.abort ();
	Database.trace ("" + transaction + " failed: " + exception);
	throw exception;
      }
      catch (Exception exception) {
	transaction.abort ();
	Database.trace ("" + transaction + " failed: " + exception);
	throw new RuntimeException (exception);
      }
    }
    finally {
      /* not absolutely necessary, still */
      Transaction.current.set (null);
    }
  }

  protected Timestamp getTimestamp (String name) {
    if (timestamps.containsKey (name))
      return timestamps.get (name);
    Timestamp result = new Timestamp ();
    timestamps.put (name, result);
    return result;
  }

  public void updateRead (String name, int newTimestamp) {
    Timestamp timestamp = getTimestamp (name);
    // Database.trace ("trying to update \"" + name + "\" read timestamp from " + timestamp.read + " to " + newTimestamp);

    // Database.trace ("" + newTimestamp + " >= " + timestamp.write + " = " + (newTimestamp >= timestamp.write));
    if (newTimestamp >= timestamp.write) {
      // Database.trace ("" + timestamp.read + " -> " + ((newTimestamp >= timestamp.read) ? newTimestamp : timestamp.read));
      timestamp.read = (newTimestamp >= timestamp.read) ? newTimestamp : timestamp.read;
    }
    else throw new AbortTransaction ("can't read table with higher write timestamp, t < t_w");
  }

  public void updateWrite (String name, int newTimestamp) {
    Timestamp timestamp = getTimestamp (name);
    Database.trace ("trying to update \"" + name + "\" write timestamp from " + timestamp.write + " to " + newTimestamp);
    timestamp.write = newTimestamp;
  }

  public synchronized Table copyTable (String name, Transaction transaction) {
    Table table = database.tables.get (name);

    /* give a meaningful error at least */
    if (table == null)
      throw new NullPointerException ("no table of name \"" + name + "\" exists");

    Table result = new Table (table);

    updateRead (name, transaction.timestamp);

    return result;
  }

  protected Set <Transaction> transactions;
  protected Database database;
  protected Map <String, Timestamp> timestamps;
  protected int timestamp;
}
