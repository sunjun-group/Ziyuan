package learntest.activelearning.core.handler;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import learntest.activelearning.core.coverage.CoverageAgentRunner;
import learntest.activelearning.core.settings.LearntestSettings;
import learntest.core.commons.TimeController;
import learntest.core.commons.data.classinfo.MethodInfo;
import microbat.instrumentation.cfgcoverage.CoverageAgentParams;
import microbat.instrumentation.cfgcoverage.CoverageOutput;
import microbat.instrumentation.cfgcoverage.InstrumentationUtils;
import sav.common.core.SavException;
import sav.common.core.SavRtException;
import sav.common.core.utils.SingleTimer;
import sav.strategies.dto.AppJavaClassPath;

/**
 * @author LLT
 *
 */
public class CoverageCounter {
	private String agentJarPath;
	private String savJunitRunnerJarPath;
	private int cdgLayer;
	private Logger log = LoggerFactory.getLogger(CoverageCounter.class);
	private long methodExecTimeout;
	private boolean collectConditionVariation;
	private TimeController timeController = TimeController.getInstance();
	private boolean runCoverageAsMethodInvoke = false;

	public CoverageCounter(LearntestSettings settings, boolean collectConditionVariation) {
		this.agentJarPath = settings.getResources().getMicrobatInstrumentationJarPath();
		this.cdgLayer = settings.getCfgExtensionLayer();
		this.methodExecTimeout = settings.getMethodExecTimeout();
		this.savJunitRunnerJarPath = settings.getResources().getSavJunitRunnerJarPath();
		this.collectConditionVariation = collectConditionVariation;
		this.runCoverageAsMethodInvoke = settings.isRunCoverageAsMethodInvoke();
	}

	public CoverageOutput runCoverage(MethodInfo targetMethod, List<String> junitMethods,
			AppJavaClassPath appClasspath, int inputValueExtractLevel) throws SavException, SavRtException {
		log.debug("calculate coverage..");
		SingleTimer timer = SingleTimer.start("cfg-coverage");
		CoverageAgentRunner coverageAgent = new CoverageAgentRunner(agentJarPath, savJunitRunnerJarPath, appClasspath,
				runCoverageAsMethodInvoke);
		/* build agent params */
		CoverageAgentParams agentParams = new CoverageAgentParams();
		agentParams.setCdgLayer(cdgLayer);
		agentParams.setClassPaths(appClasspath.getClasspaths());
		agentParams.setTargetMethodLoc(
				InstrumentationUtils.getClassLocation(targetMethod.getClassName(), targetMethod.getMethodSignature()));
		agentParams.setInclusiveMethodIds(new ArrayList<String>());
		agentParams.setWorkingDirectory(appClasspath.getWorkingDirectory());
		agentParams.setVarLayer(inputValueExtractLevel);
		agentParams.setCollectConditionVariation(collectConditionVariation);
		CoverageOutput coverageOutput = coverageAgent.run(agentParams, methodExecTimeout, junitMethods);
		timer.captureExecutionTime();
		timer.logResults(log);
		timeController.logCoverageRunningTime(targetMethod, junitMethods, timer.getExecutionTime());
		return coverageOutput;
	}
	
}
