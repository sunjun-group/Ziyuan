package tzuyu.engine.utils;

import java.io.File;
import java.io.FileWriter;

public class Options {

  /**
   * The number of different test cases should we generate for each query
   */
  private static int traces_per_query = 1;

  public static final int tracesPerQuery() {
    return traces_per_query;
  }

  private static int string_max_len = 10;

  public static final int stringMaxLen() {
    return string_max_len;
  }

  private static boolean long_format = true;

  public static final boolean longFormat() {
    return long_format;
  }

  private static boolean always_use_ints_as_objects = true;

  public static final boolean alwaysUseIntsAsObjects() {
    return always_use_ints_as_objects;
  }

  private static File outputDir = new File(Globals.userDir);

  public static final File outputDir() {
    return outputDir;
  }

  public static final String getAbsoluteAddress(String filename) {
    return outputDir.getAbsolutePath() + Globals.fileSep + filename;
  }

  /**
   * The maximum class definition depth used for static analysis and
   * instrumentation.
   */
  private static int class_max_depth = 5;

  public static final int classMaxDepth() {
    return class_max_depth;
  }

  /**
   * The maximum number of elements in an array when cloning an object. The
   * array field in a target object may be too long to clone (results in out of
   * memory problem, so we only want to clone the maximum number of elements to
   * eradicate the out of memory problem.
   */
  private static int array_max_length = 5;

  public static final int arrayMaxLength() {
    return array_max_length;
  }

  public static void setOptions(boolean o2i, int tpq, File output, boolean im) {
    always_use_ints_as_objects = o2i;
    traces_per_query = tpq;
    if (output != null) {
      outputDir = output;
    }
    inheritedMethods = im;
  }

  private static boolean forbid_null = true;

  public static boolean forbidNull() {
    return forbid_null;
  }

  private static boolean debug_checks = false;

  public static boolean debugChecks() {
    return debug_checks;
  }

  public static FileWriter log = null;

  public static boolean prettyPrint() {
    return true;
  }
  
  private static boolean inheritedMethods = false;
  
  public static boolean useInheritedMethods() {
    return inheritedMethods;
  }
}
