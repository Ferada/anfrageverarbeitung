package main;

import java.io.*;
import java.util.*;

import relationenalgebra.*;

public class Scheduler {
  public Scheduler (Database database) {
    this.database = database;
    transactions = new HashSet <Transaction> ();
  }

  public Transaction start () {
    Transaction result = new Transaction (this);
    synchronized (this) {
      transactions.add (result);
    }
    return result;
  }

  /** Ends the transaction.  Returns false if it couldn't be committed.
      Should be called by Transaction only. */
  public synchronized boolean endTransaction (Transaction transaction) {
    return false;
  }

  /** Aborts the transaction.  Should be called by Transaction only. */
  public synchronized void abortTransaction (Transaction transaction) {
    transactions.remove (transaction);
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
	Database.trace ("transaction " + transaction + " aborted");
	return null;
      }
      catch (RuntimeException exception) {
	transaction.abort ();
	Database.trace ("transaction " + transaction + " aborted, due to exception " + exception);
	throw exception;
      }
    }
    finally {
      /* not absolutely necessary, still */
      Transaction.current.set (null);
    }
  }

  protected Set <Transaction> transactions;
  protected Database database;
}
