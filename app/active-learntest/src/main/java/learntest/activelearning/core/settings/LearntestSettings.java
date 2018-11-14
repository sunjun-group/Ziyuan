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
	private int receiverFieldRetrieveLevel = 2; 
	private int cfgExtensionLayer = 2;
	private LearnTestResources resources;
	private boolean runCoverageAsMethodInvoke = false;
	private boolean addMainClassWhenGeneratingTest = false;
	private long runtimeForEachRound = 60000l;
	private int testingIteration = 10;
	private boolean coverageRunSocket = false;
	private int learnArraySizeThreshold = 10;
	private long eachGradientSearchExecutionTimeOut = 60000; //ms
	private long gentestTargetMethodTimeout = 200l;
	private long gentestExtMethodTimeout = 20l;
	
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

	public boolean isCoverageRunSocket() {
		return coverageRunSocket;
	}

	public void setCoverageRunSocket(boolean coverageRunSocket) {
		this.coverageRunSocket = coverageRunSocket;
	}

	public int getLearnArraySizeThreshold() {
		return learnArraySizeThreshold;
	}

	public void setLearnArraySizeThreshold(int learnArraySizeThreshold) {
		this.learnArraySizeThreshold = learnArraySizeThreshold;
	}

	public long getEachGradientSearchExecutionTimeOut() {
		return eachGradientSearchExecutionTimeOut;
	}

	public void setEachGradientSearchExecutionTimeOut(long branchExecutionTimeOut) {
		this.eachGradientSearchExecutionTimeOut = branchExecutionTimeOut;
	}

	public long getGentestTargetMethodTimeout() {
		return gentestTargetMethodTimeout;
	}

	public void setGentestTargetMethodTimeout(long gentestTargetMethodTimeout) {
		this.gentestTargetMethodTimeout = gentestTargetMethodTimeout;
	}

	public long getGentestExtMethodTimeout() {
		return gentestExtMethodTimeout;
	}

	public void setGentestExtMethodTimeout(long gentestExtMethodTimeout) {
		this.gentestExtMethodTimeout = gentestExtMethodTimeout;
	}

	public int getReceiverFieldRetrieveLevel() {
		return receiverFieldRetrieveLevel;
	}

	public void setReceiverFieldRetrieveLevel(int receiverFieldRetrieveLevel) {
		this.receiverFieldRetrieveLevel = receiverFieldRetrieveLevel;
	}
}
