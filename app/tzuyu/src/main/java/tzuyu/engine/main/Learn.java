package tzuyu.engine.main;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tzuyu.engine.TzClass;
import tzuyu.engine.TzConfiguration;
import tzuyu.engine.TzMethod;
import tzuyu.engine.main.Command.CommandType;
import tzuyu.engine.model.ClassInfo;
import analyzer.ClassAnalyzer;

public class Learn implements CommandHandler {
	private static Logger log = LoggerFactory.getLogger(Help.class);
	public boolean handle(Command command) {
		TzClass project = processCommand(command);
		CommandLineReportHandler reporter = new CommandLineReportHandler(project.getConfiguration());
		
//		Tzuyu tzuyuEngine = new Tzuyu(project, reporter, new ISubTypesScanner() {
//
//			@Override
//			public Class<?> getRandomImplClzz(Class<?> iface) {
//				// TODO Auto-generated method stub
//				return null;
//			}
//		});
//		try {
//			tzuyuEngine.dfaLearning();
//			
//		} catch (ReportException e) {
//			// TODO nice to have
//			e.printStackTrace();
//		} catch (InterruptedException e) {
//			// TODO nice to have
//			e.printStackTrace();
//		} catch (TzException e) {
//			// TODO nice to have
//			e.printStackTrace();
//		}
//		
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
				log.error(
						"Target class is not specified, system aborts");
				System.exit(1);
			}

			targetClass = Class.forName(className);
		} catch (ClassNotFoundException e) {
			log.error(
					"Invalid target class is specified, system aborts");
			System.exit(1);
		}
		
		List<TzMethod> methods = new ArrayList<TzMethod>();
		for (String name : Option.methods.getValue()) {
			methods.add(new TzMethod(name, ""));
		}

		ClassAnalyzer analyzer = new ClassAnalyzer(targetClass, methods);
		Map<Class<?>, ClassInfo> classes = analyzer.analysis();
		project.setClasses(targetClass, classes);

		return project;
	}

	public CommandType[] getCmdTypes() {
		return new CommandType[] {CommandType.learn};
	}
}
