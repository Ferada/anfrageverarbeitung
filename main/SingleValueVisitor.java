package main;

import java.util.*;

import parser.syntaxtree.*;
import parser.visitor.*;

import relationenalgebra.*;

/** Used collect single values some levels below the current one.  Only
    useful if extended to actually visit nodes, therefore declared abstract. */
public abstract class SingleValueVisitor <T> extends ObjectDepthFirst {
  public SingleValueVisitor <T> reset () {
    value = null;
    return this;
  }

  public T value;
}
