package microbat.evaluation.junit;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

import microbat.util.JavaUtil;
import microbat.util.MicroBatUtil;
import microbat.util.Settings;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.ChildListPropertyDescriptor;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.MarkerAnnotation;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.TypeDeclaration;

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
				
				List<MethodDeclaration> testingMethods = findTestingMethod(cu); 
				
				if(!testingMethods.isEmpty()){
					String className = JavaUtil.getFullNameOfCompilationUnit(cu);
					
					for(MethodDeclaration testingMethod: testingMethods){
						String methodName = testingMethod.getName().getIdentifier();
						
						AppJavaClassPath appClassPath = createProjectClassPath(className, methodName);
						
						
//						URLClassLoader classLoader = loadProjectClassPath();
//						boolean isSuccessful = MicroBatTestRunner.isTestSuccessful(className, methodName, classLoader);
						
						TestCaseRunner checker = new TestCaseRunner();
						checker.collectBreakPoints(appClassPath);
						
						if(checker.isPassingTest()){
							System.out.println("a passing test case");
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

	private List<MethodDeclaration> findTestingMethod(CompilationUnit cu) {
		boolean isSubclassOfTestCase = isSubclassOfTestCase(cu);
		
		TestingMethodChecker checker = new TestingMethodChecker(isSubclassOfTestCase);
		cu.accept(checker);
		
		List<MethodDeclaration> testingMethods = checker.getTestingMethods();
		
		return testingMethods;
	}

	private boolean isSubclassOfTestCase(CompilationUnit cu) {
		TypeDeclaration typeDel = (TypeDeclaration) cu.types().get(0);
		ITypeBinding binding = typeDel.resolveBinding();
		
		boolean isSubclassOfTestCase = false;
		String parentName = "";
		while(true){
			if(binding == null){
				break;
			}
			
			ITypeBinding superBinding = binding.getSuperclass();
			if(superBinding == null){
				break;
			}
			
			parentName = superBinding.getQualifiedName();
			if(parentName.equals("junit.framework.TestCase")){
				isSubclassOfTestCase = true;
				break;
			}
			
			binding = superBinding;
		}
		
		return isSubclassOfTestCase;
	}
	
	class TestingMethodChecker extends ASTVisitor{
		private boolean isSubclassOfTestCase;
		private ArrayList<MethodDeclaration> testingMethods = new ArrayList<>();
		
		public TestingMethodChecker(boolean isSubclassOfTestCase) {
			super();
			this.isSubclassOfTestCase = isSubclassOfTestCase;
		}


		public boolean visit(MethodDeclaration md){
			
			if(isSubclassOfTestCase){
				String methodName = md.getName().getIdentifier();
				if(methodName.startsWith("test")){
					testingMethods.add(md);
					return false;
				}
			}
			else{
				ChildListPropertyDescriptor descriptor = md.getModifiersProperty();
				Object obj = md.getStructuralProperty(descriptor);
				List<ASTNode> methodModifiers = MicroBatUtil.asT(obj);
				
				for(ASTNode node: methodModifiers){
					if(node instanceof MarkerAnnotation){
						MarkerAnnotation annotation = (MarkerAnnotation)node;
						String name = annotation.getTypeName().getFullyQualifiedName();
						if(name != null && name.equals("Test")){
							testingMethods.add(md);
							return false;
						}
					}
				}
			}
			
			
			return false;
		}


		public ArrayList<MethodDeclaration> getTestingMethods() {
			return testingMethods;
		}
		
		
	}
}
