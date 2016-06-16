package learntest.main;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.google.inject.Guice;
import com.google.inject.Injector;

import gentest.builder.RandomTraceGentestBuilder;
import gentest.core.data.Sequence;
import gentest.injection.GentestModules;
import gentest.injection.TestcaseGenerationScope;
import gentest.junit.TestsPrinter;
import gentest.main.GentestConstants;
import learntest.gentest.TestSeqGenerator;
import net.sf.javailp.Result;
import sav.common.core.Pair;
import sav.common.core.SavException;
import sav.commons.TestConfiguration;

public class TestGenerator {
	
	private static String prefix = "test";
	
	public void genTest() throws ClassNotFoundException, SavException {
		RandomTraceGentestBuilder builder = new RandomTraceGentestBuilder(40);
		builder.queryMaxLength(1).testPerQuery(GentestConstants.DEFAULT_TEST_PER_QUERY);
		builder.forClass(Class.forName(LearnTestConfig.className)).method(LearnTestConfig.methodName);
		TestsPrinter printer = new TestsPrinter(LearnTestConfig.pkg, LearnTestConfig.pkg, 
				prefix, LearnTestConfig.typeName, TestConfiguration.getTestScrPath(LearnTestConfig.MODULE));
		printer.printTests(builder.generate());
		
		/*final FileCompilationUnitPrinter cuPrinter = new FileCompilationUnitPrinter(
				appClasspath.getSrc());
		final List<String> junitClassNames = new ArrayList<String>();
		TestsPrinter printer = new TestsPrinter(LearnTestConfig.pkg, LearnTestConfig.pkg, prefix, LearnTestConfig.typeName,
				new ICompilationUnitPrinter() {
					
					@Override
					public void print(List<CompilationUnit> compilationUnits) {
						for (CompilationUnit cu : compilationUnits) {
							junitClassNames.add(ClassUtils.getCanonicalName(cu
									.getPackage().getName().getName(), cu
									.getTypes().get(0).getName()));
						}
						cuPrinter.print(compilationUnits);
					}
				});
		printer.printTests(builder.generate());
		List<File> generatedFiles = cuPrinter.getGeneratedFiles();
		
		Recompiler recompiler = new Recompiler(new VMConfiguration(appClasspath));
		recompiler.recompileJFile(appClasspath.getTestTarget(), generatedFiles);*/
	}
	
	public void genTestAccordingToInput(List<Result> inputs, List<Set<String>> variables) throws SavException {
		//TestSeqGenerator generator = new TestSeqGenerator();
		GentestModules injectorModule = new GentestModules();
		injectorModule.enter(TestcaseGenerationScope.class);
		TestSeqGenerator generator = getTestcaseGenerator(injectorModule);
		List<Sequence> sequences = new ArrayList<Sequence>();
		int index = 0;
		for (Result input : inputs) {
			sequences.add(generator.generateSequence(input, variables.get(index ++)));
		}
		injectorModule.exit(TestcaseGenerationScope.class);
		TestsPrinter printer = new TestsPrinter(LearnTestConfig.resPkg, null, 
				prefix, LearnTestConfig.typeName, TestConfiguration.getTestScrPath(LearnTestConfig.MODULE));
		printer.printTests(new Pair<List<Sequence>, List<Sequence>>(sequences, new ArrayList<Sequence>()));
	}
	
	private TestSeqGenerator getTestcaseGenerator(GentestModules injectorModule) {
		Injector injector = Guice.createInjector(injectorModule);
		return injector.getInstance(TestSeqGenerator.class);
	}
	
}
