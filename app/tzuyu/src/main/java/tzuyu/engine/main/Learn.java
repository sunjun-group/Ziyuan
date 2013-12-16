package tzuyu.engine.main;

import java.io.File;
import java.util.List;
import java.util.Map;

import tzuyu.engine.TzLogger;
import tzuyu.engine.Tzuyu;
import tzuyu.engine.TzuyuAlgorithmFactory;
import tzuyu.engine.iface.algorithm.Learner;
import tzuyu.engine.model.Analytics;
import tzuyu.engine.model.ClassInfo;
import tzuyu.engine.model.dfa.DFA;
import tzuyu.engine.utils.Options;
import analyzer.ClassAnalyzer;

public class Learn implements CommandHandler {

	@Override
	public boolean handle(Command command) {
		processCommand(command);

		Learner learner = TzuyuAlgorithmFactory.getLearner();
		// QueryHandler handler = new QueryHandler(initial);
		// QueryHandlerV2 handler = new QueryHandlerV2(initial);
		long startTime = System.currentTimeMillis();
		TzLogger.log().info("============Start of Statistics for",
				Analytics.getTarget().getSimpleName(), "============");
		DFA dfa = learner.startLearning();

		if (dfa == null) {
			return false;
		}
		learner.report(new CommandLineReportHandler());
		long endTime = System.currentTimeMillis();
		// logger.info("Alphabet Size in Final DFA: " + (dfa.sigma.getSize()
		// -1));
		// logger.info("Number of States in Final DFA: " + dfa.getStateSize());
		// dfa.print();
		// saveDFA(dfa);
		// logger.info("Total NO. of membership queries: " +
		// learner.getMembershipCount());
		// logger.info("Total NO. of candidate queries: " +
		// learner.getCandidateCount());
		// logger.info("Total NO. of SVM Calls: " +
		// learner.getRefinementCount());
		// logger.info("Total Time consumed by SVM: " +
		// learner.getSVMConsumedTime());
		//
		// logger.info("Total Time: " + (endTime - startTime) + " ms.");
		// logger.info("============End of Statistics for "
		// + Analytics.getTarget().getSimpleName() + "============");
		// System.out.print("=========== Starting to print out JUnit test cases=====");
		// learner.writeJUnitTestCases();
		// System.out.print("================= End of TzuYu run ====================");
		return true;
	}

	@Override
	public boolean canHandle(Command command) {
		if (command.cmdStr.equals("learn")) {
			return true;
		}
		return false;
	}

	private void processCommand(Command cmd) {

		Class<?> targetClass = null;
		try {
			String className = Option.target.getValue();
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

		String outDir = Option.output.getValue();
		File output = null;
		if (!outDir.equals("")) {
			output = new File(Option.output.getValue());
			if (!output.exists() || !output.isDirectory()) {
				TzLogger.log().error("Invalid output directory, use the default");
				output = null;
			}
		}

		List<String> methods = Option.methods.getValue();

		ClassAnalyzer analyzer = new ClassAnalyzer(targetClass, methods);
		Map<Class<?>, ClassInfo> classes = analyzer.analysis();
		Analytics.initialize(targetClass, classes);

		boolean object2Int = Option.object2Integer.getValue();
		boolean im = Option.inheritedMethods.getValue();
		// boolean object2Int = false;
		int tpq = Option.testsPerQuery.getValue();

		Options.setOptions(object2Int, tpq, output, im);
	}

}
