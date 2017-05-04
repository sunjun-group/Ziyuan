package learntest.main;

import java.io.File;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import org.jacop.core.Domain;
import org.jacop.floats.core.FloatDomain;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;

import gentest.builder.RandomTraceGentestBuilder;
import gentest.core.commons.utils.MethodUtils;
import gentest.core.data.MethodCall;
import gentest.core.data.Sequence;
import gentest.injection.GentestModules;
import gentest.injection.TestcaseGenerationScope;
import gentest.junit.FileCompilationUnitPrinter;
import gentest.junit.TestsPrinter;
import gentest.junit.TestsPrinter.PrintOption;
import learntest.core.gentest.ITestGenerator;
import learntest.gentest.TestSeqGenerator;
import learntest.util.LearnTestUtil;
import net.sf.javailp.Result;
import sav.common.core.Pair;
import sav.common.core.SavException;
import sav.commons.TestConfiguration;
import sav.strategies.dto.execute.value.ExecVar;

public class TestGenerator implements ITestGenerator {
	private static int NUMBER_OF_INIT_TEST = 1;
	private static String prefix = "test";
	
	/**
	 * 
	 * @throws ClassNotFoundException
	 * @throws SavException
	 */
	public void genTest() throws ClassNotFoundException, SavException {
		String mSig = LearnTestUtil.getMethodWthSignature(LearnTestConfig.targetClassName, 
				LearnTestConfig.targetMethodName, LearnTestConfig.getMethodLineNumber());
		
		Class<?> clazz = LearnTestUtil.retrieveClass(LearnTestConfig.targetClassName);
		RandomTraceGentestBuilder builder = new RandomTraceGentestBuilder(NUMBER_OF_INIT_TEST)
										.classLoader(LearnTestUtil.getPrjClassLoader())
										.queryMaxLength(1)
										.testPerQuery(1)
										.forClass(clazz)
										.method(mSig);
		String testSourceFolder = LearnTestUtil.retrieveTestSourceFolder();
		
		boolean isL2T = LearnTestConfig.isL2TApproach;
		String packageName = LearnTestConfig.getTestPackageName(isL2T);
		String simpleClassName = LearnTestConfig.getSimpleClassName();
		TestsPrinter printer = new TestsPrinter(packageName, null, prefix, simpleClassName, testSourceFolder);
		Pair<List<Sequence>, List<Sequence>> pair = builder.generate();
		printer.printTests(pair);
	}
	
	public GentestResult genTestAccordingToSolutions(List<Domain[]> solutions, List<ExecVar> vars) 
			throws ClassNotFoundException, SavException {
		return genTestAccordingToSolutions(solutions, vars, PrintOption.OVERRIDE);
	}
	
	/**
	 * 
	 * @param solutions
	 * @param vars
	 * @param printOption whether to append existing test file or create a new one.
	 * @return 
	 */
	public GentestResult genTestAccordingToSolutions(List<Domain[]> solutions, List<ExecVar> vars, PrintOption printOption) 
			throws ClassNotFoundException, SavException {
		MethodCall target = findTargetMethod();
		if (target == null) {
			return null;
		}
		solutions = clean(solutions, vars.size());
		
		GentestModules injectorModule = new GentestModules();
		injectorModule.enter(TestcaseGenerationScope.class);
		List<Module> modules = new ArrayList<Module>();
		modules.add(injectorModule);
		Injector injector = Guice.createInjector(modules);
		TestSeqGenerator generator = injector.getInstance(TestSeqGenerator.class);
		generator.setTarget(target);
		
		List<Sequence> sequences = new ArrayList<Sequence>();
		//int index = 0;
		for (Domain[] solution : solutions) {
			//sequences.add(generator.generateSequence(input, variables.get(index ++)));
			sequences.add(generator.generateSequence(solution, vars));
		}
		injectorModule.exit(TestcaseGenerationScope.class);

		TestsPrinter printer = new TestsPrinter(LearnTestConfig.getResultedTestPackage(LearnTestConfig.isL2TApproach),
				null, prefix, LearnTestConfig.getSimpleClassName(), LearnTestUtil.retrieveTestSourceFolder(),
				printOption);
		List<String> junitClassName = printer.printTests(Pair.of(sequences, new ArrayList<Sequence>(0)));
		return new GentestResult(junitClassName,
				((FileCompilationUnitPrinter) printer.getCuPrinter()).getGeneratedFiles());
	}
	
