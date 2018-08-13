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
import sav.utils.ExecutionTimerUtils;
import sav.utils.IExecutionTimer;
import sav.utils.TestRunner;

/**
 * @author LLT
 *
 */
public class SavSimpleRunner implements TestRunner {
	private static final String SUCCESS_MSG = "no fail";
	protected boolean successful = false;
	protected String failureMessage = SUCCESS_MSG;
	protected String className;
	protected String methodName;
	protected long curThreadId = -1;
	
	public SavSimpleRunner(){
		
	}
	
	public static void main(String[] args){
		long currentTime = System.currentTimeMillis();
        long vmStartTime = ManagementFactory.getRuntimeMXBean().getStartTime();
        System.out.println("Startup time: " + (currentTime - vmStartTime));
		JunitRunnerParameters params = JunitRunnerParameters.parse(args);
		IExecutionTimer timer = ExecutionTimerUtils.getExecutionTimer(params.getTimeout() >= 0);
		SavSimpleRunner junitRunner = executeTestcases(params, timer);
		junitRunner.$exitProgram("SavJunitRunner finished!");
	}

	public static SavSimpleRunner executeTestcases(JunitRunnerParameters params, IExecutionTimer timer) {
		SavSimpleRunner junitRunner = new SavSimpleRunner();
		for (String[] tc : params.getTestcases()) {
			junitRunner.curThreadId = -1;
			junitRunner.className = tc[0];
			junitRunner.methodName = tc[1];
			timer.run(junitRunner, params.getTimeout());
			System.out.println("is successful? " + junitRunner.successful);
			System.out.println("failure message: " + junitRunner.failureMessage);
			junitRunner.$exitTest(junitRunner.successful + ";" + junitRunner.failureMessage, junitRunner.className,
					junitRunner.methodName, junitRunner.curThreadId);
		}
		return junitRunner;
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
			// ignore
			failureMessage = e.getMessage();
		}
		$testFinished(className, methodName);
	}
	
	@Override
	public void onTimeout() {
		System.out.println("TEST TIME OUT!!");
		this.failureMessage = "time out!";
	}
	
	protected void $testFinished(String className, String methodName) {
		// for agent part.
	}

	protected void $testStarted(String className, String methodName) {
		// for agent part.
	}
	
	protected void $exitProgram(String resultMsg) {
		// for agent part.
	}

	protected void $exitTest(String className, String methodName, String testResultMsg, Long curThreadId) {
		// for agent part.
	}

}
