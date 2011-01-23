package main;

/** Exception used to abort a running transaction.  Used instead of
    rewriting every expression with transaction-aware code. */
public class AbortTransaction extends RuntimeException {
  /** We only allow named aborts to faciliate debugging. */
  public AbortTransaction (String description) {
    super (description);
  }

  private static final long serialVersionUID = 92384908563L;
}
