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

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

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
	private static final Logger LOGGER = Logger.getLogger(Engine.class);
	private static final int DEFAULT_PORT = 80;
	private static final int DEFAULT_VALUE_RETRIVE_LEVEL = 4;

	private VMConfiguration vmConfig = initVmConfig();
	private Machine machine = getDefaultMachine();
	private List<String> testcases = new ArrayList<String>();
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
		
		return addBreakPoint(breakPoint);
	}
	
	public Engine addBreakPoint(BreakPoint breakPoint) {
		breakPoints.add(breakPoint);
		return this;
	}

	public Engine run() throws Exception {
		return run(DEFAULT_VALUE_RETRIVE_LEVEL, getMachine());
	}

	public Engine addTestcase(final String testcase) {
		testcases.add(testcase);
		return this;
	}
	
	public Engine addTestcases(final List<String> testcases) {
		for(String testcase: testcases){
			addTestcase(testcase);
		}
		return this;
	}

	public Engine run(final int valueRetriveLevel, final Machine machine) throws Exception {
		final TestcasesExecutor testRunner = new TestcasesExecutor(vmConfig, valueRetriveLevel);
		final TcExecResult testResult = testRunner.execute(testcases,
				breakPoints);

		results = new ArrayList<Result>(breakPoints.size());
		for (BreakPoint bkp : breakPoints) {
			LOGGER.info("Start to learn at " + bkp);
			final List<BreakpointValue> passValues = testResult.getPassValues(bkp);
			final List<BreakpointValue> failValues = testResult.getFailValues(bkp);
			// Cannot train if there are not enough data
			if(passValues.isEmpty() && failValues.isEmpty()){
				//fail to add breakpoint
				continue;
			}
			else if(failValues.isEmpty()){
				LOGGER.info("This line is likely not a bug!");
				continue;
			}
			else if(passValues.isEmpty()){
				LOGGER.info("This line is likely a bug!");
				return this;
			}
						
			// Configure data for SVM machine
			machine.resetData();
			List<String> varLabels = new ArrayList<String>(bkp.getVars().size());
			for (Variable var : bkp.getVars()) {
				varLabels.add(var.getId());
			}

			if (varLabels.size() > 0) {
				machine.setDataLabels(varLabels);
			} else {
				// User did not specify variable names
				// We use all available ones
				List<String> allVariableLabels = testResult.getAllVariableLabels(true, bkp);
				machine.setDataLabels(allVariableLabels);
			}

			BugExpert.addDataPoints(machine, passValues, failValues);

			// Train
			machine.train();

			// Store outputs
			final Result result = new Result();
			result.breakPoint = bkp;
			result.learnedLogic = machine.getLearnedLogic();
			result.accuracy = machine.getModelAccuracy();

			LOGGER.info("Learn: " + result);
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
			str.append("*******************\n");
			str.append("Breakpoint@").append(breakPoint.getClassCanonicalName()).append(":")
					.append(breakPoint.getLineNo()).append("\n");
			if (StringUtils.isBlank(learnedLogic)) {
				str.append("Could not learn anything.");
			} else {
				str.append("Logic:\n").append(learnedLogic).append("\n");				
				str.append("Accuracy: ").append(accuracy).append("\n");
			}
			return str.toString();
		}
	}

}
