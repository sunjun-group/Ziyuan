/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package sav.junit;

import java.lang.management.ManagementFactory;
import java.lang.reflect.Method;

import sav.utils.ClassUtils;
import sav.utils.CountDownExecutionTimer;
import sav.utils.IExecutionTimer;
import sav.utils.TestRunner;

/**
 * @author LLT
 *
 */
public class SavSimpleRunner implements TestRunner {
	private static final String SUCCESS_MSG = "no fail";
	private boolean successful = false;
	private String failureMessage = SUCCESS_MSG;
	private String className;
	private String methodName;
	private long curThreadId = -1;
	
	public SavSimpleRunner(){
		
	}
	
	public static void main(String[] args){
		long currentTime = System.currentTimeMillis();
        long vmStartTime = ManagementFactory.getRuntimeMXBean().getStartTime();
        System.out.println("Startup time: " + (currentTime - vmStartTime));
		JunitRunnerParameters params = JunitRunnerParameters.parse(args);
		IExecutionTimer timer;
		if (params.getTimeout() <= 0) {
			timer = new IExecutionTimer() {
				
				@Override
				public boolean run(TestRunner target, long timeout) {
					target.run();
					return true;
				}
			};
		} else {
			timer = new CountDownExecutionTimer();
		}
		SavSimpleRunner junitRunner = new SavSimpleRunner();
		for (String[] tc : params.getTestcases()) {
			junitRunner.curThreadId = -1;
			junitRunner.className = tc[0];
			junitRunner.methodName = tc[1];
			timer.run(junitRunner, params.getTimeout());
			System.out.println("is successful? " + junitRunner.successful);
			System.out.println(junitRunner.failureMessage);
			junitRunner.$exitTest(junitRunner.successful + ";" + junitRunner.failureMessage, junitRunner.className,
					junitRunner.methodName, junitRunner.curThreadId);
		}
		junitRunner.$exitProgram("SavJunitRunner finished!");
	}
	
	@Override
	public void run() {
		try {
			curThreadId = Thread.currentThread().getId();
			final Class<?> clazz = Class.forName(className);
			final Method method = ClassUtils.loockupMethod(clazz, methodName);
			$testStarted(className, methodName);
			method.invoke(clazz.newInstance());
		} catch (Exception e) {
			e.printStackTrace();
		}
		$testFinished(className, methodName);
	}
	
	@Override
	public void onTimeout() {
		this.failureMessage = "time out!";
	}
	
	private void $testFinished(String className, String methodName) {
		// for agent part.
	}

	private void $testStarted(String className, String methodName) {
		// for agent part.
	}
	
	private void $exitProgram(String resultMsg) {
		// for agent part.
	}

	private void $exitTest(String className, String methodName, String testResultMsg, Long curThreadId) {
		// for agent part.
	}

}
