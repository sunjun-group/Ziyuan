/**
 * Copyright TODO
 */
package gentest;

import gentest.data.MethodCall;
import gentest.data.Sequence;
import japa.parser.ast.CompilationUnit;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import junit.JWriter;

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
		JWriter jwriter = new JWriter();
		CompilationUnit cu1 = jwriter.write(result.first());
		System.out.println("pass size: " + result.first().size());
		System.out.println(cu1.toString());
		String testResultFolder = this.getClass().getResource("/testdata/gentest").getPath();
		File file = new File(testResultFolder + "/pass/TestResultPass.java");
		PrintStream stream = new PrintStream(file);
		stream.println(cu1.toString());
		stream.close();
		System.out.println("---------------------------------------");
		System.out.println("fail size: " + result.second().size());
		CompilationUnit cu2 = jwriter.write(result.second());
		stream = new PrintStream(new File("D:/_1_Projects/LLT/Gentest/workspace/trunk/app/gentest/src/test/java/testdata/TestResultFail.java"));
		stream.println(cu2.toString());
		stream.close();
		System.out.println(cu2.toString());
	}
	
	
	
//	private File newTestFile(String targetClassName) {
//		String testResultFolder = this.getClass().getResource("/testdata/gentest").getPath();
//		File file = new File(testResultFolder + "/pass/TestResultPass.java");
//	}

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
 