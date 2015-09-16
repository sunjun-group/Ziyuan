package icsetlv;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import icsetlv.common.dto.BkpInvariantResult;
import icsetlv.variable.TestcasesExecutor;
import libsvm.core.KernelType;
import libsvm.core.Machine;
import libsvm.core.MachineType;
import libsvm.core.Parameter;
import libsvm.extension.FeatureSelectionMachine;
import sav.common.core.utils.JunitUtils;
import sav.strategies.dto.AppJavaClassPath;
import libsvm.extension.PositiveSeparationMachine;
import libsvm.extension.RandomNegativePointSelection;
import sav.strategies.dto.BreakPoint;
import sav.strategies.dto.BreakPoint.Variable;

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
	private TestcasesExecutor testcaseExecutor;
	private AppJavaClassPath appClasspath;
	private Machine machine = getDefaultMachine();
	private List<String> testcases = new ArrayList<String>();
	private List<BreakPoint> breakPoints = new ArrayList<BreakPoint>();

	public Engine(AppJavaClassPath app) {
		this.appClasspath = app;
	}

	public Engine reset(AppJavaClassPath app) {
		return new Engine(app);
	}

	public List<BkpInvariantResult> run() throws Exception {
		InvariantMediator learner = new InvariantMediator(appClasspath);
		learner.setTcExecutor(getTestcaseExecutor());
		learner.setMachine(getMachine());
		return learner.learn(testcases, breakPoints);
	}	

	public void setMachine(Machine machine) {
		this.machine = machine;
	}

	private Machine getMachine() {
		return this.machine;
	}

	public static Machine getDefaultMachine() {
//		final Machine machine = new PositiveSeparationMachine(new RandomNegativePointSelection());
//		Machine machine = new FeatureSelectionMachine();
		Machine machine = new Machine();
		return machine.setParameter(new Parameter().setMachineType(MachineType.C_SVC)
				.setKernelType(KernelType.LINEAR).setEps(0.00001).setUseShrinking(false)
				.setPredictProbability(false).setC(Double.MAX_VALUE));
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
	
	public Engine addTestcases(String testClass) throws ClassNotFoundException {
		addTestcases(JunitUtils.extractTestMethods(Arrays.asList(testClass)));
		return this;
	}
	
	public void setTestcaseExecutor(TestcasesExecutor testcaseExecutor) {
		this.testcaseExecutor = testcaseExecutor;
	}
	
	public TestcasesExecutor getTestcaseExecutor() {
		if (testcaseExecutor == null) {
			testcaseExecutor = new TestcasesExecutor(DefaultValues.DEBUG_VALUE_RETRIEVE_LEVEL);
		}
		return testcaseExecutor;
	}

}
