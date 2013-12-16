package tzuyu.engine.main;

import tzuyu.engine.TzLogger;
import tzuyu.engine.main.Command.CommandType;
import tzuyu.engine.utils.Globals;
import static tzuyu.engine.main.Option.OptionType.*;

public class Help implements CommandHandler {

	@Override
	public boolean handle(Command command) {
		if (command.getType() == CommandType.help) {
			printHelp();
		} else if (command.getType() == CommandType.version) {
			printVersion();
		}
		return true;
	}

	private void printHelp() {
		TzLogger.log()
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
		TzLogger.log().info(Globals.TZUYU_VERSION);
	}

	@Override
	public CommandType[] getCmdTypes() {
		return new CommandType[] { CommandType.help, CommandType.version };
	}

}
