package main;

import java.io.*;
import java.util.*;

import joptsimple.*;

import parser.gene.*;

import static java.util.Arrays.*;

/** Program entry point, extracted into own class to aid readability.
    Also manages command-line options. */
public class Main {
  public static void main(String[] args) throws ParseException,
    FileNotFoundException, IOException, ClassNotFoundException
  {
    Database database = new Database ();

    OptionParser parser = parser (args);

    /* no, of course System.exit will some day return. fu. */
    OptionSet options = null;
    try {
      options = parser.parse (args);
    }
    catch (OptionException exception) {
      System.out.println ("An error occured: " + exception + "\n");
      help (parser);
      System.exit (-1);
    }

    if (options.has ("?")) {
      help (parser);
      return;
    }

    applyOptions (database, options);

    if (!options.has ("noread")) {
      database.ensureDirectory (database.databaseDirectory);
      database.readTables (database.databaseDirectory);
    }

    Collection <String> nonOptions = options.nonOptionArguments ();

    /* if no filenames are given, read from standard input */
    if (nonOptions.size () == 0)
      database.readSQLStream (System.in);
    else {
      /* else read every file and perhaps read from standard input afterwards */
      for (String filename : nonOptions)
    	database.readSQLFile (filename);

      if (options.has ("c"))
	database.readSQLStream (System.in);
    }

    // database.test ();

    if (!options.has ("nowrite")) {
      database.ensureDirectory (database.databaseDirectory);
      database.writeTables (database.databaseDirectory);
    }
  }

  public static OptionParser parser (String[] args) {
    return new OptionParser () {
      {
	acceptsAll (asList ("?", "h", "help"), "display this help");
	acceptsAll (asList ("v", "verbose"), "be more verbose");

	acceptsAll (asList ("c", "stdin"), "always read input from stdin");

	accepts ("sql", "use SQL (like) syntax");
	// accepts ("sexp", "use full S-expression syntax (default)");
	// accepts ("costs", "calculate and print operation costs");

	accepts ("dot", "print expression trees in dot syntax");

	accepts ("storage", "directory for database files")
	  .withRequiredArg ().defaultsTo ("database");
	accepts ("noread", "don't read database files");
	accepts ("nowrite", "don't write database files");

	acceptsAll (asList ("o", "optimisations"), "optimisation level")
	  .withRequiredArg ().ofType (Integer.class).defaultsTo (7);
      }
    };
  }

  public static void applyOptions (Database database, OptionSet options) {
    database.verbose = options.has ("verbose");

    database.printSQL = options.has ("sql");
    database.printDot = options.has ("dot");

    database.databaseDirectory = (String) options.valueOf ("storage");

    database.optimisationLevel = ((Integer) options.valueOf ("optimisations")).intValue ();
  }

  public static void help (OptionParser parser) throws IOException {
    System.out.println ("Usage: main.Main [options] [file]*\n" +
			"If no files are given, input is read from standard input.  The program\n" +
			"reads simple SQL statements, terminated by semicolons.  By default, the\n" +
			"serialized database tables are read from the database directory before\n" +
			"execution of statements starts and are written back after all statements\n" +
			"were run successfully, that is, no exception was thrown during the\n" +
			"process.\n" +
			"Optimisation levels 1 to 4 are basic required optimisations, everything\n" +
			"above up to 7 are optional optimisations.\n");
    parser.printHelpOn (System.out);
  }
}
