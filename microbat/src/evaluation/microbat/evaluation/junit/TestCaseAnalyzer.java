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
//	private List<Trial> overLongTrials = new ArrayList<>();
	private IgnoredTestCaseFiles ignoredTestCaseFiles;
	private ParsedTrials parsedTrials;
	
	private List<String> errorMsgs = new ArrayList<>();
	private int trialFileNum = 0;
	
	public TestCaseAnalyzer(){
		ignoredTestCaseFiles = new IgnoredTestCaseFiles();
		parsedTrials = new ParsedTrials();
		trialFileNum = parsedTrials.getStartFileOrder();
	}
	
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
		
//		ExcelReporter reporter = new ExcelReporter();
//		reporter.start();
//		
//		IPackageFragmentRoot testRoot = JavaUtil.findTestPackageRootInProject();
//		
//		for(IJavaElement element: testRoot.getChildren()){
//			if(element instanceof IPackageFragment){
//				runEvaluation((IPackageFragment)element, reporter);				
//			}
//		}
//		
//		reporter.export(trials, Settings.projectName+trialFileNum);
		
		runSingeTestCase();
	}
	
	private void runSingeTestCase(){
		String className = "org.apache.commons.math.analysis.BinaryFunctionTest";
		String methodName = "testAtan2";
		String mutationFile = "C:\\Users\\YUNLIN~1\\AppData\\Local\\Temp\\"
				+ "apache-common-math-2.2\\3054_38_3\\FastMath.java";
		String mutatedClass = "org.apache.commons.math.util.FastMath";
		int mutatedLine = 3054;
		
		try {
			runEvaluationForSingleTrial(className, methodName, mutationFile, mutatedClass, mutatedLine);
		} catch (JavaModelException e) {
			e.printStackTrace();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void runEvaluationForSingleTrial(String testClassName,
			String testMethodName, String mutationFile, String mutatedClassName, int mutatedLine) 
					throws JavaModelException, MalformedURLException, IOException {
		String testcaseName = testClassName + "#" + testMethodName;
		AppJavaClassPath testcaseConfig = createProjectClassPath(testClassName, testMethodName);
		
		File mutatedFile = new File(mutationFile);
		
		Trace killingMutatantTrace = 
				mutateCode(mutatedClassName, mutatedFile, testcaseConfig, mutatedLine, testcaseName);
		
		if(killingMutatantTrace == null){
			System.out.println("mutated trace is over long");
			return;
		}
		
		TestCaseRunner checker = new TestCaseRunner();
		
		List<BreakPoint> executingStatements = checker.collectBreakPoints(testcaseConfig);
		Trace correctTrace = new TraceModelConstructor().
				constructTraceModel(testcaseConfig, executingStatements);
		
		SimulatedMicroBat microbat = new SimulatedMicroBat();
		ClassLocation mutatedLocation = new ClassLocation(mutatedClassName, null, mutatedLine);
		Trial trial;
		try {
			trial = microbat.detectMutatedBug(killingMutatantTrace, correctTrace, mutatedLocation, 
					testcaseName, mutatedFile.toString());
			if(trial != null){
				if(!trial.isBugFound()){
					System.err.println("Cannot find bug in Mutated File: " + mutatedFile);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("Mutated File: " + mutatedFile);
		}
		
	}

	private void runEvaluation(IPackageFragment pack, ExcelReporter reporter) throws JavaModelException {
		
		for(IJavaElement javaElement: pack.getChildren()){
			if(javaElement instanceof IPackageFragment){
				runEvaluation((IPackageFragment)javaElement, reporter);
			}
			else if(javaElement instanceof ICompilationUnit){
				ICompilationUnit icu = (ICompilationUnit)javaElement;
				CompilationUnit cu = JavaUtil.convertICompilationUnitToASTNode(icu);
				
				List<MethodDeclaration> testingMethods = JTestUtil.findTestingMethod(cu); 
				if(!testingMethods.isEmpty()){
					String className = JavaUtil.getFullNameOfCompilationUnit(cu);
					
					for(MethodDeclaration testingMethod: testingMethods){
						String methodName = testingMethod.getName().getIdentifier();
						runEvaluationForSingleMethod(className, methodName, reporter);
					}
					
				}
			}
		}
		
	}
	
	private boolean runEvaluationForSingleMethod(String className, String methodName, ExcelReporter reporter) 
			throws JavaModelException {
		AppJavaClassPath testcaseConfig = createProjectClassPath(className, methodName);
		String testCaseName = className + "#" + methodName;
		
		if(this.ignoredTestCaseFiles.contains(testCaseName)){
			return false;
		}
		
		TestCaseRunner checker = new TestCaseRunner();
		checker.checkValidity(testcaseConfig);
		
		Trace correctTrace = null;
		
		if(checker.isPassingTest()){
			System.out.println(testCaseName + " is a passed test case");
			
			List<BreakPoint> executingStatements = checker.collectBreakPoints(testcaseConfig);
			System.out.println("identifying the possible mutated location for " + testCaseName);
			List<ClassLocation> locationList = findMutationLocation(executingStatements);
			
			if(!locationList.isEmpty()){
				System.out.println("mutating the tested methods of " + testCaseName);
				Map<String, MutationResult> mutations = generateMutationFiles(locationList);
				System.out.println("mutation done for " + testCaseName);
				for(String mutatedClass: mutations.keySet()){
					MutationResult result = mutations.get(mutatedClass);
					for(Integer line: result.getMutatedFiles().keySet()){
						List<File> mutatedFileList = result.getMutatedFiles(line);	
						
						for(File mutationFile: mutatedFileList){
							
							Trial tmpTrial = new Trial();
							tmpTrial.setTestCaseName(testCaseName);
							tmpTrial.setMutatedFile(mutationFile.toString());
							tmpTrial.setMutatedLineNumber(line);
							
							if(parsedTrials.contains(tmpTrial)){
								continue;
							}
							
							try {
								Trace killingMutatantTrace = 
										mutateCode(mutatedClass, mutationFile, testcaseConfig, line, testCaseName);
								
								if(killingMutatantTrace != null){
									if(null == correctTrace){
										correctTrace = new TraceModelConstructor().
												constructTraceModel(testcaseConfig, executingStatements);
									}
									
									SimulatedMicroBat microbat = new SimulatedMicroBat();
									ClassLocation mutatedLocation = new ClassLocation(mutatedClass, null, line);
									Trial trial;
									try {
										trial = microbat.detectMutatedBug(killingMutatantTrace, correctTrace, mutatedLocation, 
												testCaseName, mutationFile.toString());
										if(trial != null){
											trials.add(trial);	
											if(!trial.isBugFound()){
												String errorMsg = "Test case: " + testCaseName + 
														" fail to find bug\n" + "Mutated File: " + mutationFile;
												System.err.println(errorMsg);
												errorMsgs.add(errorMsg);
											}
											
											if(trials.size() > 100){
												reporter.export(trials, Settings.projectName+trialFileNum);
												
												trials.clear();
												reporter = new ExcelReporter();
												reporter.start();
												trialFileNum++;
											}
										}
									} catch (Exception e) {
										e.printStackTrace();
										String errorMsg = "Test case: " + testCaseName + 
												" has exception\n" + "Mutated File: " + mutationFile;
										System.err.println(errorMsg);
										errorMsgs.add(errorMsg);
									}
									
									if(errorMsgs.size() > 5){
										System.currentTimeMillis();
									}
								}
								else{
									System.out.println("No suitable mutants for test case " + testCaseName + "in line " + line);
								}
							} catch (Exception e) {
								e.printStackTrace();
								System.err.println("Test Case:");
								System.err.println(tmpTrial);
							} 
						}
					}
				}
			}
			else{
				System.out.println("but " + testCaseName + " cannot be mutated");
				this.ignoredTestCaseFiles.addTestCase(testCaseName);
			}
		}
		else{
			System.out.println(testCaseName + " is a failed test case");
			this.ignoredTestCaseFiles.addTestCase(testCaseName);
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

	private Trace mutateCode(String mutatedClass, File mutationFile, AppJavaClassPath testcaseConfig, 
			int mutatedLine, String testCaseName) 
			throws MalformedURLException, JavaModelException, IOException, NullPointerException {
		
		Trace killingMutantTrace = null;
		
		ICompilationUnit iunit = JavaUtil.findICompilationUnitInProject(mutatedClass);
		String originalCodeText = iunit.getSource();
		
		System.out.print("checking mutated class " + iunit.getElementName() + " (line: " + mutatedLine + ")");
		String mutatedCodeText = FileUtils.readFileToString(mutationFile);
		
		iunit.getBuffer().setContents(mutatedCodeText);
		iunit.save(new NullProgressMonitor(), true);
		
		autoCompile();
		
		TestCaseRunner checker = new TestCaseRunner();
		checker.checkValidity(testcaseConfig);
		
		boolean isKill = !checker.isPassingTest() && !checker.hasCompilationError();
		System.out.println(": " + (isKill?"killed":"not killed"));
		
		if(isKill){
			System.out.println("generating trace for mutated class " + iunit.getElementName() + " (line: " + mutatedLine + ")");
			TraceModelConstructor constructor = new TraceModelConstructor();
			
			List<BreakPoint> executingStatements = checker.collectBreakPoints(testcaseConfig);
			
			if(checker.isOverLong()){
				Trial trial = new Trial(testCaseName, mutatedLine, mutationFile.toString(), false, null, 0, Trial.OVER_LONG);
				trials.add(trial);
				return null;
			}
			
			long t1 = System.currentTimeMillis();
			killingMutantTrace = constructor.constructTraceModel(testcaseConfig, executingStatements);
			long t2 = System.currentTimeMillis();
			System.out.println("Trace length: " + killingMutantTrace.size() + ", which takes " + (t2-t1)/1000 + "s to analyze.");
			
			//TraceFilePair tfPair = new TraceFilePair(killingMutantTrace, mutationFile.toString());
		}
		else{
			Trial trial = new Trial(testCaseName, mutatedLine, mutationFile.toString(), false, null, 0, Trial.NOT_KILL);
			trials.add(trial);
		}
		
		iunit.getBuffer().setContents(originalCodeText);
		iunit.save(new NullProgressMonitor(), true);
		autoCompile();
		
		return killingMutantTrace;
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
