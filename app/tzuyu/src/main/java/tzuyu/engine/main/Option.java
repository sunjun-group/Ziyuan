package tzuyu.engine.main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class Option {
  
  private static final String targetHelpStr = 
      " the target class for which to learn specifications.";
  private static final String outputHelpStr = 
      " directory\t the directory to which the results are put.";
  private static final String testsPerQueryHelpStr = 
      " number\t number of test cases for each query.";
  private static final String object2IntegerHelpStr = 
      " flag\t whether to use integers as the Object type values.";
  private static final String inheritedMethodsHelpStr = 
      " flag\t whether to learn specifications for inherited methods.";
  private static final String methodsHelpStr = 
      " only learn specifications for the specified method(s).";
  
  public static final StringOption target = 
      new StringOption("target", "", targetHelpStr);
  public static final StringOption output = 
      new StringOption("out", "", outputHelpStr);
  public static final IntegerOption testsPerQuery = 
      new IntegerOption("tpq", 1, testsPerQueryHelpStr);
  public static final BooleanOption object2Integer = 
      new BooleanOption("o2i", false, object2IntegerHelpStr);
  public static final BooleanOption inheritedMethods = 
      new BooleanOption("im", false, inheritedMethodsHelpStr);
  
  public static final MultipleValueOption methods = 
      new MultipleValueOption("methods", methodsHelpStr);

  public static final Map<String, Option> options = 
      new HashMap<String, Option>();

  static {
    options.put(target.name, target);
    options.put(output.name, output);
    options.put(testsPerQuery.name, testsPerQuery);
    options.put(object2Integer.name, object2Integer);
    options.put(methods.name, methods);
    options.put(inheritedMethods.name, inheritedMethods);
  }

  public final String name;

  protected Option(String optName) {
    this.name = optName;
  }

  public static Option getOption(String optName) {
    Option option = options.get(optName);
    return option;
  }

  public abstract boolean parseValue(String val);
  
  public abstract String getHelp();
}

class IntegerOption extends Option {
  private String helpStr;
  private int val;

  public IntegerOption(String name, int defaultVal, String help) {
    super(name);
    val = defaultVal;
    helpStr = help;
  }

  public int getValue() {
    return val;
  }

  @Override
  public boolean parseValue(String val) {
    this.val = Integer.valueOf(val);
    return true;
  }

  @Override
  public String getHelp() {
    return helpStr;
  }
}

class StringOption extends Option {
  private String val;
  private String helpStr;
  
  public StringOption(String name, String defaultVal, String help) {
    super(name);
    this.val = defaultVal;
    this.helpStr = help;
  }

  @Override
  public boolean parseValue(String val) {
    this.val = val;
    return true;
  }

  public String getValue() {
    return val;
  }

  @Override
  public String getHelp() {
    return helpStr;
  }
}

class BooleanOption extends Option {
  private boolean val;
  private String helpStr;

  public BooleanOption(String name, boolean defaultVal, String help) {
    super(name);
    val = defaultVal;
    helpStr = help;
  }

  @Override
  public boolean parseValue(String val) {
    this.val = Boolean.valueOf(val);
    return true;
  }

  public boolean getValue() {
    return val;
  }

  @Override
  public String getHelp() {
    return helpStr;
  }
}

class MultipleValueOption extends Option {
  private List<String> vals;

  private String helpStr;
  
  public MultipleValueOption(String name, String help) {
    super(name);
    vals = new ArrayList<String>();
    helpStr = help;
  }

  @Override
  public boolean parseValue(String val) {
    vals.add(val);
    return true;
  }

  public List<String> getValue() {
    return vals;
  }

  @Override
  public String getHelp() {
    return helpStr;
  }
}
