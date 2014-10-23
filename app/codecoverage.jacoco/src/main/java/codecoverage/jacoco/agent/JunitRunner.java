/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package codecoverage.jacoco.agent;

import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.junit.runner.Request;
import org.junit.runner.notification.Failure;

import sav.common.core.Pair;
import sav.common.core.utils.ClassUtils;
import sav.common.core.utils.CollectionUtils;
import sav.strategies.dto.BreakPoint;
import codecoverage.jacoco.RequestExecution;

/**
 * @author LLT
 *
 */
public class JunitRunner {
	
	public static void main(String[] args) throws Exception {
		if (CollectionUtils.isEmpty(args)) {
			System.exit(0);
		}
		JunitRunnerParameters params = JunitRunnerParameters.parse(args);
		List<Pair<String, String>> classMethods = toPair(params.getClassMethods());
		RequestExecution requestExec = new RequestExecution();
		JunitResult result = new JunitResult();
		for (Pair<String, String> classMethod : classMethods) {
			Request request = toRequest(classMethod);
			if (request == null) {
				continue;
			}
			requestExec.setRequest(request);
			requestExec.run();
			extractBrkpsFromTrace(requestExec.getFailures(), params.getTestingClassNames(),
					result.getFailureTraces());
			result.addResult(classMethod, requestExec.getFailures().isEmpty());
		}
		
		File file = new File(params.getDestfile());
		result.save(file);
	}
	
	private static void extractBrkpsFromTrace(List<Failure> failureTrace,
			List<String> testingClassNames, Set<BreakPoint> failureTraces) {
		for (Failure failure : failureTrace) {
			for (StackTraceElement trace : failure.getException()
					.getStackTrace()) {
				if (trace.getClassName() != null
						&& testingClassNames.contains(trace.getClassName())) {
					failureTraces.add(new BreakPoint(trace.getClassName(),
							trace.getMethodName(), trace.getLineNumber()));
				}
			}
		}
	}
	
	private static Request toRequest(Pair<String, String> pair)
			throws ClassNotFoundException {
		Class<?> junitClass = loadClass(pair.a);
		Method[] methods = junitClass.getMethods();
		for (Method method : methods) {
			if (method.getName().equals(pair.b)) {
				return Request.method(junitClass, method.getName());
			}
		}
		return null;
	}
	
	private static List<Pair<String, String>> toPair(List<String> junitClassTestMethods) {
		List<Pair<String, String>> result = new ArrayList<Pair<String,String>>(junitClassTestMethods.size());
		for (String classMethod : junitClassTestMethods) {
			result.add(ClassUtils.splitClassMethod(classMethod));
		}
		return result;
	}

	private static Class<?> loadClass(String className) throws ClassNotFoundException {
		return Class.forName(className);
	}
}
