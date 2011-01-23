package main;

import java.io.*;
import java.util.*;

import org.apache.log4j.*;

import relationenalgebra.*;

/** Runs a transaction in a concurrent thread. */
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
	log.debug ("" + transaction + " executed " + optimised);

	if (result != null) {
	  Table manifested = result.manifest ();
	  log.info ("" + transaction + " result is:");
	  Database.print (manifested.toString ());
	  Database.print ("");
	}
      }
      catch (AbortTransaction abort) {
	transaction.abort ();
	log.info ("" + transaction + " aborted: " + abort);
	return;
      }
      catch (RuntimeException exception) {
	transaction.abort ();
	log.error ("" + transaction + " failed: " + exception);
	return;
      }
      catch (Exception exception) {
	transaction.abort ();
	log.error ("" + transaction + " failed: " + exception);
	return;
      }

    boolean committed = transaction.commit ();
    log.info ("" + transaction + (committed ? " committed" : " aborted"));
  }

  protected Database database;
  protected Collection <ITreeNode> nodes;

  private static Logger log = Logger.getLogger (ClientThread.class);
}
