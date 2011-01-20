package main;

import java.io.*;
import java.util.*;

import parser.gene.*;
import parser.syntaxtree.*;
import parser.visitor.*;

import relationenalgebra.*;
import optimisation.*;

/** Main class, managing individual tables, parsing, serialisation and
    runtime options. */
public class Database {
  public static String relative (String path1, String path2) {
    return new File (new File (path1), path2).getPath ();
  }

  public void ensureDirectory (String directory) throws IOException, SecurityException {
    File file = new File (directory);

    if (file.exists ()) {
      if (!file.canRead ())
	throw new IOException ("can't read from directory \"" + file + "\"");
      if (!file.canWrite ())
	throw new IOException ("can't write to directory \"" + file + "\"");
    }
    else if (!file.mkdir ())
      throw new IOException ("creation of directory \"" + file + "\" failed");
  }

  public void readTables (String directory) throws IOException, ClassNotFoundException {
    String[] files = new File (directory).list ();

    if (files != null)
      for (String file : files)
	readTable (relative (directory, file));

    trace ("read " + files.length + plural (" table", files.length));
  }

  public void writeTables (String directory) throws IOException, ClassNotFoundException {
    for (Table table : tables.values ())
      if (table != null)
	writeTable (relative (directory, table.name), table);

    trace ("wrote " + tables.size () + plural (" table", tables.size ()));
  }

  public static String plural (String string, int i) {
    return (i == 1) ? string : (string + "s");
  }

  public Collection <ITreeNode> readSQLStream (InputStream stream) throws ParseException, FileNotFoundException {
    return parse (new SimpleSQLParser (stream));
  }

  public Collection <ITreeNode> readSQLFile (String filename) throws ParseException, FileNotFoundException {
    return parse (new SimpleSQLParser (new FileReader (filename)));
  }

  /** Parses simple SQL statements using a parser and executes them. */
  public Collection <ITreeNode> parse (SimpleSQLParser parser) throws ParseException {
    parser.setDebugALL (false);
    CompilationStatements statements = parser.CompilationStatements ();
    SimpleSQLToRelAlgVisitor visitor = new SimpleSQLToRelAlgVisitor ();
    return (Collection <ITreeNode>) statements.accept (visitor, null);
  }

  public void execute (Collection <ITreeNode> nodes) {
    if (nodes != null)
      for (ITreeNode node : nodes)
    	execute (node);
  }

  /** Executes a single expression, printing results, if available. */
  public void execute (ITreeNode node) {
    ITreeNode optimised = optimise (node);

    Table result = scheduler.execute (optimised);
    if (result != null) {
      print (result.toString ());
      print ("");
    }
  }

  public Thread prepareThread (Collection <ITreeNode> nodes) {
    if (nodes == null) return null;
    return new ClientThread (this, nodes);
  }

  public ITreeNode optimise (ITreeNode node) {
    Optimisations optimisations = new Optimisations (this);

    return optimisations.optimise (optimisationLevel, node);
  }

  /** Prints debug messages to standard error. */
  public static synchronized void trace (Object message) {
    if (verbose)
      System.err.println ("" + Thread.currentThread () + ": " + message);
  }

  public static synchronized void traceDot (Object message) {
    if (verbose) {
      DotPrinter printer = new DotPrinter (System.err);
      printer.print (message);
    }
  }

  public static synchronized void traceExpression (Object message) {
    trace ("" + message + (printSQL ? ";" : ""));
    if (printDot) traceDot (message);
  }

  /** Prints normal output to standard output. */
  public static synchronized void print (Object message) {
    System.out.println ("" + message);
  }

  /** Returns the table with the given name or null if no such table
      exists. */
  public Table getTable (String name) {
    return Transaction.current.get ().getTable (name);
  }

  public void add (Table table) {
    Transaction.current.get ().addTable (table);
  }

  public void remove (Table table) {
    Transaction.current.get ().removeTable (table);
  }

  public Database () {
    tables = new HashMap <String, Table> ();
    scheduler = new Scheduler (this);
  }

  public void readTable (String filename) throws IOException, ClassNotFoundException {
    FileInputStream file = new FileInputStream (filename);
    ObjectInputStream stream = new ObjectInputStream (file);
    Table table = (Table) stream.readObject ();
    tables.put (table.name, table);
    stream.close ();
    file.close ();
  }

  public void writeTable (String filename, Table table) throws IOException, ClassNotFoundException {
    FileOutputStream file = new FileOutputStream (filename);
    ObjectOutputStream stream = new ObjectOutputStream (file);
    stream.writeObject (table);
    stream.close ();
    file.close ();
  }

  public Map <String, Table> tables;
  protected Scheduler scheduler;

  /** Print expressions using SQL syntax.  Will probably not work with
      optimized expressions, so it's disabled by default. */
  public static boolean printSQL;
  public static boolean printDot;
  /** Print more information, that is, enable the trace method. */
  public static boolean verbose;
  /* TODO: add a flag to print only the few first and last rows if > 100? rows */
  public static String databaseDirectory;

  public static int optimisationLevel;
}
