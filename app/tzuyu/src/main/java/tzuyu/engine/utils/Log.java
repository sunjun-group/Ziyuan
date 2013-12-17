package tzuyu.engine.utils;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.List;

// TODO [LLT]: do we need this? if yes, put it into reporterHandler instead!!
public final class Log {

  private Log() {
    throw new IllegalStateException("no instance");
  }

  public static final ByteArrayOutputStream bos;
  public static final PrintStream systemOutErrStream;
  public static final PrintStream err;
  public static final PrintStream out;

  static {
    bos = new ByteArrayOutputStream();
    systemOutErrStream = new PrintStream(bos);
    err = System.err;
    out = System.out;
  }

  public static void log(String s) {
    if (!isLoggingOn())
      return;

//    try {
//      Options.log.write(s);
//      Options.log.flush();
//    } catch (IOException e) {
//      e.printStackTrace();
//      System.exit(1);
//    }
  }

  public static void logLine(String s) {
    if (!isLoggingOn())
      return;

//    try {
//      Options.log.write(s);
//      Options.log.write(Globals.lineSep);
//      Options.log.flush();
//    } catch (IOException e) {
//      e.printStackTrace();
//      System.exit(1);
//    }
  }

  public static void logSequence(String s) {
    if (!isLoggingOn())
      return;

//    try {
//      Options.log.write(Globals.lineSep + Globals.lineSep);
//      Options.log.write(s);
//      Options.log.flush();
//
//    } catch (IOException e) {
//      e.printStackTrace();
//      System.exit(1);
//    }
  }

  public static void logStatements(List<String> model) {
    if (!isLoggingOn())
      return;

//    try {
//      Options.log.write("Statements : " + Globals.lineSep);
//      for (String t : model) {
//        Options.log.write(t);
//        Options.log.write(Globals.lineSep);
//        Options.log.flush();
//      }
//    } catch (IOException e) {
//      e.printStackTrace();
//      System.exit(1);
//    }
  }

  public static boolean isLoggingOn() {
//    return Options.log != null;
	  return false;
  }
}
