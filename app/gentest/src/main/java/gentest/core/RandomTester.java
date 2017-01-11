/**
 * Copyright TODO
 */
package gentest.core;

import gentest.core.data.MethodCall;
import gentest.core.data.Sequence;
import gentest.injection.GentestModules;
import gentest.injection.TestcaseGenerationScope;

import java.util.ArrayList;
import java.util.List;

import sav.common.core.Pair;
import sav.common.core.SavException;
import sav.common.core.utils.Randomness;

import com.google.inject.Guice;
import com.google.inject.Injector;

/**
 * @author LLT
 *
 */
public class RandomTester implements ITester {
	private GentestModules injectorModule = new GentestModules();
	private GentestListener listener;
	// max length of joined methods.
	private int queryMaxLength;
	private int testPerQuery;
	private int numberOfTcs;
	
	public RandomTester(int queryMaxLength, int testPerQuery,
			int numberOfTcs) {
		this.queryMaxLength = queryMaxLength;
		this.testPerQuery = testPerQuery;
		this.numberOfTcs = numberOfTcs;
	}
	
	/**
	 * generate test sequences from seed methods.
	 * The result will be in the order passTcs, failTcs;
	 */
	@Override
	public Pair<List<Sequence>, List<Sequence>> test(
			List<MethodCall> methodcalls) throws SavException {
		injectorModule.enter(TestcaseGenerationScope.class);
		
		List<Sequence> passTcs = new ArrayList<Sequence>();
		List<Sequence> failTcs = new ArrayList<Sequence>();
		TestcaseGenerator tcGenerator = getTestcaseGenerator();
		int testsOnQuery = testPerQuery;
		List<MethodCall> query = null;
		while (!finish(passTcs.size(), failTcs.size())) {
			if (testsOnQuery >= testPerQuery) {
				query = randomWalk(methodcalls);
				testsOnQuery = 0;
			}
			Sequence testcase = tcGenerator.generateSequence(query);
			if (listener != null) {
				listener.onFinishGenerateSeq(tcGenerator, testcase);
			}
			if (tcGenerator.getExecutor().isSuccessful()) {
				passTcs.add(testcase);
			} else {
				failTcs.add(testcase);
			}
			testsOnQuery++;
		}
		
		injectorModule.exit(TestcaseGenerationScope.class);
		return new Pair<List<Sequence>, List<Sequence>>(
				passTcs, failTcs);
	}

	private TestcaseGenerator getTestcaseGenerator() {
		Injector injector = Guice.createInjector(injectorModule);
		return injector.getInstance(TestcaseGenerator.class);
	}

	private boolean finish(int passTcSize, int failTcSize) {
		return passTcSize + failTcSize >= numberOfTcs; 
	}

	protected <T>List<T> randomWalk(List<T> methodcalls) {
		int traceLength = Randomness.nextInt(queryMaxLength) + 1;
		List<T> query = new ArrayList<T>(traceLength);
		for (int i = 0; i < traceLength; i++) {
			T nextMethodCall = Randomness.randomMember(methodcalls);
			query.add(nextMethodCall);
		}
		return query;
	}
	
	public void setListener(GentestListener listener) {
		this.listener = listener;
	}
}
