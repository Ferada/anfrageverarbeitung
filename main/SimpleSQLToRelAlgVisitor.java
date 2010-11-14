package main;

import java.util.*;

import parser.syntaxtree.*;
import parser.visitor.*;

import relationenalgebra.*;

public class SimpleSQLToRelAlgVisitor extends ObjectDepthFirst {
  private final SingleValueVisitor <String> valueVisitor;

  public SimpleSQLToRelAlgVisitor () {
    valueVisitor = new SingleValueVisitor <String> () {
      public Object visit (NodeToken token, Object object) {
	this.value = token.tokenImage;
	return null;
      }
    };
  }

  private <T> Collection <T> defaultCollection () {
    return new ArrayList <T> ();
  }

  public Collection <Object> visit (Enumeration enumeration, Object object) {
    Collection <Object> result = defaultCollection ();
    while (enumeration.hasMoreElements ())
      result.add (((Node) enumeration.nextElement ()).accept(this, object));
    return result;
  }

  public Object visit(NodeList list, Object object) {
    return visit (list.elements (), object);
  }

  public Object visit(NodeListOptional list, Object object) {
    if (list.present ())
      return visit (list.elements (), object);
    else return null;
  }

  public Object visit(NodeOptional optional, Object object) {
    if (optional.present ())
      return optional.node.accept (this, object);
    else return null;
  }

  public Object visit(NodeSequence sequence, Object object) {
    return visit (sequence.elements (), object);
  }

  public Object visit(CompilationStatements statements, Object object) {
    return statements.f0.accept (this, object);
  }

  public Object visit(CompilationStatement statement, Object object) {
    return statement.f0.accept (this, object);
  }

  public Object visit(CompilationUnit unit, Object object) {
    return unit.f0.accept(this, object);
  }

  public Object visit (Query query, Object object) {
    trace ("query");

    CollectionVisitor <String> columnVisitor = new CollectionVisitor <String> () {
      public Object visit (Item item, Object object) {
	item.f0.accept (valueVisitor.reset (), null);
	String name = valueVisitor.value;

	item.f1.accept (valueVisitor.reset (), null);
	String postfix = valueVisitor.value;

	if (postfix == null)
	  collect (name);
	else
	  collect (name + "." + postfix);

	return null;
      }
    };

    query.f1.accept (columnVisitor, null);

    CollectionVisitor <Relation> relationVisitor = new CollectionVisitor <Relation> () {
      public Object visit (parser.syntaxtree.Table table, Object object) {
	collect ((relationenalgebra.Relation) table.accept ((ObjectVisitor) object, null));
	return null;
      }
    };

    query.f3.accept (relationVisitor, this);

    Object crosses = null;
    boolean first = true;
    Collection <Relation> relations = relationVisitor.collection;

    /* TODO: reverse this beforehand, so it more resembles the SQL syntax */
    for (Relation relation : relations) {
      if (first) {
	crosses = relation;
	first = false;
      }
      else {
	crosses = new CrossProduct (relation, (ITreeNode) crosses);
      }
    }

    relationenalgebra.AndExpression andExpression = (relationenalgebra.AndExpression) query.f4.accept (this, null);

    Selection selection = new Selection (andExpression, (ITreeNode) crosses);
    Projection projection = new Projection (columnVisitor.collection, selection);

    return projection;
  }

  public Object visit (parser.syntaxtree.Update update, Object object) {
    trace ("update");

    update.f1.accept (valueVisitor.reset (), object);
    String name = valueVisitor.value;

    final Map <String, String> map = new HashMap <String, String> ();
    ObjectDepthFirst visitor = new ObjectDepthFirst () {
      public Object visit (AssignExpression expression, Object object) {
	expression.f0.accept (valueVisitor.reset (), object);
	String name = valueVisitor.value;
	
	expression.f2.accept (valueVisitor.reset (), object);
	String value = valueVisitor.value;
	
	map.put (name, value);
	return null;
      }
    };

    update.f3.accept (visitor, null);
    relationenalgebra.AndExpression andExpression = (relationenalgebra.AndExpression) update.f4.accept (this, object);

    return new relationenalgebra.Update (map, andExpression, name);
  }

  public Object visit (parser.syntaxtree.Insert insert, Object object) {
    trace ("insert");

    insert.f2.accept (valueVisitor.reset (), object);
    String name = valueVisitor.value;

    CollectionVisitor <String> columnVisitor = new CollectionVisitor <String> () {
      public Object visit (Name name, Object object) {
	name.accept (valueVisitor.reset (), object);
	collect (valueVisitor.value);
	return null;
      }
    };

    CollectionVisitor <String> valuesVisitor = new CollectionVisitor <String> () {
      public Object visit (LiteralExpression expression, Object object) {
	expression.f0.accept (valueVisitor.reset (), object);
	String value = valueVisitor.value;
	collect (value.substring (1, value.length () - 1));
	return null;
      }
    };

    insert.f3.accept (columnVisitor, null);
    Collection <String> columns = (columnVisitor.collection.size () > 0) ? columnVisitor.collection : null;

    insert.f6.accept (valuesVisitor, null);
    Collection <String> values = valuesVisitor.collection;

    return new relationenalgebra.Insert (values, columns, name);
  }

