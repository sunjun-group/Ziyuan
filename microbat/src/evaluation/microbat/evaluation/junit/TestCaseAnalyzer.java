package microbat.evaluation.junit;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import microbat.evaluation.SimulatedMicroBat;
import microbat.evaluation.TraceModelConstructor;
import microbat.evaluation.model.Trial;
import microbat.model.BreakPoint;
import microbat.model.trace.Trace;
import microbat.util.JTestUtil;
import microbat.util.JavaUtil;
import microbat.util.MicroBatUtil;
import mutation.mutator.Mutator;

import org.apache.commons.io.FileUtils;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.MethodDeclaration;

import sav.strategies.dto.AppJavaClassPath;
import sav.strategies.dto.ClassLocation;
import sav.strategies.mutanbug.MutationResult;

public class TestCaseAnalyzer {
	
	public static final String TEST_RUNNER = "microbat.evaluation.junit.MicroBatTestRunner";
	
//	private String testPackage = "com.test";
	private String testPackage = "org.apache.common.math";
	private List<Trial> trials = new ArrayList<>();
	
	public void test(){
		String str = "C:\\Users\\YUNLIN~1\\AppData\\Local\\Temp\\mutatedSource8245811234241496344\\47_25_1\\Main.java";
		File file = new File(str);
		
		try {
			String content = FileUtils.readFileToString(file);
			
			ICompilationUnit unit = JavaUtil.findICompilationUnitInProject("com.Main");
			unit.getBuffer().setContents(content);
			unit.save(new NullProgressMonitor(), true);
			
			IProject project = JavaUtil.getSpecificJavaProjectInWorkspace();
			try {
				project.build(IncrementalProjectBuilder.INCREMENTAL_BUILD, new NullProgressMonitor());
			} catch (CoreException e) {
				e.printStackTrace();
			}
			
			System.currentTimeMillis();
			
		} catch (IOException | JavaModelException e) {
			e.printStackTrace();
		}
	}
	
	private Map<String, MutationResult> generateMutationFiles(List<ClassLocation> locationList){
		ClassLocation cl = locationList.get(0);
		String cName = cl.getClassCanonicalName();
		ICompilationUnit unit = JavaUtil.findICompilationUnitInProject(cName);
		URI uri = unit.getResource().getLocationURI();
		String sourceFolderPath = uri.toString();
		cName = cName.replace(".", "/") + ".java";
		
		sourceFolderPath = sourceFolderPath.substring(0, sourceFolderPath.indexOf(cName));
		sourceFolderPath = sourceFolderPath.substring(5, sourceFolderPath.length());
		Mutator mutator = new Mutator(sourceFolderPath);
		Map<String, MutationResult> mutations = mutator.mutate(locationList);
		
		return mutations;
	}
	
	public void runEvaluation() throws JavaModelException{
		IPackageFragmentRoot testRoot = JavaUtil.findTestPackageRootInProject();
		
		for(IJavaElement element: testRoot.getChildren()){
			if(element instanceof IPackageFragment){
				runEvaluation((IPackageFragment)element);				
			}
		}
		
		
	}

	private void runEvaluation(IPackageFragment pack) throws JavaModelException {
		for(IJavaElement javaElement: pack.getChildren()){
			if(javaElement instanceof IPackageFragment){
				runEvaluation((IPackageFragment)javaElement);
			}
			else if(javaElement instanceof ICompilationUnit){
				ICompilationUnit icu = (ICompilationUnit)javaElement;
				CompilationUnit cu = JavaUtil.convertICompilationUnitToASTNode(icu);
				
				List<MethodDeclaration> testingMethods = JTestUtil.findTestingMethod(cu); 
				if(!testingMethods.isEmpty()){
					String className = JavaUtil.getFullNameOfCompilationUnit(cu);
					
					for(MethodDeclaration testingMethod: testingMethods){
						String methodName = testingMethod.getName().getIdentifier();
						
						runEvaluationForSingleMethod(className, methodName);
						
					}
					
				}
			}
		}
	}

