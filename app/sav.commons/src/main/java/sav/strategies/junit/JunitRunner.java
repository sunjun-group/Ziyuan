/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package sav.strategies.junit;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.runner.Request;
import org.junit.runner.notification.Failure;

import sav.common.core.Pair;
import sav.common.core.utils.CollectionUtils;
import sav.common.core.utils.JunitUtils;
import sav.strategies.dto.BreakPoint;
import sav.strategies.vm.ProgramArgumentBuilder;

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
		JunitResult result = runTestcases(params);
		if (params.getDestfile() != null) {
			File file = new File(params.getDestfile());
			result.save(file);
		}
	}

	public static JunitResult runTestcases(JunitRunnerParameters params)
			throws ClassNotFoundException, IOException {
		System.out.println("Run testcases: ");
		List<Pair<String, String>> classMethods = JunitUtils.toPair(params
				.getClassMethods());
		RequestExecution requestExec = new RequestExecution();
		JunitResult result = new JunitResult();
		List<Failure> falures = new ArrayList<Failure>();
		for (Pair<String, String> classMethod : classMethods) {
			Request request = toRequest(classMethod);
			if (request == null) {
				continue;
			}
			requestExec.setRequest(request);
			requestExec.run();
			falures.addAll(requestExec.getFailures());
			boolean isPass = requestExec.getFailures().isEmpty();
			result.addResult(classMethod, isPass);
			System.out.println(classMethod + ", result: " + isPass);
		}
		extractBrkpsFromTrace(falures, params, result.getFailureTraces());
//		extractBrkpsFromTrace(falures, params.getTestingClassNames(),
//				result.getFailureTraces());
		return result;
	}
	
	private static void extractBrkpsFromTrace(List<Failure> falures,
			JunitRunnerParameters params, Set<BreakPoint> bkps) {
		Set<String> acceptedClasses = new HashSet<String>();
		List<String> testingClassNames = CollectionUtils.nullToEmpty(params
				.getTestingClassNames());
		List<String> testingPkgs = CollectionUtils.nullToEmpty(params
				.getTestingPkgs());
		System.out.println("FailureTrace: ");
		for (Failure failure : falures) {
			for (StackTraceElement trace : failure.getException()
					.getStackTrace()) {
				String className = trace.getClassName();
				int lineNumber = trace.getLineNumber();
				System.out.println(String
						.format("%s@%s", className, lineNumber));
				if (className == null) {
					continue;
				}
				if (acceptedClasses.contains(className)
						|| testingClassNames.contains(className)) {
					bkps.add(new BreakPoint(className, trace.getMethodName(),
							lineNumber));
					continue;
				}
				for (String pkg : testingPkgs) {
					if (className.startsWith(pkg)) {
						acceptedClasses.add(className);
						bkps.add(new BreakPoint(className, trace
								.getMethodName(), lineNumber));
						break;
					}
				}
			}
		}
	}

	private static void extractBrkpsFromTrace(List<Failure> failureTrace,
			List<String> testingClassNames, Set<BreakPoint> failureTraces) {
		System.out.println("FailureTrace: ");
		for (Failure failure : failureTrace) {
			for (StackTraceElement trace : failure.getException()
					.getStackTrace()) {
				String className = trace.getClassName();
				int lineNumber = trace.getLineNumber();
				System.out.println(String.format("%s@%s", className, lineNumber));
				if (className != null
						&& (testingClassNames == null || testingClassNames
								.contains(className))) {
					failureTraces.add(new BreakPoint(className,
							trace.getMethodName(), lineNumber));
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
	
	private static Class<?> loadClass(String className) throws ClassNotFoundException {
		return Class.forName(className);
	}
	
	public static class JunitRunnerProgramArgBuilder extends ProgramArgumentBuilder {
		public JunitRunnerProgramArgBuilder methods(List<String> classMethods){
			addArgument(JunitRunnerParameters.CLASS_METHODS, classMethods);
			return this;
		}
		
		public JunitRunnerProgramArgBuilder method(String classMethod){
			addArgument(JunitRunnerParameters.CLASS_METHODS, classMethod);
			return this;
		}
		
		public JunitRunnerProgramArgBuilder destinationFile(String destFile){
			addArgument(JunitRunnerParameters.DEST_FILE, destFile);
			return this;
		}
		
		public JunitRunnerProgramArgBuilder testClassNames(List<String> testClassNames){
			addArgument(JunitRunnerParameters.TESTING_CLASS_NAMES, testClassNames);
			return this;
		}
	}
}
