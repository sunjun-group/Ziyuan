package tzuyu.engine.main;

import tzuyu.engine.main.Command.CommandType;

public interface CommandHandler {

  public boolean handle(Command command);

  public CommandType[] getCmdTypes();
}
