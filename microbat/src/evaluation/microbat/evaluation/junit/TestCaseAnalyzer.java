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
import microbat.evaluation.io.ExcelReporter;
import microbat.evaluation.io.IgnoredTestCaseFiles;
import microbat.evaluation.model.Trial;
import microbat.model.BreakPoint;
import microbat.model.trace.Trace;
import microbat.util.JTestUtil;
import microbat.util.JavaUtil;
import microbat.util.MicroBatUtil;
import microbat.util.Settings;
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
	
	private List<Trial> trials = new ArrayList<>();
	private IgnoredTestCaseFiles ignoredTestCaseFiles = new IgnoredTestCaseFiles();
	
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
		
//		runSingeTestCase();
	}
	
	private void runSingeTestCase(){
		String className = "org.apache.commons.math.analysis.BinaryFunctionTest";
		String methodName = "testFix2nd";
		
		try {
			runEvaluationForSingleMethod(className, methodName);
		} catch (JavaModelException e) {
			e.printStackTrace();
		}
	}

	private void runEvaluation(IPackageFragment pack) throws JavaModelException {
		
		int num = 0;
		ExcelReporter reporter = new ExcelReporter();
		reporter.start();
		
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
						
						
						if(trials.size() > 30000){
							reporter.export(trials, Settings.projectName+num);
							
							trials.clear();
							reporter = new ExcelReporter();
							num++;
						}
					}
					
				}
			}
		}
		
		reporter.export(trials, Settings.projectName+num);
	}
	
//	private void locateCertainTestCase(String className, String methodName){
//		
//	}

	private boolean runEvaluationForSingleMethod(String className, String methodName) throws JavaModelException {
		AppJavaClassPath testcaseConfig = createProjectClassPath(className, methodName);
		String testcaseName = className + "#" + methodName;
		
		if(this.ignoredTestCaseFiles.contains(testcaseName)){
			return false;
		}
		
		TestCaseRunner checker = new TestCaseRunner();
		checker.checkValidity(testcaseConfig);
		
		Trace correctTrace = null;
		
		if(checker.isPassingTest()){
			System.out.println(testcaseName + " is a passed test case");
			
			List<BreakPoint> executingStatements = checker.collectBreakPoints(testcaseConfig);
			List<ClassLocation> locationList = findMutationLocation(executingStatements);
			if(!locationList.isEmpty()){
				System.out.println("mutating the tested methods of " + testcaseName);
				Map<String, MutationResult> mutations = generateMutationFiles(locationList);
				System.out.println("mutation done for " + testcaseName);
				for(String mutatedClass: mutations.keySet()){
					MutationResult result = mutations.get(mutatedClass);
					for(Integer line: result.getMutatedFiles().keySet()){
						
//						if(line != 16){
//							continue;
//						}
						
						List<File> mutatedFileList = result.getMutatedFiles(line);		
						
						if(!mutatedFileList.isEmpty()){
							try {
								List<TraceFilePair> killingMutatantTraces = 
										mutateCode(mutatedClass, mutatedFileList, testcaseConfig, line);
								
								if(!killingMutatantTraces.isEmpty()){
									if(null == correctTrace){
										correctTrace = new TraceModelConstructor().
												constructTraceModel(testcaseConfig, executingStatements);
									}
									
									for(TraceFilePair pair: killingMutatantTraces){
										Trace mutantTrace = pair.mutatedTrace;
										SimulatedMicroBat microbat = new SimulatedMicroBat();
										ClassLocation mutatedLocation = new ClassLocation(mutatedClass, null, line);
										Trial trial;
										try {
											trial = microbat.detectMutatedBug(mutantTrace, correctTrace, mutatedLocation, 
													testcaseName, pair.mutatedFile);
											if(trial != null){
												trials.add(trial);	
												if(!trial.isBugFound()){
													System.err.println("Cannot find bug in Mutated File: " + pair.mutatedFile);
												}
											}
										} catch (Exception e) {
											e.printStackTrace();
											System.err.println("Mutated File: " + pair.mutatedFile);
										}
									}
									
//									return true;
								}
								else{
									System.out.println("No suitable mutants for test case " + testcaseName + "in line " + line);
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
			else{
				System.out.println("but " + testcaseName + " cannot be mutated");
				this.ignoredTestCaseFiles.addTestCase(testcaseName);
			}
		}
		else{
			System.out.println(testcaseName + " is a failed test case");
			this.ignoredTestCaseFiles.addTestCase(testcaseName);
			return false;
		}
		
		return false;
	}
	
	class TraceFilePair{
		Trace mutatedTrace;
		String mutatedFile;
		
		public TraceFilePair(Trace mutatedTrace, String mutatedFile) {
			super();
			this.mutatedTrace = mutatedTrace;
			this.mutatedFile = mutatedFile;
		}
		
		public Trace getMutatedTrace() {
			return mutatedTrace;
		}
		
		public void setMutatedTrace(Trace mutatedTrace) {
			this.mutatedTrace = mutatedTrace;
		}
		
		public String getMutatedFile() {
			return mutatedFile;
		}
		
		public void setMutatedFile(String mutatedFile) {
			this.mutatedFile = mutatedFile;
		}
		
	}

	private List<TraceFilePair> mutateCode(String key, List<File> mutatedFileList, AppJavaClassPath testcaseConfig, int mutatedLine)
			throws MalformedURLException, JavaModelException, IOException {
		ICompilationUnit iunit = JavaUtil.findICompilationUnitInProject(key);
		String originalCodeText = iunit.getSource();
		
		List<TraceFilePair> killingMutantTraces = new ArrayList<>();
		
		for(File file: mutatedFileList){
			System.out.println("generating trace for mutated class " + iunit.getElementName() + " (line: " + mutatedLine + ")");
			String mutatedCodeText = FileUtils.readFileToString(file);
			
			iunit.getBuffer().setContents(mutatedCodeText);
			iunit.save(new NullProgressMonitor(), true);
			
			autoCompile();
			
			TestCaseRunner checker = new TestCaseRunner();
			checker.checkValidity(testcaseConfig);
			
			boolean isKill = !checker.isPassingTest() && !checker.hasCompilationError();
			if(isKill){
				TraceModelConstructor constructor = new TraceModelConstructor();
				
				List<BreakPoint> executingStatements = checker.collectBreakPoints(testcaseConfig);
				Trace killingMutantTrace = constructor.constructTraceModel(testcaseConfig, executingStatements);
				
				TraceFilePair tfPair = new TraceFilePair(killingMutantTrace, file.toString());
				
				killingMutantTraces.add(tfPair);
			}
			
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
			try {
				if(!JTestUtil.isLocationInTestPackage(location)){
					locations.add(location);				
				}
			} catch (JavaModelException e) {
				e.printStackTrace();
			}
			
		}
		
		return locations;
	}
	
	

	private AppJavaClassPath createProjectClassPath(String className, String methodName){
		AppJavaClassPath classPath = MicroBatUtil.constructClassPaths();
		
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
