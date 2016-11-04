package learntest.main;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import org.jacop.core.Domain;
import org.jacop.core.IntDomain;

import com.google.inject.Guice;
import com.google.inject.Injector;

import gentest.builder.RandomTraceGentestBuilder;
import gentest.core.commons.utils.MethodUtils;
import gentest.core.data.MethodCall;
import gentest.core.data.Sequence;
import gentest.injection.GentestModules;
import gentest.injection.TestcaseGenerationScope;
import gentest.junit.TestsPrinter;
import learntest.gentest.TestSeqGenerator;
import learntest.util.LearnTestUtil;
import net.sf.javailp.Result;
import sav.common.core.Pair;
import sav.common.core.SavException;
import sav.commons.TestConfiguration;
import sav.strategies.dto.execute.value.ExecVar;

public class TestGenerator {
	
	private static String prefix = "test";
	
	
	
	@SuppressWarnings("rawtypes")
	public void genTest() throws ClassNotFoundException, SavException {
		RandomTraceGentestBuilder builder = new RandomTraceGentestBuilder(1);
		builder.queryMaxLength(1).testPerQuery(1);
		
//		Class clazz = Class.forName(LearnTestConfig.testClassName);
		Class clazz = LearnTestUtil.retrieveClass(LearnTestConfig.testClassName);
		builder.forClass(clazz).method(LearnTestConfig.testMethodName);
		
		//builder.forClass(Class.forName(LearnTestConfig.className));
		
		String testSourceFolder = LearnTestUtil.retrieveTestSourceFolder();
		
		System.currentTimeMillis();
		
		boolean isL2T = LearnTestConfig.isL2TApproach;
		TestsPrinter printer = new TestsPrinter(LearnTestConfig.getTestPackageName(isL2T), null/*LearnTestConfig.getTestPackageName(isL2T)*/, 
				prefix, LearnTestConfig.getSimpleClassName(), testSourceFolder);
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
	
	public void genTestAccordingToSolutions(List<Domain[]> solutions, List<ExecVar> vars) 
			throws ClassNotFoundException, SavException {
		MethodCall target = findTargetMethod();
		if (target == null) {
			return;
		}
		solutions = clean(solutions, vars.size());
		
		GentestModules injectorModule = new GentestModules();
		injectorModule.enter(TestcaseGenerationScope.class);
		Injector injector = Guice.createInjector(injectorModule);
		TestSeqGenerator generator = injector.getInstance(TestSeqGenerator.class);
		generator.setTarget(target);
		
		List<Sequence> sequences = new ArrayList<Sequence>();
		//int index = 0;
		for (Domain[] solution : solutions) {
			//sequences.add(generator.generateSequence(input, variables.get(index ++)));
			sequences.add(generator.generateSequence(solution, vars));
		}
		injectorModule.exit(TestcaseGenerationScope.class);
		
//		TestsPrinter printer = new TestsPrinter(LearnTestConfig.getResultedTestPackage(), null, 
//				prefix, LearnTestConfig.getSimpleClassName(), TestConfiguration.getTestScrPath(LearnTestConfig.MODULE));
		TestsPrinter printer = new TestsPrinter(LearnTestConfig.getResultedTestPackage(LearnTestConfig.isL2TApproach), null, 
				prefix, LearnTestConfig.getSimpleClassName(), LearnTestUtil.retrieveTestSourceFolder());
		printer.printTests(new Pair<List<Sequence>, List<Sequence>>(sequences, new ArrayList<Sequence>()));
	}
	
//	public String retrieveTestSourceFolder() {
//		IWorkspaceRoot myWorkspaceRoot = ResourcesPlugin.getWorkspace().getRoot();
//		IProject iProject = myWorkspaceRoot.getProject(LearnTestConfig.projectName);
//		IJavaProject javaProject = JavaCore.create(iProject);
//		
//		try {
//			for(IPackageFragmentRoot root: javaProject.getAllPackageFragmentRoots()){
//				if(root instanceof PackageFragmentRoot){
//					String name = root.getElementName();
//					if(name.equals("test")){
//						URI uri = root.getCorrespondingResource().getLocationURI();
//						String sourceFolderPath = uri.toString();
//						sourceFolderPath = sourceFolderPath.substring(6, sourceFolderPath.length());
//						
//						return sourceFolderPath;
//					}
//					
//					
//				}
//				
//			}
//		} catch (JavaModelException e) {
//			e.printStackTrace();
//		}
//		
//		
//		return null;
//	}
	
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
			if (((IntDomain) solution[i]).min() != ((IntDomain) r[i]).min()) {
				return false;
			}
		}
		return true;
	}

	public void genTestAccordingToInput(List<Result> inputs, 
			//List<Set<String>> variables
			//List<Variable> variables
			List<String> variables) throws ClassNotFoundException, SavException {
		MethodCall target = findTargetMethod();
		if (target == null) {
			return;
		}
		inputs = clean(inputs, variables);
		
		//TestSeqGenerator generator = new TestSeqGenerator();
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
//		Class<?> clazz = Class.forName(LearnTestConfig.testClassName);
		Class<?> clazz = LearnTestUtil.retrieveClass(LearnTestConfig.testClassName);
		Method method = MethodUtils.findMethod(clazz, LearnTestConfig.testMethodName);
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
	
}
