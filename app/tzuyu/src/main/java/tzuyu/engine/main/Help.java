package tzuyu.engine.main;

import java.util.logging.Logger;

import tzuyu.engine.utils.Globals;

public class Help implements CommandHandler {

//  private static final Logger logger = Logger.getGlobal();
	private static final Logger logger = Logger.getLogger(Help.class.getSimpleName());

  @Override
  public boolean handle(Command command) {
    if (command.cmdStr.equals("help")) {
      printHelp();
    } else if (command.cmdStr.equals("version")) {
      printVersion();
    }
    return true;
  }

  private void printHelp() {
    logger.info("Typical usage of TzuYu is to type:");
    logger.info("tzuyu --" + Command.learn + " [options] -"
        + Option.target.name + " class.full.name [-" + Option.methods.name
        + " m1 m2]");
    logger.info("or");
    logger.info("tzuyu --" + Command.help + " to show this manual.");
    logger.info("tzuyu --" + Command.version + " to show the version.");
    logger.info("options are the fowllowing :");
    logger.info("-" + Option.testsPerQuery.name
        + Option.testsPerQuery.getHelp());
    logger.info("-" + Option.object2Integer.name
        + Option.object2Integer.getHelp());
    logger.info("-" + Option.output.name
        + Option.output.getHelp());
    logger.info("-" + Option.inheritedMethods.name
        + Option.inheritedMethods.getHelp());
  }

  private void printVersion() {
    logger.info(Globals.TZUYU_VERSION);
  }

  @Override
  public boolean canHandle(Command command) {
    if (command.cmdStr.equals("help") || command.cmdStr.equals("version")) {
      return true;
    }
    return false;
  }

}
