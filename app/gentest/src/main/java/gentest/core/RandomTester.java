/**
 * Copyright TODO
 */
package gentest.core;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Guice;
import com.google.inject.Injector;

import gentest.core.commons.utils.GenTestUtils;
import gentest.core.data.MethodCall;
import gentest.core.data.Sequence;
import gentest.injection.GentestModules;
import gentest.injection.TestcaseGenerationScope;
import sav.common.core.ModuleEnum;
import sav.common.core.Pair;
import sav.common.core.SavException;
import sav.common.core.utils.Randomness;

/**
 * @author LLT
 *
 */
public class RandomTester implements ITester {
	private static final Logger log = LoggerFactory.getLogger(RandomTester.class);
	private GentestModules injectorModule;
	private GentestListener listener;
	// max length of joined methods.
	private int queryMaxLength;
	private int testPerQuery;
	private int numberOfTcs;
	private long methodExecTimeout;
	
	public RandomTester(int queryMaxLength, int testPerQuery,
			int numberOfTcs, ClassLoader prjClassLoader) {
		this.queryMaxLength = queryMaxLength;
		this.testPerQuery = testPerQuery;
		this.numberOfTcs = numberOfTcs;
		injectorModule = new GentestModules(prjClassLoader);
	}
	
	public RandomTester(int queryMaxLength, int testPerQuery,
			int numberOfTcs) {
		this(queryMaxLength, testPerQuery, numberOfTcs, GenTestUtils.getDefaultClassLoader());
	}
	
	/**
	 * generate test sequences from seed methods.
	 * The result will be in the order passTcs, failTcs;
	 */
	@Override
	public Pair<List<Sequence>, List<Sequence>> test(
			List<MethodCall> methodcalls) throws SavException {
		injectorModule.setMethodExecTimeout(methodExecTimeout);
		injectorModule.enter(TestcaseGenerationScope.class);
		List<Sequence> passTcs = new ArrayList<Sequence>();
		List<Sequence> failTcs = new ArrayList<Sequence>();
		TestcaseGenerator tcGenerator = getTestcaseGenerator();
		int testsOnQuery = testPerQuery;
		List<MethodCall> query = null;
		removeInvalidMethodCalls(methodcalls);
		if (methodcalls.isEmpty()) {
			throw new SavException("No testable method is found!", ModuleEnum.TESTCASE_GENERATION);
		}
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

	private void removeInvalidMethodCalls(List<MethodCall> methodcalls) {
		Iterator<MethodCall> it = methodcalls.iterator();
		List<Method> invalidMethods = new ArrayList<Method>(methodcalls.size());
		for (; it.hasNext(); ) {
			Method method = it.next().getMethod();
			for (Class<?> paramType : method.getParameterTypes()) {
				if (!Modifier.isPublic(paramType.getModifiers())) {
					log.debug("Invisible parameter type: {}", paramType);
					invalidMethods.add(method);
					it.remove();
					break;
				}
			}
		}
		if (!invalidMethods.isEmpty()) {
			if (invalidMethods.size() == 1) {
				log.info("Unable to test method: {}", invalidMethods);
			} else {
				log.info("Unable to test these methods: {}", invalidMethods);
			}
		}
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
	
	public GentestModules getInjectorModule() {
		return injectorModule;
	}
	
	public void setListener(GentestListener listener) {
		this.listener = listener;
	}
	
	public void setMethodExecTimeout(long methodExecTimeout) {
		this.methodExecTimeout = methodExecTimeout;
	}
}
