package relationenalgebra;

/** Has one child. */
public interface IOneChildNode extends ITreeNode {
  public ITreeNode getChild ();
  public void setChild (ITreeNode child);
}
