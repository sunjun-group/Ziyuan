/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package cfgcoverage.jacoco;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jacoco.core.data.ExecutionData;
import org.jacoco.core.data.ExecutionData.ProbesType;
import org.jacoco.core.runtime.AgentOptions;

import cfgcoverage.jacoco.analysis.data.CfgCoverage;
import codecoverage.jacoco.agent.JaCoCo;
import sav.common.core.SavException;
import sav.strategies.dto.AppJavaClassPath;

/**
 * @author LLT
 *
 */
public class CfgJaCoCo {
	private AppJavaClassPath appClasspath;
	private JaCoCo jacoco;
	
	public CfgJaCoCo(AppJavaClassPath appClasspath) {
		this.appClasspath = appClasspath;
		// set up jacoco
		ProbesType probesType = ProbesType.INTEGER;
		ExecutionData.setProbesType(probesType);
		Map<String, String> extraAgentParams = new HashMap<String, String>();
		extraAgentParams.put(AgentOptions.PROBESTYPE, probesType.name());
		jacoco = new JaCoCo(appClasspath);
		jacoco.setMoreAgentParams(extraAgentParams);
	}
	
	public List<CfgCoverage> run(List<String> testingClassNames,
			List<String> junitClassNames) throws SavException, IOException,
			ClassNotFoundException {
		ExecutionReporter reporter = new ExecutionReporter(new String[] {
				appClasspath.getTarget(), appClasspath.getTestTarget() });
		jacoco.run(reporter, testingClassNames, junitClassNames);
		return reporter.getCoverage();
	}
	
}
