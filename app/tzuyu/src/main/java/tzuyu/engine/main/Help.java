package tzuyu.engine.main;

import static tzuyu.engine.main.Option.OptionType.INHERITED_METHOD;
import static tzuyu.engine.main.Option.OptionType.METHODS;
import static tzuyu.engine.main.Option.OptionType.OBJECT_TO_INTEGER;
import static tzuyu.engine.main.Option.OptionType.OUTPUT;
import static tzuyu.engine.main.Option.OptionType.TARGET;
import static tzuyu.engine.main.Option.OptionType.TEST_PERS_QUERY;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tzuyu.engine.main.Command.CommandType;
import tzuyu.engine.utils.Globals;

public class Help implements CommandHandler {
	private static Logger log = LoggerFactory.getLogger(Help.class);
	public boolean handle(Command command) {
		if (command.getType() == CommandType.help) {
			printHelp();
		} else if (command.getType() == CommandType.version) {
			printVersion();
		}
		return true;
	}

	private void printHelp() {
		log.info("Typical usage of TzuYu is to type:");
				log.info("tzuyu --" + CommandType.learn.name()
						+ " [options] -" + TARGET.getOpt()
						+ " class.full.name [-" + METHODS.getOpt() + " m1 m2]");
				log.info("or");
				log.info("tzuyu --" + CommandType.help.name()
						+ " to show this manual.");
				log.info("tzuyu --" + CommandType.version.name()
						+ " to show the version.");
				log.info("options are the fowllowing :");
				log.info("-" + TEST_PERS_QUERY.getOpt()
						+ Option.testsPerQuery.getHelp());
				log.info("-" + OBJECT_TO_INTEGER.getOpt()
						+ Option.object2Integer.getHelp());
				log.info("-" + OUTPUT.getOpt() + Option.output.getHelp());
				log.info("-" + INHERITED_METHOD.getOpt()
						+ Option.inheritedMethods.getHelp());
	}

	private void printVersion() {
		log.info(Globals.TZUYU_VERSION);
	}

	public CommandType[] getCmdTypes() {
		return new CommandType[] { CommandType.help, CommandType.version };
	}

}
