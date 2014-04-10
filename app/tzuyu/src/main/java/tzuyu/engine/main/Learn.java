package tzuyu.engine.main;

import java.util.List;
import java.util.Map;

import tzuyu.engine.TzClass;
import tzuyu.engine.TzConfiguration;
import tzuyu.engine.Tzuyu;
import tzuyu.engine.iface.IReferencesAnalyzer;
import tzuyu.engine.main.Command.CommandType;
import tzuyu.engine.model.ClassInfo;
import tzuyu.engine.model.exception.ReportException;
import tzuyu.engine.model.exception.TzException;
import analyzer.ClassAnalyzer;

public class Learn implements CommandHandler {

	public boolean handle(Command command) {
		TzClass project = processCommand(command);
		CommandLineReportHandler reporter = new CommandLineReportHandler(project.getConfiguration());
		
		Tzuyu tzuyuEngine = new Tzuyu(project, reporter, new IReferencesAnalyzer() {

			@Override
			public Class<?> getRandomImplClzz(Class<?> iface) {
				// TODO Auto-generated method stub
				return null;
			}
		});
		try {
			tzuyuEngine.run();
		} catch (ReportException e) {
			// TODO nice to have
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO nice to have
			e.printStackTrace();
		} catch (TzException e) {
			// TODO nice to have
			e.printStackTrace();
		}
		
		return true;
	}

	private TzClass processCommand(Command cmd) {
		TzClass project = new TzClass();
		TzConfiguration config = new TzConfiguration(true);
		project.setConfiguration(config);
		
		for (Option<?> opt : cmd.getOptions()) {
			opt.transferToTzConfig(project);
		}
		
		Class<?> targetClass = null;
		try {
			String className = project.getClassName();
			if (className.equals("")) {
				CommandLineLogger.instance().error(
						"Target class is not specified, system aborts");
				System.exit(1);
			}

			targetClass = Class.forName(className);
		} catch (ClassNotFoundException e) {
			CommandLineLogger.instance().error(
					"Invalid target class is specified, system aborts");
			System.exit(1);
		}
		
		List<String> methods = Option.methods.getValue();

		ClassAnalyzer analyzer = new ClassAnalyzer(targetClass, methods);
		Map<Class<?>, ClassInfo> classes = analyzer.analysis();
		project.setClasses(targetClass, classes);

		return project;
	}

	public CommandType[] getCmdTypes() {
		return new CommandType[] {CommandType.learn};
	}
}
