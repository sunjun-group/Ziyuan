package tzuyu.engine.main;

import java.util.ArrayList;
import java.util.List;

public class Main {

  private static List<CommandHandler> handlers;

  static {
    handlers = new ArrayList<CommandHandler>();
    handlers.add(new Help());
    handlers.add(new Learn());
  }

  /**
   * @param args
   */
  public static void main(String[] args) {

    List<Command> commands = new ArgParser(args).parse();
    for (Command command : commands) {
      for (CommandHandler handler : handlers) {
        if (handler.canHandle(command)) {
          handler.handle(command);
        }
      }
    }
  }

}
