package relationenalgebra;

/** Has two children. */
public interface ITwoChildNode extends IOneChildNode {
  public ITreeNode getSecondChild ();
  public void setSecondChild (ITreeNode child);
}
