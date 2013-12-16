package tzuyu.engine.main;

import java.util.List;
import java.util.Map;

import tzuyu.engine.TzConfiguration;
import tzuyu.engine.TzLogger;
import tzuyu.engine.TzProject;
import tzuyu.engine.Tzuyu;
import tzuyu.engine.main.Command.CommandType;
import tzuyu.engine.model.ClassInfo;
import analyzer.ClassAnalyzer;

public class Learn implements CommandHandler {

	@Override
	public boolean handle(Command command) {
		TzProject project = processCommand(command);
		CommandLineReportHandler reporter = new CommandLineReportHandler();
		
		Tzuyu tzuyuEngine = new Tzuyu(project, reporter);
		tzuyuEngine.run();
		
		return true;
	}

	private TzProject processCommand(Command cmd) {
		TzProject project = new TzProject();
		TzConfiguration config = new TzConfiguration();
		project.setConfiguration(config);
		
		for (Option<?> opt : cmd.getOptions()) {
			opt.transferToTzConfig(project);
		}
		
		Class<?> targetClass = null;
		try {
			String className = project.getClassName();
			if (className.equals("")) {
				TzLogger.log().error(
						"Target class is not specified, system aborts");
				System.exit(1);
			}

			targetClass = Class.forName(className);
		} catch (ClassNotFoundException e) {
			TzLogger.log().error(
					"Invalid target class is specified, system aborts");
			System.exit(1);
		}
		
		List<String> methods = Option.methods.getValue();

		ClassAnalyzer analyzer = new ClassAnalyzer(targetClass, methods);
		Map<Class<?>, ClassInfo> classes = analyzer.analysis();
		project.setClasses(targetClass, classes);

		return project;
	}

	@Override
	public CommandType[] getCmdTypes() {
		return new CommandType[] {CommandType.learn};
	}
}