  public Object visit (parser.syntaxtree.Delete delete, Object object) {
    trace ("delete");

    delete.f2.accept (valueVisitor.reset (), object);
    String name = valueVisitor.value;

    relationenalgebra.AndExpression andExpression = (relationenalgebra.AndExpression) delete.f3.accept (this, object);

    return new relationenalgebra.Delete (andExpression, name);
  }

  public Object visit (ColumnDefinition column, Object object) {
    column.f0.accept (valueVisitor.reset (), object);
    return valueVisitor.value;
  }

  public Object visit (parser.syntaxtree.CreateTable create, Object object) {
    trace ("createtable");

    create.f2.accept (valueVisitor.reset (), object);
    String name = valueVisitor.value;

    CollectionVisitor <String> visitor = new CollectionVisitor <String> () {
      public Object visit (ColumnDefinition column, Object object) {
	collect ((String) column.accept ((ObjectVisitor) object, null));
	return null;
      }
    };

    Object first = create.f4.accept (this, object);
    visitor.collect ((String) first);

    create.f5.accept (visitor, this);

    return new relationenalgebra.CreateTable (visitor.collection, name);
  }

  public Object visit (parser.syntaxtree.DropTable drop, Object object) {
    trace ("droptable");

    drop.f1.accept (valueVisitor.reset (), object);
    String name = valueVisitor.value;

    return new relationenalgebra.DropTable (name);
  }

  public Object visit (parser.syntaxtree.Table table, Object object) {
    table.f0.accept (valueVisitor.reset (), object);
    String name = valueVisitor.value;
    
    table.f1.accept (valueVisitor.reset (), object);
    String postfix = valueVisitor.value;

    return new Relation (name, postfix);
  }

  public Object visit (Where where, Object object) {
    return where.f1.accept (this, object);
  }

  public Object visit (parser.syntaxtree.AndExpression expression, Object object) {
    trace ("and expression");

    CollectionVisitor <relationenalgebra.OrExpression> visitor =
      new CollectionVisitor <relationenalgebra.OrExpression> () {
      public Object visit (parser.syntaxtree.OrExpression expression, Object object) {
	collect ((relationenalgebra.OrExpression) expression.accept ((ObjectVisitor) object, null));
	return null;
      }
    };

    Object first = expression.f0.accept (this, object);
    visitor.collect ((relationenalgebra.OrExpression) first);

    expression.f1.accept (visitor, this);

    Object result = new relationenalgebra.AndExpression (visitor.collection);
    return result;
  }

  public Object visit (parser.syntaxtree.OrExpression expression, Object object) {
    trace ("or expression");

    CollectionVisitor <relationenalgebra.EqualityExpression> visitor =
      new CollectionVisitor <relationenalgebra.EqualityExpression> () {
      public Object visit (parser.syntaxtree.EqualityExpression expression, Object object) {
	collect ((relationenalgebra.EqualityExpression) expression.accept ((ObjectVisitor) object, null));
	return null;
      }
    };

    Object first = expression.f1.accept (this, object);
    visitor.collect ((relationenalgebra.EqualityExpression) first);
    
    expression.f2.accept (visitor, this);

    Object result = new relationenalgebra.OrExpression (visitor.collection);
    return result;
  }

  public Object visit (parser.syntaxtree.EqualityExpression expression, Object object) {
    trace ("equality expression");

    relationenalgebra.EqualityExpression result;

    if (!expression.f1.present ()) {
      Object value = expression.f0.accept (this, null);
      result = new relationenalgebra.EqualityExpression ((relationenalgebra.PrimaryExpression) value);
    }
    else {
      Object first = expression.f0.accept (this, null);

      NodeSequence sequence = (NodeSequence) ((NodeOptional) expression.f1).node;
      assert (sequence.size () == 2);

      int which = ((NodeChoice) sequence.elementAt (0)).which;
      Object second = sequence.elementAt (1).accept (this, null);
      Operator operator = Operator.getOperator (which);

      result = new relationenalgebra.EqualityExpression ((relationenalgebra.PrimaryExpression) first,
							 (relationenalgebra.PrimaryExpression) second,
							 operator);
    }

    return result;
  }

  public Object visit (parser.syntaxtree.PrimaryExpression expression, Object object) {
    trace ("primary expression");

    SingleValueVisitor <relationenalgebra.PrimaryExpression> visitor =
      new SingleValueVisitor <relationenalgebra.PrimaryExpression> () {
      public Object visit (LiteralExpression expression, Object object) {
	expression.f0.accept (valueVisitor.reset (), null);
	String value = valueVisitor.value;
	this.value = new relationenalgebra.PrimaryExpression (value.substring (1, value.length () - 1));
	return null;
      }

      public Object visit (NodeSequence sequence, Object object) {
	assert (sequence.size () == 2);

	sequence.elementAt (0).accept (valueVisitor.reset (), null);
	String first = valueVisitor.value;
	sequence.elementAt (1).accept (valueVisitor.reset (), null);
	String second = valueVisitor.value;

	if (second == null)
	  value = new relationenalgebra.PrimaryExpression (null, first);
	else
	  value = new relationenalgebra.PrimaryExpression (first, second);

	return null;
      }
    };

    expression.f0.accept (visitor, null);

    return visitor.value;
  }

  public static void trace (String message) {
    // System.err.println (message);
  }
}
