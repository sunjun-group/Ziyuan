package learntest.activelearning.core.settings;

/**
 * @author LLT
 *
 */
public class LearntestSettings {
	private long methodExecTimeout = 200l; // ms
	private int initRandomTestNumber = 1;
	private int inputValueExtractLevel = 3;
	private int nnLearningThreshold = 3;
	private int cfgExtensionLayer = 2;
	private LearnTestResources resources;
	private boolean runCoverageAsMethodInvoke = false;
	private boolean addMainClassWhenGeneratingTest = false;
	private long runtimeForEachRound = 60000l;
	private int testingIteration = 10;
	
	public LearntestSettings(LearnTestResources resources) {
		this.resources = resources;
	}

	public long getMethodExecTimeout() {
		return methodExecTimeout;
	}

	public void setMethodExecTimeout(long methodExecTimeout) {
		this.methodExecTimeout = methodExecTimeout;
	}

	public int getInitRandomTestNumber() {
		return initRandomTestNumber;
	}

	public void setInitRandomTestNumber(int initRandomTestNumber) {
		this.initRandomTestNumber = initRandomTestNumber;
	}

	public int getInputValueExtractLevel() {
		return inputValueExtractLevel;
	}

	public void setInputValueExtractLevel(int inputValueExtractLevel) {
		this.inputValueExtractLevel = inputValueExtractLevel;
	}

	public int getNnLearningThreshold() {
		return nnLearningThreshold;
	}

	public void setNnLearningThreshold(int nnLearningThreshold) {
		this.nnLearningThreshold = nnLearningThreshold;
	}

	public int getCfgExtensionLayer() {
		return cfgExtensionLayer;
	}

	public void setCfgExtensionLayer(int cdgLayer) {
		this.cfgExtensionLayer = cdgLayer;
	}

	public LearnTestResources getResources() {
		return resources;
	}

	public void setResources(LearnTestResources resources) {
		this.resources = resources;
	}

	public boolean isRunCoverageAsMethodInvoke() {
		return runCoverageAsMethodInvoke;
	}

	public void setRunCoverageAsMethodInvoke(boolean runCoverageAsMethodInvoke) {
		this.runCoverageAsMethodInvoke = runCoverageAsMethodInvoke;
	}

	public boolean isAddMainClassWhenGeneratingTest() {
		return addMainClassWhenGeneratingTest;
	}

	public void setAddMainClassWhenGeneratingTest(boolean addMainClassWhenGeneratingTest) {
		this.addMainClassWhenGeneratingTest = addMainClassWhenGeneratingTest;
	}

	public long getRuntimeForEachRound() {
		return runtimeForEachRound;
	}

	public void setRuntimeForEachRound(long runtimeForEachRound) {
		this.runtimeForEachRound = runtimeForEachRound;
	}

	public int getTestingIteration() {
		return testingIteration;
	}

	public void setTestingIteration(int testingIteration) {
		this.testingIteration = testingIteration;
	}
	
}
