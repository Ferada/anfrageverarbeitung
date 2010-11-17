package relationenalgebra;

/** Implements default behaviour. */
public abstract class AbstractTwoChildNode implements ITwoChildNode {
  public ITreeNode getChild () {
    return first;
  }

  public void setChild (ITreeNode child) {
    this.first = first;
  }

  public ITreeNode getSecondChild () {
    return second;
  }

  public void setSecondChild (ITreeNode child) {
    second = child;
  }

  protected ITreeNode first, second;
}
