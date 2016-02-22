package microbat.evaluation.junit;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import microbat.evaluation.TraceModelConstructor;
import microbat.evaluation.mutation.MutationPointChecker;
import microbat.model.BreakPoint;
import microbat.model.trace.Trace;
import microbat.util.JTestUtil;
import microbat.util.JavaUtil;
import microbat.util.MicroBatUtil;
import mutation.mutator.Mutator;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.InfixExpression;
import org.eclipse.jdt.core.dom.MethodDeclaration;

import sav.strategies.dto.AppJavaClassPath;
import sav.strategies.dto.ClassLocation;
import sav.strategies.mutanbug.MutationResult;

public class TestCaseParser {
	
	public static final String TEST_RUNNER = "microbat.evaluation.junit.MicroBatTestRunner";
	
	private String testPackage = "com.test";
	
	private void setUp(){
		//TODO
	}
	
	public void runEvaluation(){
		
		IPackageFragment packFrag = JavaUtil.findIPackageInProject(testPackage);
		try {
			for(ICompilationUnit icu: packFrag.getCompilationUnits()){
				CompilationUnit cu = JavaUtil.convertICompilationUnitToASTNode(icu);
				
				List<MethodDeclaration> testingMethods = JTestUtil.findTestingMethod(cu); 
				
				if(!testingMethods.isEmpty()){
					String className = JavaUtil.getFullNameOfCompilationUnit(cu);
					
					for(MethodDeclaration testingMethod: testingMethods){
						String methodName = testingMethod.getName().getIdentifier();
						
						AppJavaClassPath appClassPath = createProjectClassPath(className, methodName);
						
						TestCaseRunner checker = new TestCaseRunner();
						List<BreakPoint> executingStatements = checker.collectBreakPoints(appClassPath);
						
						//mutate
						List<ClassLocation> locationList = findMutationLocation(executingStatements);
						Mutator mutator = new Mutator("");
						
						Map<String, MutationResult> result = mutator.mutate(locationList);
						
						if(checker.isPassingTest()){
							Trace correctTrace = new TraceModelConstructor().constructTraceModel(appClassPath, executingStatements);
							
							System.currentTimeMillis();
							
						}
						else{
							System.out.println("a failed test case");
						}
					}
					
				}
			}
		} catch (JavaModelException e) {
			e.printStackTrace();
		}
	}
	
	private List<ClassLocation> findMutationLocation(List<BreakPoint> executingStatements) {
		List<ClassLocation> locations = new ArrayList<>();
		
		Map<String, List<BreakPoint>> lineMap = new HashMap<>();
		for(BreakPoint point: executingStatements){
			String className = point.getDeclaringCompilationUnitName();
			
			List<BreakPoint> points = lineMap.get(className);
			if(points == null){
				points = new ArrayList<>();
			}
			
			lineMap.put(className, points);
			points.add(point);
		}
		
		for(String className: lineMap.keySet()){
			CompilationUnit cu = JavaUtil.findCompilationUnitInProject(className);
			List<Integer> lines = new ArrayList<>();
			for(BreakPoint point: lineMap.get(className)){
				lines.add(point.getLineNo());
			}
			
			MutationPointChecker checker = new MutationPointChecker(cu, lines);
			cu.accept(checker);
			
			List<ClassLocation> mutationLocations = checker.getMutationPoints();
			locations.addAll(mutationLocations);
		}
		
		
		return locations;
	}
	
	

	private AppJavaClassPath createProjectClassPath(String className, String methodName){
		AppJavaClassPath classPath = MicroBatUtil.constructClassPaths("bin");
		
		String userDir = System.getProperty("user.dir");
		String junitDir = userDir + File.separator + "dropins" + File.separator + "junit_lib";
		
		String junitPath = junitDir + File.separator + "junit.jar";
		String hamcrestCorePath = junitDir + File.separator + "org.hamcrest.core.jar";
		String testRunnerPath = junitDir  + File.separator + "testrunner.jar";
		
		classPath.addClasspath(junitPath);
		classPath.addClasspath(hamcrestCorePath);
		classPath.addClasspath(testRunnerPath);
		
		classPath.addClasspath(junitDir);
		
		classPath.setOptionalTestClass(className);
		classPath.setOptionalTestMethod(methodName);
		
		classPath.setLaunchClass(TEST_RUNNER);
		
		return classPath;
		
		
//		File file = new File(classPath.getClasspathStr());
//		List<URL> cpList = new ArrayList<>();
//		for(String cPath: classPath.getClasspaths()){
//			File file = new File(cPath);
//			URL url;
//			try {
//				url = file.toURI().toURL();
//				cpList.add(url);
//			} catch (MalformedURLException e) {
//				e.printStackTrace();
//			}
//		}
//		URLClassLoader urlcl  = URLClassLoader.newInstance(cpList.toArray(new URL[0]));
//		return urlcl;
	}
}