	private List<Domain[]> clean(List<Domain[]> solutions, int size) {
		List<Domain[]> res = new ArrayList<Domain[]>();
		for (Domain[] solution : solutions) {
			boolean dup = false;
			for (Domain[] r : res) {
				if(duplicate(solution, r, size)) {
					dup = true;
					break;
				}
			}
			if (!dup) {
				res.add(solution);
			}
		}
		return res;
	}

	private boolean duplicate(Domain[] solution, Domain[] r, int size) {
		for (int i = 0; i < size; i++) {
			if (((FloatDomain) solution[i]).min() != ((FloatDomain) r[i]).min()) {
				return false;
			}
		}
		return true;
	}

	public void genTestAccordingToInput(List<Result> inputs, List<String> variables)
			throws ClassNotFoundException, SavException {
		MethodCall target = findTargetMethod();
		if (target == null) {
			return;
		}
		inputs = clean(inputs, variables);
		
		GentestModules injectorModule = new GentestModules();
		injectorModule.enter(TestcaseGenerationScope.class);
		Injector injector = Guice.createInjector(injectorModule);
		TestSeqGenerator generator = injector.getInstance(TestSeqGenerator.class);
		generator.setTarget(target);
		
		List<Sequence> sequences = new ArrayList<Sequence>();
		//int index = 0;
		for (Result input : inputs) {
			//sequences.add(generator.generateSequence(input, variables.get(index ++)));
			sequences.add(generator.generateSequence(input, variables));
		}
		injectorModule.exit(TestcaseGenerationScope.class);
		
		TestsPrinter printer = new TestsPrinter(LearnTestConfig.getResultedTestPackage(LearnTestConfig.isL2TApproach), null, 
				prefix, LearnTestConfig.getSimpleClassName(), TestConfiguration.getTestScrPath(LearnTestConfig.MODULE));
		printer.printTests(new Pair<List<Sequence>, List<Sequence>>(sequences, new ArrayList<Sequence>()));
	}

	private MethodCall findTargetMethod() throws ClassNotFoundException {
		return findTargetMethod(LearnTestConfig.targetClassName, LearnTestConfig.targetMethodName);
	}
	
	private MethodCall findTargetMethod(String targetClassName, String targetmethodName) throws ClassNotFoundException {
		Class<?> clazz = LearnTestUtil.retrieveClass(targetClassName);
		Method method = MethodUtils.findMethod(clazz, targetmethodName);
		if (Modifier.isPublic(method.getModifiers())) {
			return MethodCall.of(method, clazz);
		}
		return null;
	}
	
	private List<Result> clean(List<Result> inputs, List<String> variables) {
		List<Result> res = new ArrayList<Result>();
		for (Result input : inputs) {
			boolean flag = true;
			for (Result result : res) {
				if (duplicate(input, result, variables)) {
					flag = false;
					break;
				}
			}
			if (flag) {
				res.add(input);
			}
		}
		return res;
	}

	private boolean duplicate(Result input, Result result, List<String> variables) {
		for (String var : variables) {
			if (input.get(var) == null) {
				if (result.get(var) != null) {
					return false;
				}
			} else if (!input.get(var).equals(result.get(var))) {
				return false;
			}
		}
		return true;
	}
	
	public static class GentestResult {
		private List<String> junitClassNames;
		private List<File> junitfiles;

		public GentestResult(List<String> junitClassNames, List<File> generatedFiles) {
			this.junitClassNames = junitClassNames;
			this.junitfiles = generatedFiles;
		}

		public List<String> getJunitClassNames() {
			return junitClassNames;
		}

		public List<File> getJunitfiles() {
			return junitfiles;
		}
	}
}
