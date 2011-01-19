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
    Transaction.current.set (transaction);

    for (ITreeNode node : nodes)
      try {
	AbstractTable result = node.execute (database);
	if (result != null) {
	  Table manifested = result.manifest ();
	  Database.print (manifested.toString ());
	  Database.print ("");
	}
      }
      catch (AbortTransaction abort) {
	transaction.abort ();
	Database.trace ("transaction " + transaction + " aborted");
	return;
      }

    boolean committed = transaction.commit ();
    Database.trace ("transaction " + transaction + (committed ? " committed" : " aborted"));
  }

  protected Database database;
  protected Collection <ITreeNode> nodes;
}
