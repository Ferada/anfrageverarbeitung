package main;

import java.io.*;
import java.util.*;

import parser.gene.*;
import parser.syntaxtree.*;
import parser.visitor.*;

import relationenalgebra.*;

/** Main class, managing individual tables, parsing, serialisation and
    runtime options. */
public class Database {
  /** Test for the Join operation (which isn't directly translated from
      simple SQL syntax. */
  public void test () throws ParseException {
    String string = "ID = B_ID";
    SimpleSQLParser parser = new SimpleSQLParser (new StringReader (string));
    parser.setDebugALL (false);
    parser.syntaxtree.AndExpression expression = parser.AndExpression ();
    SimpleSQLToRelAlgVisitor visitor = new SimpleSQLToRelAlgVisitor ();
    Object result = expression.accept (visitor, null);

    ITreeNode cross = new CrossProduct (new Relation ("Buch", null),
					new Relation ("Buch_Autor", null));

    Collection <ColumnName> names = new ArrayList <ColumnName> ();
    names.add (new ColumnName ("Buch_Autor", "Autorenname"));
    names.add (new ColumnName ("Buch", "Titel"));

    ITreeNode selection = new Selection ((relationenalgebra.AndExpression) result, cross);
    ITreeNode join = new Join ((relationenalgebra.AndExpression) result,
			       new Relation ("Buch", null),
			       new Relation ("Buch_Autor", null));

    ITreeNode projection = new Projection (names, selection);
    execute (projection);

    projection = new Projection (names, join);
    execute (projection);
  }

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
      writeTable (relative (directory, table.name), table);

    trace ("wrote " + tables.size () + plural (" table", tables.size ()));
  }

  public static String plural (String string, int i) {
    return (i == 1) ? string : (string + "s");
  }

  public void readSQLStream (InputStream stream) throws ParseException, FileNotFoundException {
    parse (new SimpleSQLParser (stream));
  }

  public void readSQLFile (String filename) throws ParseException, FileNotFoundException {
    parse (new SimpleSQLParser (new FileReader (filename)));
  }

  /** Parses simple SQL statements using a parser and executes them. */
  public void parse (SimpleSQLParser parser) throws ParseException {
    parser.setDebugALL (false);
    CompilationStatements statements = parser.CompilationStatements ();
    SimpleSQLToRelAlgVisitor visitor = new SimpleSQLToRelAlgVisitor ();
    Collection <ITreeNode> result = (Collection <ITreeNode>) statements.accept (visitor, null);

    if (result != null)
      for (ITreeNode node : result)
	execute (node);
  }

  /** Executes a single expression, printing results, if available. */
  public void execute(ITreeNode node) {
    traceExpression (node.toString ());
    ITreeNode optimized = optimize (node);
    traceExpression (optimized.toString ());

    AbstractTable result = optimized.execute (this);
    if (result != null) {
      Table manifested = result.manifest ();
      print (manifested.toString ());
      print ("");
    }
  }

  public ITreeNode optimize (ITreeNode node) {
    Visitor compact = new CompactVisitor ();
    Visitor tautology = new TautologyVisitor ();
    Visitor join = new JoinVisitor ();

    Object result = node;
    result = join.dispatch (result);
    result = tautology.dispatch (result);
    result = compact.dispatch (result);
    return (ITreeNode) result;
  }

  /** Prints debug messages to standard error. */
  public static void trace (String message) {
    if (verbose)
      System.err.println (message);
  }

  public static void traceExpression (String message) {
    trace (message + (printSQL ? ";" : ""));
  }

  /** Prints normal output to standard output. */
  public static void print (String message) {
    System.out.println (message);
  }

  /** Returns the table with the given name or null if no such table
      exists. */
  public Table getTable (String name) {
    Table result = tables.get (name);
    /* give a meaningful error at least */
    if (result == null)
      throw new NullPointerException ("no table of name \"" + name + "\" exists");
    return result;
  }

  public void add (Table table) {
    if (tables.containsKey (table.name))
      trace ("overwriting table " + table.name + " with new table");
    tables.put (table.name, table);
  }

  public void remove (Table table) {
    if (tables.remove (table.name) == null)
      trace ("couldn't remove table " + table.name + ", not in database");
  }

  public Database () {
    tables = new HashMap <String, Table> ();
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

  protected Map <String, Table> tables;
  /** Print expressions using SQL syntax.  Will probably not work with
      optimized expressions, so it's disabled by default. */
  public static boolean printSQL;
  /** Print more information, that is, enable the trace method. */
  public static boolean verbose;
  /* TODO: add a flag to print only the few first and last rows if > 100? rows */
  public static String databaseDirectory;
}
