package microbat.evaluation.junit;

import java.io.File;
import java.util.List;

import microbat.codeanalysis.bytecode.MicrobatSlicer;
import microbat.model.BreakPoint;
import microbat.util.JTestUtil;
import microbat.util.JavaUtil;
import microbat.util.MicroBatUtil;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.MethodDeclaration;

import sav.strategies.dto.AppJavaClassPath;

public class TestCaseParser {
	
	public static final String TEST_RUNNER = "microbat.evaluation.junit.MicroBatTestRunner";
	
	private String testPackage = "com.test";
	
	private void setUp(){
		//TODO
	}
	
	public void runEvaluation(){
		
		setUp();
		
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
						
						
//						URLClassLoader classLoader = loadProjectClassPath();
//						boolean isSuccessful = MicroBatTestRunner.isTestSuccessful(className, methodName, classLoader);
						
						TestCaseRunner checker = new TestCaseRunner();
						List<BreakPoint> executingStatements = checker.collectBreakPoints(appClassPath);
						
						if(checker.isPassingTest()){
							MicrobatSlicer slicer = new MicrobatSlicer(executingStatements);
							List<BreakPoint> breakpoints = null;
							try {
								System.out.println("start analyzing byte code ...");
								breakpoints = slicer.parsingBreakPoints(appClassPath);
								System.out.println("finish analyzing byte code ...!");
							} catch (Exception e1) {
								e1.printStackTrace();
							}
							
							
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
