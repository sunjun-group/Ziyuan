/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package sav.strategies.junit.jacocoMock;

import java.io.IOException;
import java.lang.reflect.Method;

import sav.common.core.Pair;
import sav.common.core.utils.ClassUtils;
import sav.common.core.utils.ExecutionTimer;
import sav.common.core.utils.JunitUtils;
import sav.strategies.junit.JunitResult;
import sav.strategies.junit.JunitRunnerParameters;

/**
 * @author LLT
 * this classRunner is for simple generated test which does not require any special 
 * junit services other than just invoke the test method.
 * using this for execution time optimization.
 */
public class JaCoCoMockSimpleRunner extends JaCoCoMockJunitRunner {
	
	@Override
	public JunitResult runTestcases(JunitRunnerParameters params) throws ClassNotFoundException, IOException {
		System.out.println("RunTestcases:");
		ExecutionTimer executionTimer = getExecutionTimer(params.getTimeout());
		for (String classMethodStr : params.getClassMethods()) {
			System.out.println(classMethodStr + "...");
			Pair<String, String> classMethod = JunitUtils.toPair(classMethodStr);
			final Class<?> clazz = Class.forName(classMethod.a);
			final Method method = ClassUtils.loockupMethod(clazz, classMethod.b);
			executionTimer.run(new Runnable() {
				
				@Override
				public void run() {
					try {
						method.invoke(clazz.newInstance());
					} catch (Exception e) {
						// ignore
						e.printStackTrace();
					} 
				}
			});
			onFinishTestCase(classMethodStr, null);
		}
		return null;
	}
	
}
