package relationenalgebra;

/** Implements default behaviour. */
public abstract class AbstractOneChildNode implements IOneChildNode {
  public ITreeNode getChild () {
    return child;
  }

  public void setChild (ITreeNode child) {
    this.child = child;
  }

  protected ITreeNode child;
}
