//
// Generated by JTB 1.2.2
//

package parser.syntaxtree;

/**
 * Grammar production:
 * f0 -> PrimaryExpression()
 * f1 -> [ ( "=" | "!=" | <LT> | <GT> | <LE> | <GE> ) PrimaryExpression() ]
 */
public class EqualityExpression implements Node {
   private Node parent;
   public PrimaryExpression f0;
   public NodeOptional f1;

   public EqualityExpression(PrimaryExpression n0, NodeOptional n1) {
      f0 = n0;
      if ( f0 != null ) f0.setParent(this);
      f1 = n1;
      if ( f1 != null ) f1.setParent(this);
   }

   public void accept(parser.visitor.Visitor v) {
      v.visit(this);
   }
   public Object accept(parser.visitor.ObjectVisitor v, Object argu) {
      return v.visit(this,argu);
   }
   public void setParent(Node n) { parent = n; }
   public Node getParent()       { return parent; }
}

