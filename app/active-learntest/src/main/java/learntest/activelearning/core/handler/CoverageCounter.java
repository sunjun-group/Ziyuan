package learntest.activelearning.core.handler;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cfg.CFG;
import cfgcoverage.jacoco.CfgJaCoCo;
import cfgcoverage.jacoco.analysis.data.CfgCoverage;
import cfgcoverage.jacoco.utils.CfgJaCoCoUtils;
import cfgcoverage.jacoco.utils.CoverageUtils;
import learntest.activelearning.core.coverage.CoverageAgentRunner;
import learntest.activelearning.core.settings.LearntestSettings;
import learntest.core.commons.data.classinfo.MethodInfo;
import microbat.instrumentation.cfgcoverage.CoverageAgentParams;
import microbat.instrumentation.cfgcoverage.CoverageOutput;
import microbat.model.ClassLocation;
import sav.common.core.SavException;
import sav.common.core.utils.CollectionUtils;
import sav.common.core.utils.SingleTimer;
import sav.common.core.utils.TextFormatUtils;
import sav.strategies.dto.AppJavaClassPath;
import sav.strategies.vm.VMConfiguration;

/**
 * @author LLT
 *
 */
public class CoverageCounter {
	private String agentJarPath;
	private int cdgLayer;
	private Logger log = LoggerFactory.getLogger(CoverageCounter.class);

	public CoverageCounter(String agentJarPath, int cdgLayer) {
		this.agentJarPath = agentJarPath;
		this.cdgLayer = cdgLayer;
	}
	
	public CfgCoverage countCoverage(MethodInfo targetMethod, List<String> junitClassNames, CFG cfg, AppJavaClassPath appClasspath)
			throws SavException {
		log.debug("calculate coverage..");
		SingleTimer timer = SingleTimer.start("cfg-coverage");
		CfgJaCoCo jacoco = new CfgJaCoCo(appClasspath);
		String methodId = CfgJaCoCoUtils.createMethodId(targetMethod.getClassName(), targetMethod.getMethodName(),
				targetMethod.getSignature());
		List<String> targetMethods = CollectionUtils.listOf(methodId);
		Map<String, CfgCoverage> coverageMap = jacoco.runJunit(targetMethods,
				Arrays.asList(targetMethod.getClassName()), junitClassNames);
		CfgCoverage cfgCoverage = coverageMap.get(methodId);
		if (cfgCoverage == null) {
			log.debug("Cannot get cfgCoverage from result map!");
			log.debug("coverageMap={}", TextFormatUtils.printMap(coverageMap));
		} else {
			List<String> lines = CoverageUtils.getBranchCoverageDisplayTexts(cfgCoverage, -1);
			for (String line : lines) {
				log.debug(line);
			}
		}
		timer.logResults(log);
		return cfgCoverage;
	}

	public CoverageOutput runCoverage(MethodInfo targetMethod, List<String> junitClassNames, CFG cfg,
			AppJavaClassPath appClasspath) throws SavException {
		log.debug("calculate coverage..");
		SingleTimer timer = SingleTimer.start("cfg-coverage");
		CoverageAgentRunner coverageAgent = new CoverageAgentRunner(agentJarPath, new VMConfiguration(appClasspath));
		CoverageAgentParams agentParams = new CoverageAgentParams();
		agentParams.setCdgLayer(cdgLayer);
		agentParams.setClassPaths(appClasspath.getClasspaths());
		agentParams.setTargetMethodLoc(new ClassLocation(targetMethod.getClassName(), targetMethod.getMethodSignature(), -1));
		CoverageOutput coverageOutput = coverageAgent.run(agentParams);
		timer.logResults(log);
		return coverageOutput;
	}
}
