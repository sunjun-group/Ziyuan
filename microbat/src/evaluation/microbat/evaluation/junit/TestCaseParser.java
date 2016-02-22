package microbat.evaluation.junit;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import microbat.evaluation.mutation.MutationPointChecker;
import microbat.model.BreakPoint;
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
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.MethodDeclaration;

import sav.strategies.dto.AppJavaClassPath;
import sav.strategies.dto.ClassLocation;
import sav.strategies.mutanbug.MutationResult;

public class TestCaseParser {
	
	public static final String TEST_RUNNER = "microbat.evaluation.junit.MicroBatTestRunner";
	
	private String testPackage = "com.test";
	
	public void setUp(){
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
						
						AppJavaClassPath testcase = createProjectClassPath(className, methodName);
						
						TestCaseRunner checker = new TestCaseRunner();
						List<BreakPoint> executingStatements = checker.collectBreakPoints(testcase);
						
						if(checker.isPassingTest()){
							//Trace correctTrace = new TraceModelConstructor().constructTraceModel(appClassPath, executingStatements);
							
							List<ClassLocation> locationList = findMutationLocation(executingStatements);
							
							if(!locationList.isEmpty()){
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
								
								
								for(String key: mutations.keySet()){
									MutationResult result = mutations.get(key);
									for(Integer line: result.getMutatedFiles().keySet()){
										List<File> mutatedFileList = result.getMutatedFiles(line);		
										
										if(!mutatedFileList.isEmpty()){
											try {
												mutateCode(key, mutatedFileList, testcase);
											} catch (MalformedURLException e) {
												e.printStackTrace();
											} catch (IOException e) {
												e.printStackTrace();
											}
										}
									}
									
									System.currentTimeMillis();
								}
								
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

	private void mutateCode(String key, List<File> mutatedFileList, AppJavaClassPath testcase)
			throws MalformedURLException, JavaModelException, IOException {
		ICompilationUnit iunit = JavaUtil.findICompilationUnitInProject(key);
		String path = iunit.getResource().getLocationURI().toURL().getFile();
		
		String originalCodeText = iunit.getSource();
		CompilationUnit cunit = JavaUtil.convertICompilationUnitToASTNode(iunit);
		
		for(File file: mutatedFileList){
			
			String mutatedCodeText = FileUtils.readFileToString(file);
			
//			File toBeMutatedFile = new File(path);
//			FileUtils.writeStringToFile(toBeMutatedFile, mutatedCodeText);
			
			iunit.getBuffer().setContents(mutatedCodeText);
			iunit.save(new NullProgressMonitor(), true);
			
			autoCompile();
			
			TestCaseRunner checker = new TestCaseRunner();
			List<BreakPoint> executingStatements = checker.collectBreakPoints(testcase);
			
			boolean isKill = !checker.isPassingTest();
			System.out.println(isKill);
			
			System.currentTimeMillis();
//			Document document = new Document(mutatedCodeText);
//			ASTRewrite rewriter = ASTRewrite.create(cunit.getAST()); // ? check if the parameter object type is correct
//			
//			rewriter.rewriteAST().apply(document);
//			String source = document.get();									
			
		}
		
		iunit.getBuffer().setContents(originalCodeText);
		iunit.save(new NullProgressMonitor(), true);
		autoCompile();
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
