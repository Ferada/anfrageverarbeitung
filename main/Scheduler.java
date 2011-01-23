package main;

import java.io.*;
import java.util.*;

import org.apache.log4j.*;

import relationenalgebra.*;

/* locking order is always this before transactions! */
/** Implements the transaction management. */
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
	  log.debug ("exception caught, join aborted: " + exception);
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
	log.warn ("couldn't remove " + transaction);
      transactions.notify ();
    }
  }

  /** Ends the transaction.  Returns false if it couldn't be committed.
      Should be called by Transaction only. */
  public synchronized boolean endTransaction (Transaction transaction) {
    removeTransaction (transaction);

    Map <String, Table> tables = transaction.tables;
    Set <String> keys = tables.keySet ();
    for (String key : keys) {
      Timestamp timestamp = getTimestamp (key);

      if (transaction.timestamp < timestamp.read) {
	log.trace ("" + transaction + " aborted, " + transaction.timestamp + " < " + timestamp.read);
	return false;
      }
    }

    /* commit */

    for (String key : keys) {
      Timestamp timestamp = getTimestamp (key);
      if (transaction.timestamp >= timestamp.write &&
	  transaction.timestamp >= timestamp.read) {
	database.tables.put (key, tables.get (key));
	timestamp.write = (transaction.timestamp >= timestamp.write) ? transaction.timestamp : timestamp.write;
	log.debug ("wrote table " + key + " back to database");
      }
      else log.debug ("dismissed old table copy " + key + " back to database");
    }

    return true;
  }

  /** Aborts the transaction.  Should be called by Transaction only. */
  public void abortTransaction (Transaction transaction) {
    removeTransaction (transaction);
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
    log.trace ("trying to update \"" + name + "\" read timestamp from " + timestamp.read + " to " + newTimestamp);

    log.trace ("" + newTimestamp + " >= " + timestamp.write + " = " + (newTimestamp >= timestamp.write));
    if (newTimestamp >= timestamp.write) {
      log.trace ("" + timestamp.read + " -> " + ((newTimestamp >= timestamp.read) ? newTimestamp : timestamp.read));
      timestamp.read = (newTimestamp >= timestamp.read) ? newTimestamp : timestamp.read;
    }
    else throw new AbortTransaction ("can't read table with higher write timestamp, t < t_w");
  }

  public void updateWrite (String name, int newTimestamp) {
    Timestamp timestamp = getTimestamp (name);
    log.trace ("trying to update \"" + name + "\" write timestamp from " + timestamp.write + " to " + newTimestamp);
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

  private static Logger log = Logger.getLogger (Scheduler.class);
}
