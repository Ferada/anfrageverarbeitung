package optimisation;

import java.util.*;

import relationenalgebra.*;

public class Visitor {
  public void visit (Projection x) {
    dispatch (x.child);
  }

  public void visit (Selection x) {
    dispatch (x.expression);
    dispatch (x.child);
  }

  public void visit (CrossProduct x) {
    dispatch (x.first);
    dispatch (x.second);
  }

  public void visit (Join x) {
    dispatch (x.first);
    dispatch (x.second);
    dispatch (x.expression);
  }

  public void visit (EqualityExpression x) {
    dispatch (x.first);
    dispatch (x.second);
  }

  public void visit (AndExpression x) {
    for (IBooleanExpression e : x.expressions)
      dispatch (e);
  }

  public void visit (OrExpression x) {
    for (IBooleanExpression e : x.expressions)
      dispatch (e);
  }

  public void visit (PrimaryExpression x) {
  }

  public void visit (ConstantBooleanExpression x) {
  }

  public void visit (Relation x) {
  }

  public void visit (CreateTable x) {
  }

  public void visit (DropTable x) {
  }

  public void visit (Insert x) {
  }

  public void visit (Delete x) {
  }

  public void visit (Update x) {
  }

  public void dispatch (Object x) {
    if (x == null)
      return;

    Class klass = x.getClass ();
    if (klass == Projection.class)
      visit ((Projection) x);
    else if (klass == Selection.class)
      visit ((Selection) x);
    else if (klass == CrossProduct.class)
      visit ((CrossProduct) x);
    else if (klass == Join.class)
      visit ((Join) x);
    else if (klass == AndExpression.class)
      visit ((AndExpression) x);
    else if (klass == OrExpression.class)
      visit ((OrExpression) x);
    else if (klass == EqualityExpression.class)
      visit ((EqualityExpression) x);
    else if (klass == PrimaryExpression.class)
      visit ((PrimaryExpression) x);
    else if (klass == ConstantBooleanExpression.class)
      visit ((ConstantBooleanExpression) x);
    else if (klass == Relation.class)
      visit ((Relation) x);
    else if (klass == CreateTable.class)
      visit ((CreateTable) x);
    else if (klass == DropTable.class)
      visit ((DropTable) x);
    else if (klass == Insert.class)
      visit ((Insert) x);
    else if (klass == Delete.class)
      visit ((Delete) x);
    else if (klass == Update.class)
      visit ((Update) x);
    else
      throw new RuntimeException ("huh, can't handle class " + klass.getName ());
  }
}
