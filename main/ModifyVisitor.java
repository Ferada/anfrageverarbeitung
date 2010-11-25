package main;

import java.util.*;

import relationenalgebra.*;

public class ModifyVisitor {
  public Object visit (Projection x) {
    x.child = (ITreeNode) dispatch (x.child);
    return x;
  }

  public Object visit (Selection x) {
    x.expression = (IBooleanExpression) dispatch (x.expression);
    x.child = (ITreeNode) dispatch (x.child);
    return x;
  }

  public Object visit (CrossProduct x) {
    x.first = (ITreeNode) dispatch (x.first);
    x.second = (ITreeNode) dispatch (x.second);
    return x;
  }

  public Object visit (Join x) {
    x.first = (ITreeNode) dispatch (x.first);
    x.second = (ITreeNode) dispatch (x.second);
    x.expression = (IBooleanExpression) dispatch (x.expression);
    return x;
  }

  public Object visit (EqualityExpression x) {
    x.first = (PrimaryExpression) dispatch (x.first);
    x.second = (PrimaryExpression) dispatch (x.second);
    return x;
  }

  public Object visit (AndExpression x) {
    Collection <IBooleanExpression> result = new ArrayList <IBooleanExpression> ();
    for (IBooleanExpression e : x.expressions)
      result.add ((IBooleanExpression) dispatch (e));
    x.expressions = result;
    return x;
  }

  public Object visit (OrExpression x) {
    Collection <IBooleanExpression> result = new ArrayList <IBooleanExpression> ();
    for (IBooleanExpression e : x.expressions)
      result.add ((IBooleanExpression) dispatch (e));
    x.expressions = result;
    return x;
  }

  public Object visit (PrimaryExpression x) {
    return x;
  }

  public Object visit (ConstantBooleanExpression x) {
    return x;
  }

  public Object visit (Relation x) {
    return x;
  }

  public Object visit (CreateTable x) {
    return x;
  }

  public Object visit (DropTable x) {
    return x;
  }

  public Object visit (Insert x) {
    return x;
  }

  public Object visit (Delete x) {
    return x;
  }

  public Object visit (Update x) {
    return x;
  }

  public Object dispatch (Object x) {
    if (x == null)
      return x;

    Class klass = x.getClass ();
    if (klass == Projection.class)
      return visit ((Projection) x);
    else if (klass == Selection.class)
      return visit ((Selection) x);
    else if (klass == CrossProduct.class)
      return visit ((CrossProduct) x);
    else if (klass == Join.class)
      return visit ((Join) x);
    else if (klass == AndExpression.class)
      return visit ((AndExpression) x);
    else if (klass == OrExpression.class)
      return visit ((OrExpression) x);
    else if (klass == EqualityExpression.class)
      return visit ((EqualityExpression) x);
    else if (klass == PrimaryExpression.class)
      return visit ((PrimaryExpression) x);
    else if (klass == ConstantBooleanExpression.class)
      return visit ((ConstantBooleanExpression) x);
    else if (klass == Relation.class)
      return visit ((Relation) x);
    else if (klass == CreateTable.class)
      return visit ((CreateTable) x);
    else if (klass == DropTable.class)
      return visit ((DropTable) x);
    else if (klass == Insert.class)
      return visit ((Insert) x);
    else if (klass == Delete.class)
      return visit ((Delete) x);
    else if (klass == Update.class)
      return visit ((Update) x);
    else
      throw new RuntimeException ("huh, can't handle class " + klass.getName ());
  }
}
