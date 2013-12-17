package tzuyu.engine.main;

import java.util.ArrayList;
import java.util.List;

import tzuyu.engine.TzLogger;

public class ArgParser {
  private String[] args;
  private int cursor;

  public ArgParser(String[] arguments) {
    this.args = arguments;
    cursor = 0;
  }

  public List<Command> parse() {
    if (args.length == 0) {
      args = new String[] { "--help" };
    }

    List<Command> commands = new ArrayList<Command>();
    while (true) {
      String cmd = match("--");
      if (cmd == null) {
        break;
      }
      Command command = Command.getCommand(cmd);
      if (command == null) {
        error(cmd);
      }
      parseCommand(command);
      commands.add(command);
    }

    return commands;
  }

  public void parseCommand(Command command) {
    while (true) {
      String opt = match("-");
      if (opt == null) {
        break;
      }

      Option<?> option = Option.getOption(opt);

      if (option == null) {
        error(opt);
      }
      parseOption(option);
      command.addOption(option);
    }
  }

  public Option<?> parseOption(Option<?> opt) {
    while (true) {
      String val = match("");
      if (val == null) {
        break;
      }
      opt.parseValue(val);
    }
    return null;
  }

  private String match(String head) {
    if (cursor >= args.length) {
      return null;
    }

    String current = args[cursor];
    if (current.startsWith("--") && head.equals("--")) {
      cursor++;
      return current.substring(head.length());
    }
    if (current.startsWith("-") && head.equals("-")) {
      cursor++;
      return current.substring(head.length());
    } else if (current.charAt(0) != '-' && head.equals("")) {
      cursor++;
      return current;
    } else {
      return null;
    }
  }

  private void error(String str) {
    TzLogger.log().info("option \"" + str + "\" cannot be recognized");
    System.exit(1);
  }
}
