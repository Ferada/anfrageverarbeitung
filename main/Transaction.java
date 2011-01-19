package main;

import java.io.*;
import java.util.*;

public class Transaction {
  public Transaction (Scheduler scheduler) {
    this.scheduler = scheduler;
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

  /** Temporary tables. */
  public Map <String, Table> tables;

  /** Thread local current transaction variable. */
  public static final ThreadLocal <Transaction> current = new ThreadLocal <Transaction> ();
}
