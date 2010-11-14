package relationenalgebra;

import java.util.*;

import main.*;

public class CrossProduct implements ITwoChildNode {
  public CrossProduct (ITreeNode first, ITreeNode second) {
    this.first = first;
    this.second = second;
  }

  public ITreeNode getChild () {
    return first;
  }

  public void setChild (ITreeNode child) {
    first = child;
  }

  public ITreeNode getSecondChild () {
    return second;
  }

  public void setSecondChild (ITreeNode child) {
    second = child;
  }

  public String toString () {
    return first + ", " + second;
  }

  public Table execute (Database database) {
    Table first = this.first.execute (database);
    Table second = this.second.execute (database);
    Collection <String> columns = new ArrayList <String> ();

    // Database.trace ("CrossProduct");
    // Database.trace ("first = " + first);
    // Database.trace ("second = " + second);
    // Database.trace ("");

    for (String name : first.columns) {
      String column = ((first.name == null) ? "" : first.name + ".") + name;
      columns.add (column);
    }

    for (String name : second.columns) {
      String column = ((second.name == null) ? "" : second.name + ".") + name;
      columns.add (column);
    }

    Table result = new Table (null, columns);

    for (Collection <String> firstRow : first)
      for (Collection <String> secondRow : second)
	executeRow (result, firstRow, secondRow);

    return result;
  }

  protected void executeRow (Table result, Collection <String> firstRow, Collection <String> secondRow) {
    Collection <String> row = new ArrayList <String> (firstRow);
    row.addAll (secondRow);
    result.add (row);
  }

  protected ITreeNode first, second;
}
