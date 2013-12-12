package tzuyu.engine.main;

public interface CommandHandler {

  public boolean handle(Command command);

  public boolean canHandle(Command command);
}
