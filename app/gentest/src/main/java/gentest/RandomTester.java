/**
 * Copyright TODO
 */
package gentest;

import gentest.commons.utils.Randomness;
import gentest.data.MethodCall;
import gentest.data.Sequence;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import sav.common.core.SavException;

import net.java.quickcheck.collection.Pair;

/**
 * @author LLT
 *
 */
public class RandomTester {
	// max length of joined methods.
	private int queryMaxLength;
	private int testPerQuery;
	
	public RandomTester(int queryMaxLength, int testPerQuery) {
		this.queryMaxLength = queryMaxLength;
		this.testPerQuery = testPerQuery;
	}
	
	public Pair<List<Sequence>, List<Sequence>> test(
			List<Method> methods) throws SavException {
		List<Sequence> passTcs = new ArrayList<Sequence>();
		List<Sequence> failTcs = new ArrayList<Sequence>();
		TestcaseGenerator tcGenerator = new TestcaseGenerator();
		List<MethodCall> methodcalls = initMethodCalls(methods);
		int testsOnQuery = testPerQuery;
		List<MethodCall> query = null;
		while (!finish(passTcs.size(), failTcs.size())) {
			if (testsOnQuery >= testPerQuery) {
				query = randomWalk(methodcalls);
				testsOnQuery = 0;
			}
			Sequence testcase = tcGenerator.generateSequence(query);
			if (tcGenerator.getExecutor().isSuccessful()) {
				passTcs.add(testcase);
			} else {
				failTcs.add(testcase);
			}
			testsOnQuery++;
		}
		return new Pair<List<Sequence>, List<Sequence>>(
				passTcs, failTcs);
	}

	private List<MethodCall> initMethodCalls(List<Method> methods) {
		List<MethodCall> methodCalls = new ArrayList<MethodCall>(methods.size());
		for (Method method : methods) {
			methodCalls.add(MethodCall.of(method));
		}
		return methodCalls;
	}

	private boolean finish(int passTcSize, int failTcSize) {
		return passTcSize + failTcSize >= 100; 
	}

	private <T>List<T> randomWalk(List<T> methodcalls) {
		List<T> query = new ArrayList<T>();
		int traceLength = Randomness.nextRandomInt(queryMaxLength) + 1;
		for (int i = 0; i < traceLength; i++) {
			T nextMethodCall = Randomness.randomMember(methodcalls);
			query.add(nextMethodCall);
		}
		return query;
	}
}
