package tzuyu.engine.main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Command {

	public final static Map<CommandType, Command> commands = new HashMap<CommandType, Command>();

	static {
		commands.put(CommandType.learn, new Command(CommandType.learn));
		commands.put(CommandType.help, new Command(CommandType.help));
		commands.put(CommandType.version, new Command(CommandType.version));
	}

	private final CommandType type;
	private final List<Option<?>> options;

	private Command(CommandType type) {
		this.type = type;
		this.options = new ArrayList<Option<?>>();
	}

	public void addOption(Option<?> option) {
		this.options.add(option);
	}

	public static Command getCommand(String cmdStr) {
		Command cmd = commands.get(CommandType.valueOf(cmdStr));
		return cmd;
	}

	public List<Option<?>> getOptions() {
		return options;
	}

	public CommandType getType() {
		return type;
	}

	public enum CommandType {
		learn, help, version;
	}
}
