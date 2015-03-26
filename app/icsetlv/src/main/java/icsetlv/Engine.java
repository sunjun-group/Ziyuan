package icsetlv;

import icsetlv.common.dto.TcExecResult;
import icsetlv.variable.TestcasesExecutor;

import java.util.ArrayList;
import java.util.List;

import libsvm.core.KernelType;
import libsvm.core.Machine;
import libsvm.core.MachineType;
import libsvm.core.Parameter;
import sav.commons.TestConfiguration;
import sav.commons.utils.TestConfigUtils;
import sav.strategies.dto.BreakPoint;
import sav.strategies.dto.BreakPoint.Variable;
import sav.strategies.vm.VMConfiguration;

/**
 * Wrapper class used to centralize the configuration and executing of test
 * cases then use the test results for SVM learning.
 * 
 * Usage:
 * <ol>
 * <li>new</li>
 * <li>configure
 * <ul>
 * <li>set up environment variables</li>
 * <li>define the list of breakpoints (class name + method name + code line
 * number)</li>
 * <li>define the list of test cases (passed + failed)</li>
 * </ul>
 * <li>run</li>
 * <ul>
 * <li>the test cases will be run and variables will be collected at the
 * configured breakpoints</li>
 * <li>test result is used to build SVM data points</li>
 * <li>SVM machine is setup + run for each breakpoint</li>
 * </ul>
 * <li>get results (i.e.: the predicates learned from the values at the
 * breakpoints)</li>
 * </ol>
 * 
 * @author Nguyen Phuoc Nguong Phuc (npn)
 * 
 */
public class Engine {

	private static final int DEFAULT_PORT = 80;
	private static final int DEFAULT_VALUE_RETRIVE_LEVEL = 4;

	private VMConfiguration vmConfig = initVmConfig();
	private List<String> passedTestCases = new ArrayList<String>();
	private List<String> failedTestCases = new ArrayList<String>();
	private List<BreakPoint> breakPoints = new ArrayList<BreakPoint>();
	private List<Result> results;

	public Engine reset() {
		return new Engine();
	}

	private VMConfiguration initVmConfig() {
		final VMConfiguration vmConfig = new VMConfiguration();
		vmConfig.setJavaHome(TestConfigUtils.getJavaHome());
		vmConfig.setDebug(true);
		vmConfig.setPort(DEFAULT_PORT);
		TestConfiguration config = TestConfiguration.getInstance();
		vmConfig.setLaunchClass(config.getJunitcore());
		vmConfig.addClasspath(config.getJavaBin());
		vmConfig.addClasspath(TestConfiguration.SAV_COMMONS_TEST_TARGET);
		vmConfig.addClasspath(config.getJunitLib());
		return vmConfig;
	}

	public Engine setJavaHome(final String javaHome) {
		vmConfig.setJavaHome(javaHome);
		return this;
	}

	public Engine setPort(final int portNumber) {
		vmConfig.setPort(portNumber);
		return this;
	}

	public Engine setDebug(final boolean debugEnabled) {
		vmConfig.setDebug(debugEnabled);
		return this;
	}

	public Engine setLaunchClass(final String launchClass) {
		vmConfig.setLaunchClass(launchClass);
		return this;
	}

	public Engine addToClassPath(final String path) {
		vmConfig.addClasspath(path);
		return this;
	}

	public Engine addBreakPoint(final String className, final String methodName,
			final int lineNumber, final String... variableNames) {
		BreakPoint breakPoint = new BreakPoint(className, methodName, lineNumber);
		for (String variableName : variableNames) {
			breakPoint.addVars(new Variable(variableName));
		}
		breakPoints.add(breakPoint);
		return this;
	}

	public Engine run() throws Exception {
		return run(DEFAULT_VALUE_RETRIVE_LEVEL, getDefaultMachine());
	}

	public Engine addPassedTestcase(final String testcase) {
		passedTestCases.add(testcase);
		return this;
	}

	public Engine addFailedTestcase(final String testcase) {
		failedTestCases.add(testcase);
		return this;
	}

	public Engine run(final int valueRetriveLevel, final Machine machine) throws Exception {
		TestcasesExecutor testRunner = new TestcasesExecutor(vmConfig, valueRetriveLevel);
		final TcExecResult testResult = testRunner.execute(passedTestCases, failedTestCases,
				breakPoints);

		results = new ArrayList<Result>(breakPoints.size());
		for (BreakPoint bkp : breakPoints) {
			// Configure data for SVM machine
			machine.resetData();
			final List<String> varLabels = new ArrayList<String>(bkp.getVars().size());
			for (Variable var : bkp.getVars()) {
				varLabels.add(var.getName());
			}
			machine.setDataLabels(varLabels);
			BugExpert.addDataPoints(machine, testResult.getPassValues(bkp),
					testResult.getFailValues(bkp));

			// Train
			machine.train();

			// Store outputs
			final Result result = new Result();
			result.breakPoint = bkp;
			result.learnedLogic = machine.getLearnedLogic();
			result.accuracy = machine.getModelAccuracy();

			results.add(result);
		}

		return this;
	}

	protected Machine getDefaultMachine() {
		final Machine machine = new Machine();
		return machine.setParameter(new Parameter().setMachineType(MachineType.C_SVC)
				.setKernelType(KernelType.LINEAR).setEps(1.0).setUseShrinking(false)
				.setPredictProbability(false).setC(Double.MAX_VALUE));
	}

	public List<Result> getResults() {
		return this.results;
	}

	public static class Result {
		private BreakPoint breakPoint;
		private String learnedLogic;
		private double accuracy;

		private Result() {
			// To disable initiation from outside of Engine class
		}

		public BreakPoint getBreakPoint() {
			// TODO NPN check if it's needed to return a copy here
			return breakPoint;
		}

		public String getLearnedLogic() {
			return learnedLogic;
		}

		public double getAccuracy() {
			return accuracy;
		}

		@Override
		public String toString() {
			final StringBuilder str = new StringBuilder();
			str.append(breakPoint);
			str.append(" --> ");
			str.append(learnedLogic);
			str.append(" (");
			str.append(accuracy);
			str.append(")");
			return str.toString();
		}
	}
}
