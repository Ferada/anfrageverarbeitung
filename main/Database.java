package main;

import java.io.*;
import java.util.*;
import parser.gene.*;
import parser.syntaxtree.*;
import parser.visitor.*;
import relationenalgebra.*;

public class Database {
  public void test () throws ParseException {
    String string = "ID = B_ID";
    SimpleSQLParser parser = new SimpleSQLParser (new StringReader (string));
    parser.setDebugALL (false);
    parser.syntaxtree.AndExpression expression = parser.AndExpression ();
    SimpleSQLToRelAlgVisitor visitor = new SimpleSQLToRelAlgVisitor ();
    Object result = expression.accept (visitor, null);

    ITreeNode cross = new CrossProduct (new Relation ("Buch", null), new Relation ("Buch_Autor", null));

    Collection <ColumnName> names = new ArrayList <ColumnName> ();
    names.add (new ColumnName ("Buch_Autor", "Autorenname"));
    names.add (new ColumnName ("Buch", "Titel"));

    ITreeNode selection = new Selection ((relationenalgebra.AndExpression) result, cross);
    ITreeNode join = new Join ((relationenalgebra.AndExpression) result, new Relation ("Buch", null), new Relation ("Buch_Autor", null));

    ITreeNode projection = new Projection (names, selection);
    execute (projection);

    projection = new Projection (names, join);
    execute (projection);
  }

  public static String relative (String path1, String path2) {
    return new File (new File (path1), path2).getPath ();
  }

  public void readTables (String directory) throws IOException, ClassNotFoundException {
    String[] files = new File (directory).list ();

    if (files != null)
      for (String file : files)
	readTable (relative (directory, file));
  }

  public void writeTables (String directory) throws IOException, ClassNotFoundException {
    for (Table table : tables.values ())
      writeTable (relative (directory, table.name), table);
  }

  public void readSQLStream (InputStream stream) throws ParseException, FileNotFoundException {
    parse (new SimpleSQLParser (stream));
  }

  public void readSQLFile (String filename) throws ParseException, FileNotFoundException {
    parse (new SimpleSQLParser (new FileReader (filename)));
  }

  public void parse (SimpleSQLParser parser) throws ParseException {
    parser.setDebugALL (false);
    CompilationStatements statements = parser.CompilationStatements ();
    SimpleSQLToRelAlgVisitor visitor = new SimpleSQLToRelAlgVisitor ();
    Object result = statements.accept (visitor, null);

    if (result != null)
      for (ITreeNode node : (Collection <ITreeNode>) result)
	execute (node);
  }
  
  public void execute(ITreeNode node) {
    trace (node.toString () + ";");
    /* TODO: optimize tree */

    Table result = node.execute (this);
    if (result != null) {
      print (result.toString ());
      print ("");
    }
  }
  	
  public static void trace (String message) {
    System.err.println (message);
  }

  public static void print (String message) {
    System.out.println (message);
  }

  /** Returns the table with the given name or null if no such table
      exists. */
  public Table getTable (String name) {
    return tables.get (name);
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

  public static String databaseDirectory = "db";

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
  public static boolean calculateCosts = true;
  public static boolean printSQL = false;
}
