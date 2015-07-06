package icsetlv;

import icsetlv.common.dto.BooleanValue;
import icsetlv.common.dto.BreakpointValue;
import icsetlv.common.dto.ExecValue;
import icsetlv.common.dto.TcExecResult;
import icsetlv.variable.TestcasesExecutor;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import libsvm.core.KernelType;
import libsvm.core.Machine;
import libsvm.core.MachineType;
import libsvm.core.Parameter;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import sav.common.core.Pair;
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

	private int valueRetrieveLevel = 3;
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
		return run(valueRetrieveLevel, getMachine());
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
	
	public Engine run(final int valRetrieveLevel, final Machine machine) throws Exception {
		TestcasesExecutor testRunner = new TestcasesExecutor(vmConfig, testcases, valRetrieveLevel);
		testRunner.run(breakPoints);
		final TcExecResult testResult = testRunner.getResult();

		results = new ArrayList<Result>(breakPoints.size());
		for (BreakPoint bkp : breakPoints) {
			LOGGER.info("Start to learn at " + bkp);
			final List<BreakpointValue> passValues = testResult.getPassValues(bkp);
			final List<BreakpointValue> failValues = testResult.getFailValues(bkp);
			// Cannot train if there are not enough data
			if(passValues.isEmpty() && failValues.isEmpty()){
				//fail to add breakpoint
				continue;
			} else if(failValues.isEmpty()){
				LOGGER.info("This line is likely not a bug!");
				Result result = new AllPositiveResult();
				setResult(result, bkp, "This line is likely not a bug!", 1);
				results.add(result);
				continue;
			} else if(passValues.isEmpty()){
				LOGGER.info("This line is likely a bug!");
				Result result = new AllNegativeResult();
				setResult(result, bkp, "This line is likely a bug!", 1);
				results.add(result);
				continue;
			}
			// Configure data for SVM machine
			machine.resetData();

			// Use the label of all the collected variables
			final Set<String> labelSet = new HashSet<String>();
			for (BreakpointValue bv : passValues) {
				labelSet.addAll(bv.getAllLabels());
			}
			for (BreakpointValue bv : failValues) {
				labelSet.addAll(bv.getAllLabels());
			}
			final List<String> varLabels = new ArrayList<String>(labelSet);			
			
			// TODO: TO REMOVE, just added for debugging
//			if (bkp.getLineNo() == 298) {
//				LOGGER.info("***********LINE 298*************");
//				LOGGER.info("varLabels");
//				LOGGER.info(varLabels);
//				LOGGER.info("passValues");
//				LOGGER.info(sav.common.core.utils.StringUtils.join(passValues, "\n"));
//				LOGGER.info("failValues");
//				LOGGER.info(sav.common.core.utils.StringUtils.join(failValues, "\n"));
//				LOGGER.info("************************");
//			}
			
			List<String> exps = new ArrayList<String>();
			//check isNull
			for (Iterator<String> it = varLabels.iterator(); it.hasNext(); ) {
				String varLabel = it.next();
				if(varLabel.endsWith("isNull")){
					learnBool(passValues, failValues, exps, varLabel);		
					it.remove();
				}
				// TODO: LLT: to check & refactor.
//				else {
//					if (failValues.size() > 0) {
//						ExecValue variableValue = failValues.get(0).findVariableById(varLabel);
//						if (variableValue instanceof BooleanValue) {
//							learnBool(passValues, failValues, exps, varLabel);
//							it.remove();
//						}
//					}
//				}
			}
			
			machine.setDataLabels(varLabels);
			BugExpert.addDataPoints(machine, passValues, failValues);
			
			// Train
			machine.train();
			String svmExp = machine.getLearnedLogic();
			
			// Store outputs
			final Result result = new Result();
			if (exps.isEmpty()) {
				setResult(result, bkp, svmExp, machine.getModelAccuracy());
			} else {
				if (machine.getModelAccuracy() == 1) {
					exps.add(svmExp);
				}
				setResult(result, bkp, sav.common.core.utils.StringUtils.join(exps, " || "),
							machine.getModelAccuracy());
			}
			LOGGER.info("Learn: " + result);
			results.add(result);
		}

		return this;
	}

	private void learnBool(final List<BreakpointValue> passValues,
			final List<BreakpointValue> failValues, List<String> exps,
			String varLabel) {
		Pair<Boolean, Boolean> allTrueFalseInPass = checkAllTrueOrAllFalse(passValues, varLabel);
		Pair<Boolean, Boolean> allTrueFalseInFail = checkAllTrueOrAllFalse(failValues, varLabel);
		
		if(allTrueFalseInPass.a && allTrueFalseInFail.b){
			exps.add(varLabel);
		}
		if(allTrueFalseInPass.b && allTrueFalseInFail.a){
			exps.add("!" + varLabel);
		}
	}

	private Pair<Boolean,Boolean> checkAllTrueOrAllFalse(final List<BreakpointValue> values,
			String varLabel) {
		boolean allTrue = true;
		boolean allFalse = true;
		boolean found = false;
		for(BreakpointValue breakPoint: values){
			Double varVal = breakPoint.getValue(varLabel, null);
			if (varVal == null) {
				continue;
			}
			found = true;
			boolean val = varVal > 0;
			allTrue &= val;
			allFalse &= !val;
			
			if(!allTrue && !allFalse){
				break;
			}
		}
		if (!found) {
			return Pair.of(false, false);
		}
		return new Pair<Boolean, Boolean>(allTrue, allFalse);
	}
	
	private void setResult(Result result, BreakPoint breakpoint, String learnedLogic, double accuracy){
		result.breakPoint = breakpoint;
		result.learnedLogic = learnedLogic;
		result.accuracy = accuracy;
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
			str.append(breakPoint.getClassCanonicalName()).append(":")
					.append(breakPoint.getLineNo()).append("\n");
			if (StringUtils.isBlank(learnedLogic)) {
				str.append("Could not learn anything.");
			} else {
				str.append("Logic: ").append(learnedLogic).append("\n");
				str.append("Accuracy: ").append(accuracy).append("\n");
			}
			return str.toString();
		}
	}
	
	public void setValueRetrieveLevel(int valueRetrieveLevel) {
		this.valueRetrieveLevel = valueRetrieveLevel;
	}
	
	public static class AllPositiveResult extends Result{}
	public static class AllNegativeResult extends Result{}

}
