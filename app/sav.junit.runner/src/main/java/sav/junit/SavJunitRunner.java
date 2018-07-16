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

import sav.common.core.utils.CountDownExecutionTimer;
import sav.common.core.utils.IExecutionTimer;

/**
 * @author LLT
 *
 */
public class SavJunitRunner implements Runnable {
	private boolean successful = false;
	private String failureMessage = "no fail";
	private JUnitCore jUnitCore;
	private String className;
	private String methodName;
	
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
		IExecutionTimer timer;
		if (params.getTimeout() <= 0) {
			timer = new IExecutionTimer() {
				
				@Override
				public boolean run(Runnable target, long timeout) {
					target.run();
					return true;
				}
			};
		} else {
			timer = new CountDownExecutionTimer();
		}
		SavJunitRunner junitRunner = new SavJunitRunner();
		for (String[] tc : params.getTestcases()) {
			junitRunner.className = tc[0];
			junitRunner.methodName = tc[1];
			timer.run(junitRunner, params.getTimeout());
			junitRunner.$testFinished(junitRunner.className, junitRunner.methodName);
		}
		junitRunner.$exitProgram("SavJunitRunner finished!");
	}
	
	@Override
	public void run() {
		Request request;
		try {
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
		
		System.currentTimeMillis();
		System.out.println("is successful? " + successful);
		System.out.println(this.failureMessage);
		$exitTest(className, methodName, successful + ";" + this.failureMessage);
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

	private void $exitTest(String className, String methodName, String testResultMsg) {
		// for agent part.
	}
}
