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
import learntest.core.commons.data.classinfo.MethodInfo;
import sav.common.core.SavException;
import sav.common.core.utils.CollectionUtils;
import sav.common.core.utils.SingleTimer;
import sav.common.core.utils.TextFormatUtils;
import sav.strategies.dto.AppJavaClassPath;

/**
 * @author LLT
 *
 */
public class CoverageCounter {
	private Logger log = LoggerFactory.getLogger(CoverageCounter.class);

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

}
