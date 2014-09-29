package tzuyu.engine.main;

import static tzuyu.engine.main.Option.OptionType.INHERITED_METHOD;
import static tzuyu.engine.main.Option.OptionType.METHODS;
import static tzuyu.engine.main.Option.OptionType.OBJECT_TO_INTEGER;
import static tzuyu.engine.main.Option.OptionType.OUTPUT;
import static tzuyu.engine.main.Option.OptionType.TARGET;
import static tzuyu.engine.main.Option.OptionType.TEST_PERS_QUERY;
import sav.common.core.CommandLineLogger;
import tzuyu.engine.main.Command.CommandType;
import tzuyu.engine.utils.Globals;

public class Help implements CommandHandler {

	public boolean handle(Command command) {
		if (command.getType() == CommandType.help) {
			printHelp();
		} else if (command.getType() == CommandType.version) {
			printVersion();
		}
		return true;
	}

	private void printHelp() {
		CommandLineLogger.instance()
				.info("Typical usage of TzuYu is to type:")
				.info("tzuyu --" + CommandType.learn.name()
						+ " [options] -" + TARGET.getOpt()
						+ " class.full.name [-" + METHODS.getOpt() + " m1 m2]")
				.info("or")
				.info("tzuyu --" + CommandType.help.name()
						+ " to show this manual.")
				.info("tzuyu --" + CommandType.version.name()
						+ " to show the version.")
				.info("options are the fowllowing :")
				.info("-" + TEST_PERS_QUERY.getOpt()
						+ Option.testsPerQuery.getHelp())
				.info("-" + OBJECT_TO_INTEGER.getOpt()
						+ Option.object2Integer.getHelp())
				.info("-" + OUTPUT.getOpt() + Option.output.getHelp())
				.info("-" + INHERITED_METHOD.getOpt()
						+ Option.inheritedMethods.getHelp());
	}

	private void printVersion() {
		CommandLineLogger.instance().info(Globals.TZUYU_VERSION);
	}

	public CommandType[] getCmdTypes() {
		return new CommandType[] { CommandType.help, CommandType.version };
	}

}
