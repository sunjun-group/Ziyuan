package tzuyu.engine.main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Command {

  public static final String learn = "learn";
  public static final String help = "help";
  public static final String version = "version";

  public final static Map<String, Command> commands = 
      new HashMap<String, Command>();

  static {
    commands.put(learn, new Command(learn));
    commands.put(help, new Command(help));
    commands.put(version, new Command(version));
  }

  public final String cmdStr;
  public final List<Option> options;

  private Command(String cmd) {
    this.cmdStr = cmd;
    this.options = new ArrayList<Option>();
  }

  public void addOption(Option option) {
    this.options.add(option);
  }

  public static Command getCommand(String cmdStr) {
    Command cmd = commands.get(cmdStr);
    return cmd;
  }
}
