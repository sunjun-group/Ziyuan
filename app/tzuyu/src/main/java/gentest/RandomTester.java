/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package gentest;

import java.util.ArrayList;
import java.util.List;

import tester.ITCGStrategy;
import tester.RandomTCGStrategy;
import tzuyu.engine.TzClass;
import tzuyu.engine.TzConfiguration;
import tzuyu.engine.iface.ITzManager;
import tzuyu.engine.model.Action;
import tzuyu.engine.model.MethodInfo;
import tzuyu.engine.model.Query;
import tzuyu.engine.model.Sequence;
import tzuyu.engine.model.Trace;
import tzuyu.engine.model.TzuYuAction;
import tzuyu.engine.runtime.RMethod;
import tzuyu.engine.utils.Pair;
import tzuyu.engine.utils.Randomness;

/**
 * 
 * @author LLT
 *
 */
public class RandomTester {
	private ITCGStrategy tcg;
	private int traceMaxLength;
	private ITzManager<?> manager;
	
	public RandomTester(ITzManager<?> prjFactory) {
		tcg = prjFactory.getTCGStrategy();
		tcg = new RandomTCGStrategy(prjFactory);
		this.manager = prjFactory;
	}
	
	public Pair<List<Sequence>, List<Sequence>> test(TzClass project,
			TzConfiguration config) {
		traceMaxLength = config.getTraceMaxLength();
		tcg.setProject(project);
		List<RMethod> methods = initMethods(project, config);

		try {
			while (!finish(config)) {
				List<RMethod> query = randomWalk(methods);
				generateStmtsSequence(query);
			}
		} catch (InterruptedException e) {
			// do nothing
		}
		return getallTestcases();
	}

	private Pair<List<Sequence>, List<Sequence>> getallTestcases() {
		return tcg.getAllTestSequences(true, true);
	}

	private void generateStmtsSequence(List<RMethod> methods) {
		List<Action> actions = new ArrayList<Action>(methods.size());
		for (RMethod method : methods) {
			actions.add(TzuYuAction.fromStatmentKind(method));
		}
		Trace trace = new Trace(actions);
		Query query = new Query(trace);
		tcg.generate(query);
	}

	private List<RMethod> randomWalk(List<RMethod> methods) {
		List<RMethod> trace = new ArrayList<RMethod>();
		int traceLength = Randomness.nextRandomInt(traceMaxLength);
		for (int i = 0; i < traceLength; i++) {
			RMethod nextMethodCall = Randomness.randomMember(methods);
			trace.add(nextMethodCall);
		}
		return trace;
	}

	private boolean finish(TzConfiguration config) throws InterruptedException {
		manager.checkProgress();
		return tcg.countTcs(null) >= config.getNumberOfTcs();
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

}
