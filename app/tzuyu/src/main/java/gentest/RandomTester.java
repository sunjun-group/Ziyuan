/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package gentest;

import gentest.dto.TesterResult;

import java.util.ArrayList;
import java.util.List;

import tester.IInstrumentor;
import tester.ITCGStrategy;
import tester.RandomTCGStrategy;
import tzuyu.engine.TzClass;
import tzuyu.engine.TzConfiguration;
import tzuyu.engine.iface.ITzManager;
import tzuyu.engine.instrument.TzuYuInstrumentor;
import tzuyu.engine.model.MethodInfo;
import tzuyu.engine.model.Sequence;
import tzuyu.engine.runtime.RMethod;
import tzuyu.engine.utils.Randomness;

/**
 * 
 * @author LLT
 *
 */
public class RandomTester {

	private ITCGStrategy tcg;

	private IInstrumentor instrumentor;

	public RandomTester(ITzManager<?> prjFactory) {
		tcg = prjFactory.getTCGStrategy();
		tcg = new RandomTCGStrategy(prjFactory);
		instrumentor = new TzuYuInstrumentor();
	}
	
	public TesterResult test(TzClass project, TzConfiguration config) {
		TesterResult testResult = new TesterResult();
		List<RMethod> methods = initMethods(project, config);

		while (!finish(config, testResult)) {
			List<RMethod> query = randomWalk(methods);
			Sequence stmtsSeq = generateStmtsSequence(query);
			boolean execRes = executeSequence(stmtsSeq);
			testResult.add(stmtsSeq, execRes);
		}
		return testResult;
	}

	private boolean executeSequence(Sequence stmtsSeq) {
		// TODO Auto-generated method stub
		return true;
	}

	/**
	 * generate a sequence of statements which initializes 
	 * instance of method declared class and 
	 * parameters for the method.  
	 */
	private Sequence generateStmtsSequence(List<RMethod> query) {
		RMethod method = Randomness.randomMember(query);
		for (Class<?> type : method.getInputTypes()) {
			
		}
		// TODO Auto-generated method stub
		return null;
	}

	private List<RMethod> randomWalk(List<RMethod> methods) {
		return methods;
	}

	private boolean finish(TzConfiguration config, TesterResult testResult) {
		return testResult.getTotal() >= config.getTcTotal(); 
	}

	/**
	 * prepare init methods to generate testcases. 
	 */
	private List<RMethod> initMethods(TzClass project, TzConfiguration config) {
		List<RMethod> methods = new ArrayList<RMethod>();
		// prepare methods for trace generation
		for (MethodInfo methodInfo : project.getTargetClassInfo().getMethods(
				config.isInheritedMethod())) {
			RMethod method = RMethod.getMethod(methodInfo.getMethod());
			methods.add(method);
		}
		return methods;
	}

	public void setProject(TzClass project) {
		tcg.setProject(project);
		instrumentor.setProject(project);
	}

}
