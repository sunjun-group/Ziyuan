package tzuyu.engine.main;

import java.util.ArrayList;
import java.util.List;

import tzuyu.engine.utils.CollectionUtils;

public class Main {

	private static List<CommandHandler> handlers;

	static {
		handlers = new ArrayList<CommandHandler>();
		handlers.add(new Help());
		handlers.add(new Learn());
	}

	public static void main(String[] args) {
		List<Command> commands = new ArgParser(args).parse();
		for (Command command : commands) {
			for (CommandHandler handler : handlers) {
				if (CollectionUtils.existInArray(command.getType(),
						handler.getCmdTypes())) {
					handler.handle(command);
				}
			}
		}
	}
}
