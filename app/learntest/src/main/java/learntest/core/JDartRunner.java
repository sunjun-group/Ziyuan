/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package learntest.core;

import java.util.Collections;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jdart.core.JDartCore;
import jdart.core.JDartParams;
import jdart.model.TestInput;
import learntest.core.commons.data.classinfo.TargetMethod;
import learntest.main.LearnTestParams;
import learntest.main.LearnTestParams.LearntestSystemVariable;
import sav.common.core.utils.StringUtils;
import sav.strategies.dto.AppJavaClassPath;

/**
 * @author LLT
 *
 */
public class JDartRunner {
	private static Logger log = LoggerFactory.getLogger(JDartRunner.class);
	private AppJavaClassPath appClasspath;

	public JDartRunner(AppJavaClassPath appClasspath) {
		this.appClasspath = appClasspath;
	}
	
	@SuppressWarnings("unchecked")
	public List<TestInput> runJDart(LearnTestParams learntestParams){
		try {
			JDartParams jdartParams = initJDartParams(learntestParams);
			/* run jdart */
			String mainClass = learntestParams.getInitialTests().getMainClass();
			if (mainClass == null) {
				return Collections.EMPTY_LIST;
			}
			jdartParams.setMainEntry(mainClass);
			JDartCore jdartCore = new JDartCore();
			List<TestInput> inputs = jdartCore.run(jdartParams);
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
