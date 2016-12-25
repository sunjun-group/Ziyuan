/**
 * Copyright TODO
 */
package gentest;

import gentest.core.RandomTester;
import gentest.core.data.MethodCall;
import gentest.core.data.Sequence;
import gentest.junit.TestsPrinter;

import java.io.FileNotFoundException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import sav.common.core.Pair;
import sav.common.core.SavException;
import sav.commons.testdata.BoundedStack;

/**
 * @author LLT
 *
 */
public class GentestTest {

	@Test
	public void test() throws SecurityException, SavException, FileNotFoundException {
		RandomTester tester = new RandomTester(5, 5, 100);
		List<Method> methods = new ArrayList<Method>();
		addMethods(methods, BoundedStack.class);
		Pair<List<Sequence>, List<Sequence>> result = tester.test(toMethodCalls(methods));
		TestsPrinter printer = new TestsPrinter("testdata.test.result.boundedstack", 
				"testdata.test.result.boundedstack", "test", "BoundedStack", "./src/test/java");
		printer.printTests(result);
	}

	private List<MethodCall> toMethodCalls(List<Method> methods) {
		List<MethodCall> methodCalls = new ArrayList<MethodCall>(methods.size());
		for (Method method : methods) {
			methodCalls.add(MethodCall.of(method, method.getDeclaringClass()));
		}
		return methodCalls;
	}

	private void addMethods(List<Method> methods, Class<?> clazz) {
		for (Method method : clazz.getDeclaredMethods()) {
			if (Modifier.isPublic(method.getModifiers())) {
				methods.add(method);
			}
		}
	}
}
 