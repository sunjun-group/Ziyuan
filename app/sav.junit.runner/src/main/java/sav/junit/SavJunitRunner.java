/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package sav.junit;

import java.util.List;

import org.junit.runner.Description;
import org.junit.runner.JUnitCore;
import org.junit.runner.Request;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;

import sav.utils.ExecutionTimerUtils;
import sav.utils.IExecutionTimer;
import sav.utils.TestRunner;

/**
 * @author LLT
 *
 */
public class SavJunitRunner implements TestRunner {
	private static final String SUCCESS_MSG = "no fail";
	private boolean successful = false;
	private String failureMessage = SUCCESS_MSG;
	private JUnitCore jUnitCore;
	private String className;
	private String methodName;
	private long curThreadId = -1;
	
	public SavJunitRunner(){
		jUnitCore = new JUnitCore();
		jUnitCore.addListener(new RunListener() {
			@Override
			public void testStarted(Description description) throws Exception {
				$testStarted(description.getClassName(), description.getMethodName());
			}
			
			@Override
			public void testFinished(Description description) throws Exception {
				$testFinished(description.getClassName(), description.getMethodName());
			}
		});
	}
	
	public static void main(String[] args){
		JunitRunnerParameters params = JunitRunnerParameters.parse(args);
		IExecutionTimer timer = ExecutionTimerUtils.getExecutionTimer(params.getTimeout());
		SavJunitRunner junitRunner = executeTestcases(params, timer);
		junitRunner.$exitProgram("SavJunitRunner finished!");
	}

	public static SavJunitRunner executeTestcases(JunitRunnerParameters params, IExecutionTimer timer) {
		SavJunitRunner junitRunner = new SavJunitRunner();
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
		return junitRunner;
	}
	
	@Override
	public void run() {
		long start = System.currentTimeMillis();
		Request request;
		try {
			curThreadId = Thread.currentThread().getId();
			request = Request.method(Class.forName(className), methodName);
			Result result = jUnitCore.run(request);
			successful = result.wasSuccessful();
			
			List<Failure> failures = result.getFailures();
			for(Failure failure: failures){
				Throwable exception = failure.getException();
				this.failureMessage = exception.getMessage();
			}
			
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		
		System.out.println("TESTCASE RUNNING TIME: " + (System.currentTimeMillis() - start));
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
	
	public void $exitProgram(String resultMsg) {
		// for agent part.
	}

	private void $exitTest(String className, String methodName, String testResultMsg, Long curThreadId) {
		// for agent part.
	}

}
