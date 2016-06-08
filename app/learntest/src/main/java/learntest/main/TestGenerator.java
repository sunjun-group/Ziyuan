package learntest.main;

import gentest.builder.RandomTraceGentestBuilder;
import gentest.junit.TestsPrinter;
import gentest.main.GentestConstants;
import sav.common.core.SavException;
import sav.commons.TestConfiguration;

public class TestGenerator {
	
	private static String prefix = "test";
	
	public void gentest() throws ClassNotFoundException, SavException {
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
	
}
