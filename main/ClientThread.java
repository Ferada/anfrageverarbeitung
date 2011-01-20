package main;

import java.io.*;
import java.util.*;

import relationenalgebra.*;

public class ClientThread extends Thread {
  public ClientThread (Database database, Collection <ITreeNode> nodes) {
    this.database = database;
    this.nodes = nodes;
  }

  public void run () {
    Transaction transaction = database.scheduler.start ();
    transaction.name = getName ();
    Transaction.current.set (transaction);

    for (ITreeNode node : nodes)
      try {
	ITreeNode optimised = database.optimise (node);
	AbstractTable result = optimised.execute (database);
	if (result != null) {
	  Table manifested = result.manifest ();
	  Database.print (manifested.toString ());
	  Database.print ("");
	}
      }
      catch (AbortTransaction abort) {
	transaction.abort ();
	Database.trace ("" + transaction + " aborted: " + abort);
	return;
      }
      catch (RuntimeException exception) {
	transaction.abort ();
	Database.trace ("" + transaction + " failed: " + exception);
	return;
      }
      catch (Exception exception) {
	transaction.abort ();
	Database.trace ("" + transaction + " failed: " + exception);
	return;
      }

    boolean committed = transaction.commit ();
    Database.trace ("" + transaction + (committed ? " committed" : " aborted"));
  }

  protected Database database;
  protected Collection <ITreeNode> nodes;
}
