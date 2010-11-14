package main;

import java.util.*;

import parser.syntaxtree.*;
import parser.visitor.*;

import relationenalgebra.*;

public class CollectionVisitor <T> extends ObjectDepthFirst {
  public CollectionVisitor () {
    reset ();
  }

  public CollectionVisitor (Collection <T> collection) {
    reset (collection);
  }

  public void collect (T value) {
    collection.add (value);
  }

  public void reset () {
    this.reset (new ArrayList <T> ());
  }

  public void reset (Collection <T> collection) {
    this.collection = collection;
  }

  public Collection <T> collection;
}
