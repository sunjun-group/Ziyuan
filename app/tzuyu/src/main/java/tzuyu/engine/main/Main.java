package tzuyu.engine.main;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.log4j.PropertyConfigurator;

public class Main {

  private static List<CommandHandler> handlers;

  static {
    handlers = new ArrayList<CommandHandler>();
    handlers.add(new Help());
    handlers.add(new Learn());
    // Initialize the properties for log4j
    String log4JPropertyFile = "lib/log4j.properties";
    Properties p = new Properties();

    try {
      p.load(new FileInputStream(log4JPropertyFile));
      PropertyConfigurator.configure(p);
    } catch (IOException e) {
      System.out.println("log4j properties file not found");

    }
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
