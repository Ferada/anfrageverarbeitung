//
// Generated by JTB 1.2.2
//

package parser.syntaxtree;

/**
 * Grammar production:
 * f0 -> <SELECT>
 * f1 -> Items()
 * f2 -> <FROM>
 * f3 -> Tables()
 * f4 -> [ Where() ]
 */
public class Query implements Node {
   private Node parent;
   public NodeToken f0;
   public Items f1;
   public NodeToken f2;
   public Tables f3;
   public NodeOptional f4;

   public Query(NodeToken n0, Items n1, NodeToken n2, Tables n3, NodeOptional n4) {
      f0 = n0;
      if ( f0 != null ) f0.setParent(this);
      f1 = n1;
      if ( f1 != null ) f1.setParent(this);
      f2 = n2;
      if ( f2 != null ) f2.setParent(this);
      f3 = n3;
      if ( f3 != null ) f3.setParent(this);
      f4 = n4;
      if ( f4 != null ) f4.setParent(this);
   }

   public Query(Items n0, Tables n1, NodeOptional n2) {
      f0 = new NodeToken("select");
      if ( f0 != null ) f0.setParent(this);
      f1 = n0;
      if ( f1 != null ) f1.setParent(this);
      f2 = new NodeToken("from");
      if ( f2 != null ) f2.setParent(this);
      f3 = n1;
      if ( f3 != null ) f3.setParent(this);
      f4 = n2;
      if ( f4 != null ) f4.setParent(this);
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

