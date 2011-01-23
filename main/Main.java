package main;

import java.io.*;
import java.util.*;

import joptsimple.*;

import org.apache.log4j.*;
import org.apache.log4j.xml.*;

import parser.gene.*;

import relationenalgebra.*;

import static java.util.Arrays.*;

/** Program entry point, extracted into own class to aid readability.
    Also manages command-line options. */
public class Main {
  public static void main(String[] args) throws ParseException,
    FileNotFoundException, IOException, ClassNotFoundException
  {
    DOMConfigurator.configure ("default.xml");

    Database database = new Database ();

    OptionParser parser = parser (args);

    /* no, of course System.exit will some day return. fu. */
    OptionSet options = null;
    try {
      options = parser.parse (args);
    }
    catch (OptionException exception) {
      log.fatal ("An error occured: " + exception + "\n");
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
    if (nonOptions.size () == 0) {
      Thread thread = prepareThread (database, "System.in", database.readSQLStream (System.in));
      if (thread != null) {
	thread.start ();
	try {
	  thread.join ();
	}
	catch (Exception exception) {
	  log.error ("caught exception: " + exception);
	}
      }
    }
    else {
      Collection <Thread> threads = new ArrayList <Thread> ();

      /* else read every file and perhaps read from standard input afterwards */
      for (String filename : nonOptions) {
	Thread thread = prepareThread (database, filename, database.readSQLFile (filename));
	if (thread != null)
	  threads.add (thread);
      }

      for (Thread thread : threads)
	thread.start ();

      if (options.has ("c")) {
	Thread thread = prepareThread (database, "System.in", database.readSQLStream (System.in));
	threads.add (thread);
	thread.start ();
      }

      boolean done = false;
      while (!done) {
      	done = true;
      	for (Thread thread : threads)
      	  if (thread.isAlive ())
      	    done = false;

      	if (!done)
      	  database.scheduler.join ();
      }
    }

    if (!options.has ("nowrite")) {
      database.ensureDirectory (database.databaseDirectory);
      database.writeTables (database.databaseDirectory);
    }
  }

  private static Thread prepareThread (Database database, String name, Collection <ITreeNode> nodes) {
    Thread thread = database.prepareThread (nodes);
    if (thread != null)
      thread.setName (name);
    return thread;
  }

  public static OptionParser parser (String[] args) {
    return new OptionParser () {
      {
	acceptsAll (asList ("?", "h", "help"), "display this help");
	acceptsAll (asList ("v", "verbose"), "be more verbose")
	  .withOptionalArg ().ofType (String.class).defaultsTo ("debug");

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
    if (options.has ("verbose")) {
      String verbose = (String) options.valueOf ("verbose");
      if ("debug".equals (verbose)) {
	DOMConfigurator.configure ("debug.xml");
      }
      else if ("trace".equals (verbose)) {
	DOMConfigurator.configure ("debug.xml");
	DOMConfigurator.configure ("trace.xml");
      }
    }

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

  private static Logger log = Logger.getLogger (Main.class);
}
