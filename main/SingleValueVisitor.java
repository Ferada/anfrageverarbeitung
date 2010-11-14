package main;

import java.util.*;

import parser.syntaxtree.*;
import parser.visitor.*;

import relationenalgebra.*;

public class SingleValueVisitor <T> extends ObjectDepthFirst {
  public SingleValueVisitor <T> reset () {
    value = null;
    return this;
  }

  public T value;
}
