/**
 * Copyright TODO
 */
package gentest;

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
import net.java.quickcheck.collection.Pair;

import org.junit.Test;

import sav.common.core.SavException;
import testdata.BoundedStack;
import testdata.Program;

/**
 * @author LLT
 *
 */
public class GentestTest {

	@Test
	public void test() throws SecurityException, SavException, FileNotFoundException {
		RandomTester tester = new RandomTester(5, 5);
		List<Method> methods = new ArrayList<Method>();
		addMethods(methods, BoundedStack.class);
		addMethods(methods, Program.class);
		Pair<List<Sequence>, List<Sequence>> result = tester.test(methods);
		JWriter jwriter = new JWriter();
		CompilationUnit cu1 = jwriter.write(result.getFirst());
		System.out.println("pass size: " + result.getFirst().size());
		System.out.println(cu1.toString());
		File file = new File(
				"D:/_1_Projects/LLT/Gentest/workspace/trunk/app/gentest/src/test/java/testdata/TestResultPass.java");
		PrintStream stream = new PrintStream(file);
		stream.println(cu1.toString());
		stream.close();
		System.out.println("---------------------------------------");
		System.out.println("fail size: " + result.getSecond().size());
		CompilationUnit cu2 = jwriter.write(result.getSecond());
		stream = new PrintStream(new File("D:/_1_Projects/LLT/Gentest/workspace/trunk/app/gentest/src/test/java/testdata/TestResultFail.java"));
		stream.println(cu2.toString());
		stream.close();
		System.out.println(cu2.toString());
	}

	/**
	 * @param methods
	 */
	private void addMethods(List<Method> methods, Class<?> clazz) {
		for (Method method : clazz.getDeclaredMethods()) {
			if (Modifier.isPublic(method.getModifiers())) {
				methods.add(method);
			}
		}
	}
}
