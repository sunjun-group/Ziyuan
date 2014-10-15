/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package tzuyu.core.main;

import java.util.List;

import faultLocalization.LineCoverageInfo;

import main.ProgramAnalyzer;
import tzuyu.core.main.context.AbstractApplicationContext;


/**
 * @author LLT
 *
 */
public class TzuyuCore {
	private AbstractApplicationContext appContext;
	
	public TzuyuCore(AbstractApplicationContext appContext) {
		this.appContext = appContext;
	}
	

	public List<LineCoverageInfo> faultLocalization(List<String> testingClassNames,
			List<String> junitClassNames) throws Exception {
		ProgramAnalyzer analyzer = new ProgramAnalyzer(appContext);
		return analyzer.analyse(testingClassNames, junitClassNames,
				appContext.getSuspiciousnessCalculationAlgorithm());
	}
}
