/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package learntest.evaluation.jdart;

import java.util.Collections;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jdart.core.JDartParams;
import jdart.core.socket2.JDartProcess;
import jdart.core.socket2.JDartProcessOnDemand;
import jdart.model.TestInput;
import learntest.core.LearnTestParams;
import learntest.core.LearnTestParams.LearntestSystemVariable;
import learntest.core.commons.data.classinfo.TargetMethod;
import learntest.plugin.utils.IResourceUtils;
import learntest.plugin.utils.JdartConstants;
import sav.common.core.Pair;
import sav.common.core.utils.StringUtils;
import sav.strategies.dto.AppJavaClassPath;

/**
 * @author LLT
 * Duplicate from Learntest with the intention to replace the old one.
 */
public class JDartRunner {
	private static Logger log = LoggerFactory.getLogger(JDartRunner.class);
	private AppJavaClassPath appClasspath;

	public JDartRunner(AppJavaClassPath appClasspath) {
		this.appClasspath = appClasspath;
	}
	
	@SuppressWarnings("unchecked")
	public Pair<List<TestInput>, Integer> runJDart(JDartParams jdartParams, String mainClass){
		try {
			/* run jdart */
			if (mainClass == null) {
				return new Pair<List<TestInput>, Integer>(Collections.EMPTY_LIST, 0);
			}
			jdartParams.setMainEntry(mainClass);
			JDartProcess jdartCore = new JDartProcess();
			List<TestInput> inputs = jdartCore.run(jdartParams);
			int solveCount = jdartCore.getSolveCount();
			Pair<List<TestInput>, Integer> pair = new Pair<List<TestInput>, Integer>(inputs, solveCount);
			return pair;
		} catch (Exception e) {
			log.debug("Fail running JDart", e.getMessage());
		}
		
		return new Pair<List<TestInput>, Integer>(Collections.EMPTY_LIST, 0);
	}
	

	@SuppressWarnings("unchecked")
	public List<TestInput> runJDartOnDemand(LearnTestParams learntestParams, String mainClass,String jdartInitTc, int node, int branch){
		try {
			JDartParams jdartParams = initJDartParams(learntestParams);
			/* run jdart */
			if (mainClass == null) {
				return Collections.EMPTY_LIST;
			}
			jdartParams.setMainEntry(mainClass);
			jdartParams.setExploreBranch(branch);
			jdartParams.setExploreNode(node);
			jdartParams.setSiteProperties(IResourceUtils.getResourceAbsolutePath(JdartConstants.BUNDLE_ID,
					"libs/jpf_on_demand.properties"));
			JDartProcessOnDemand jdartCore = new JDartProcessOnDemand();
			List<TestInput> inputs = jdartCore.run(jdartParams, jdartInitTc);
			return inputs;
		} catch (Exception e) {
			log.debug("Fail running JDart", e.getMessage());
		}
		
		return Collections.EMPTY_LIST;
	}
	
	private JDartParams initJDartParams(LearnTestParams learntestParams) throws CoreException {
		JDartParams params = new JDartParams();
		params.setAppProperties(learntestParams.getSystemConfig().get(LearntestSystemVariable.JDART_APP_PROPRETIES.name()));
		params.setSiteProperties(learntestParams.getSystemConfig().get(LearntestSystemVariable.JDART_SITE_PROPRETIES.name()));
		TargetMethod targetMethod = learntestParams.getTargetMethod();
		params.setClassName(targetMethod.getClassName());
		params.setMethodName(targetMethod.getMethodName());
		params.setParamString(buildJDartParamStr(targetMethod));
		params.setClasspathStr(StringUtils.join(appClasspath.getClasspaths(), ";"));
		params.setMinFree(20*(1024<<10));
		params.setTimeLimit(3 * 1000);
		return params;
	}

	private String buildJDartParamStr(TargetMethod targetMethod) {
		int lastIdx = targetMethod.getParams().size() - 1;
		StringBuilder sb = new StringBuilder("(");
		for (int i = 0; i <= lastIdx; i++) {
			sb.append(targetMethod.getParams().get(i))
				.append(":")
				.append(targetMethod.getParamTypes().get(i));
			if (i < lastIdx) {
				sb.append(",");
			}
		}
		sb.append(")");
		return sb.toString();
	}
}
