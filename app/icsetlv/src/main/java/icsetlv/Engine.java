package icsetlv;

import icsetlv.common.dto.BreakpointValue;
import icsetlv.common.dto.TcExecResult;
import icsetlv.variable.TestcasesExecutor;

import java.util.ArrayList;
import java.util.List;

import libsvm.core.KernelType;
import libsvm.core.Machine;
import libsvm.core.MachineType;
import libsvm.core.Parameter;
import sav.common.core.Pair;
import sav.common.core.utils.Assert;
import sav.strategies.dto.BreakPoint;
import sav.strategies.dto.BreakPoint.Variable;
import sav.strategies.junit.JunitRunner;
import sav.strategies.junit.JunitRunnerParameters;
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
	private Machine machine = getDefaultMachine();
	private List<String> passedTestCases = new ArrayList<String>();
	private List<String> failedTestCases = new ArrayList<String>();
	private List<String> notExecutedTestcases = new ArrayList<String>();
	private List<BreakPoint> breakPoints = new ArrayList<BreakPoint>();
	private List<Result> results;

	public Engine reset() {
		return new Engine();
	}

	private VMConfiguration initVmConfig() {
		final VMConfiguration vmConfig = new VMConfiguration();
		vmConfig.setDebug(true);
		vmConfig.setPort(DEFAULT_PORT);
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
	
	public Engine addProgramArgument(String argument) {
		vmConfig.addProgramArgs(argument);
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
		return run(DEFAULT_VALUE_RETRIVE_LEVEL, getMachine());
	}

	public Engine addPassedTestcase(final String testcase) {
		passedTestCases.add(testcase);
		return this;
	}

	public Engine addFailedTestcase(final String testcase) {
		failedTestCases.add(testcase);
		return this;
	}

	public Engine addNotExecutedTestcase(final String testcase) {
		notExecutedTestcases.add(testcase);
		return this;
	}

	private void evaluateNotExecutedTestcases() throws Exception {
		if (notExecutedTestcases.isEmpty()) {
			return;
		}
		final JunitRunnerParameters params = new JunitRunnerParameters();
		params.setClassMethods(notExecutedTestcases);
		final List<Pair<String, String>> failedTests = JunitRunner.runTestcases(params)
				.getFailTests();
		final List<String> failedTcs = new ArrayList<String>(failedTests.size());
		for (Pair<String, String> test : failedTests) {
			failedTcs.add(new StringBuilder().append(test.a).append(".").append(test.b).toString());
		}
		for (String testcase : notExecutedTestcases) {
			if (failedTcs.contains(testcase)) {
				this.failedTestCases.add(testcase);
			} else {
				this.passedTestCases.add(testcase);
			}
		}
	}

	public Engine run(final int valueRetriveLevel, final Machine machine) throws Exception {
		evaluateNotExecutedTestcases();

		final TestcasesExecutor testRunner = new TestcasesExecutor(vmConfig, valueRetriveLevel);
		final TcExecResult testResult = testRunner.execute(passedTestCases, failedTestCases,
				breakPoints);

		results = new ArrayList<Result>(breakPoints.size());
		for (BreakPoint bkp : breakPoints) {
			// Configure data for SVM machine
			machine.resetData();
			List<String> varLabels = new ArrayList<String>(bkp.getVars().size());
			for (Variable var : bkp.getVars()) {
				varLabels.add(var.getName());
			}

			if (varLabels.size() > 0) {
				machine.setDataLabels(varLabels);
			} else {
				// User did not specify variable names
				// We use all available ones
				final BreakpointValue sample = testResult.getSampleValue(bkp);
				Assert.assertTrue(sample != null, "No test result found.");
				Assert.assertTrue(
						sample.getAllLabels() != null && sample.getAllLabels().size() > 0,
						"No variable exists at the given breakpoint.");
				machine.setDataLabels(sample.getAllLabels());
			}

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

	public void setMachine(Machine machine) {
		this.machine = machine;
	}

	private Machine getMachine() {
		return this.machine;
	}

	private Machine getDefaultMachine() {
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
			str.append("Breakpoint@").append(breakPoint.getClassCanonicalName()).append(":")
					.append(breakPoint.getLineNo()).append(" >>> ");
			str.append("Logic: ").append(learnedLogic).append(" >>> ");
			str.append("(Accuracy: ").append(accuracy).append(")");
			return str.toString();
		}
	}

}