	private boolean runEvaluationForSingleMethod(String className, String methodName) throws JavaModelException {
		AppJavaClassPath testcaseConfig = createProjectClassPath(className, methodName);
		String testcaseName = className + "#" + methodName;
		
		TestCaseRunner checker = new TestCaseRunner();
		List<BreakPoint> executingStatements = checker.collectBreakPoints(testcaseConfig);
		
		Trace correctTrace = null;
		
		if(checker.isPassingTest()){
			List<ClassLocation> locationList = findMutationLocation(executingStatements);
			if(!locationList.isEmpty()){
				Map<String, MutationResult> mutations = generateMutationFiles(locationList);
				for(String mutatedClass: mutations.keySet()){
					MutationResult result = mutations.get(mutatedClass);
					for(Integer line: result.getMutatedFiles().keySet()){
						
//						if(line != 40){
//							continue;
//						}
						
						List<File> mutatedFileList = result.getMutatedFiles(line);		
						
						if(!mutatedFileList.isEmpty()){
							try {
								List<Trace> killingMutatantTraces = mutateCode(mutatedClass, mutatedFileList, testcaseConfig);
								
								if(!killingMutatantTraces.isEmpty()){
									if(null == correctTrace){
										correctTrace = new TraceModelConstructor().
												constructTraceModel(testcaseConfig, executingStatements);
									}
									
									for(Trace mutantTrace: killingMutatantTraces){
										SimulatedMicroBat microbat = new SimulatedMicroBat();
										ClassLocation mutatedLocation = new ClassLocation(mutatedClass, null, line);
										Trial trial = microbat.detectMutatedBug(mutantTrace, correctTrace, mutatedLocation);
										trials.add(trial);
									}
									
//									return true;
								}
								else{
									System.out.println("No suitable mutants for test case " + testcaseName);
//									return false;
								}
							} catch (MalformedURLException e) {
								e.printStackTrace();
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
					}
				}
			}
		}
		else{
			System.out.println(testcaseName + "is a failed test case");
			return false;
		}
		
		return false;
	}

	private List<Trace> mutateCode(String key, List<File> mutatedFileList, AppJavaClassPath testcaseConfig)
			throws MalformedURLException, JavaModelException, IOException {
		ICompilationUnit iunit = JavaUtil.findICompilationUnitInProject(key);
		String originalCodeText = iunit.getSource();
		
		List<Trace> killingMutantTraces = new ArrayList<>();
		
		for(File file: mutatedFileList){
			
			String mutatedCodeText = FileUtils.readFileToString(file);
			
			iunit.getBuffer().setContents(mutatedCodeText);
			iunit.save(new NullProgressMonitor(), true);
			
			autoCompile();
			
			TestCaseRunner checker = new TestCaseRunner();
			List<BreakPoint> executingStatements = checker.collectBreakPoints(testcaseConfig);
			
			boolean isKill = !checker.isPassingTest() && !checker.hasCompilationError();
			if(isKill){
				TraceModelConstructor constructor = new TraceModelConstructor();
				Trace killingMutantTrace = constructor.constructTraceModel(testcaseConfig, executingStatements);
				killingMutantTraces.add(killingMutantTrace);
			}
			
			System.currentTimeMillis();
			
		}
		
		iunit.getBuffer().setContents(originalCodeText);
		iunit.save(new NullProgressMonitor(), true);
		autoCompile();
		
		return killingMutantTraces;
	}
	
	private void autoCompile() {
		IProject project = JavaUtil.getSpecificJavaProjectInWorkspace();
		try {
			project.build(IncrementalProjectBuilder.INCREMENTAL_BUILD, new NullProgressMonitor());
		} catch (CoreException e) {
			e.printStackTrace();
		}
		
	}

	private List<ClassLocation> findMutationLocation(List<BreakPoint> executingStatements) {
		List<ClassLocation> locations = new ArrayList<>();
		
		for(BreakPoint point: executingStatements){
			ClassLocation location = new ClassLocation(point.getDeclaringCompilationUnitName(), 
					null, point.getLineNo());
			locations.add(location);
		}
		
//		Map<String, List<BreakPoint>> lineMap = new HashMap<>();
//		for(BreakPoint point: executingStatements){
//			String className = point.getDeclaringCompilationUnitName();
//			
//			List<BreakPoint> points = lineMap.get(className);
//			if(points == null){
//				points = new ArrayList<>();
//			}
//			
//			lineMap.put(className, points);
//			points.add(point);
//		}
//		
//		for(String className: lineMap.keySet()){
//			CompilationUnit cu = JavaUtil.findCompilationUnitInProject(className);
//			List<Integer> lines = new ArrayList<>();
//			for(BreakPoint point: lineMap.get(className)){
//				lines.add(point.getLineNo());
//			}
//			
//			MutationPointChecker checker = new MutationPointChecker(cu, lines);
//			cu.accept(checker);
//			
//			List<ClassLocation> mutationLocations = checker.getMutationPoints();
//			locations.addAll(mutationLocations);
//		}
		
		
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
