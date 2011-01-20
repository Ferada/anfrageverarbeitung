package main;

public class AbortTransaction extends RuntimeException {
  public AbortTransaction (String description) {
    super (description);
  }

  private static final long serialVersionUID = 92384908563L;
}
