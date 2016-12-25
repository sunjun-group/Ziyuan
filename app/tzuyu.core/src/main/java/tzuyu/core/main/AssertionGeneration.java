package tzuyu.core.main;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import assertion.template.checker.BreakpointTemplate;
import assertion.template.checker.BreakpointTemplateChecker;
import assertion.utility.Utility;
import assertion.visitor.AddAssertStmtVisitor;
import de.unisb.cs.st.javaslicer.traceResult.TraceResult;
import icsetlv.InvariantMediator;
import icsetlv.common.dto.BreakpointData;
import icsetlv.variable.TestcasesExecutor;
import icsetlv.variable.VarNameVisitor.VarNameCollectionMode;
import icsetlv.variable.VariableNameCollector;
import invariant.templates.Template;
import japa.parser.JavaParser;
import japa.parser.ast.CompilationUnit;
import japa.parser.ast.body.BodyDeclaration;
import japa.parser.ast.body.ConstructorDeclaration;
import japa.parser.ast.body.MethodDeclaration;
import japa.parser.ast.body.TypeDeclaration;
import japa.parser.ast.stmt.AssertStmt;
import mutation.io.DebugLineFileWriter;
import mutation.mutator.VariableSubstitution;
import mutation.mutator.insertdebugline.AddedLineData;
import mutation.mutator.insertdebugline.DebugLineData;
import mutation.parser.ClassAnalyzer;
import mutation.parser.ClassDescriptor;
import mutation.parser.JParser;
import sav.common.core.Pair;
import sav.common.core.utils.ClassUtils;
import sav.common.core.utils.CollectionUtils;
import sav.common.core.utils.JunitUtils;
import sav.java.parser.cfg.CFG;
import sav.java.parser.cfg.CfgEdge;
import sav.java.parser.cfg.CfgEntryNode;
import sav.java.parser.cfg.CfgFactory;
import sav.java.parser.cfg.CfgNode;
import sav.strategies.IApplicationContext;
import sav.strategies.dto.AppJavaClassPath;
import sav.strategies.dto.BreakPoint;
import sav.strategies.dto.BreakPoint.Variable;
import sav.strategies.junit.JunitResult;
import sav.strategies.junit.JunitRunner;
import sav.strategies.junit.JunitRunnerParameters;
import sav.strategies.vm.VMConfiguration;
import slicer.javaslicer.JavaSlicer;
import tzuyu.core.mutantbug.Recompiler;

public class AssertionGeneration extends TzuyuCore {
	
	private static Logger log = LoggerFactory.getLogger(AssertionGeneration.class);
	
	private String traceFilePath = null;
	
	private TraceResult traceResult = null;
	
	public AssertionGeneration(IApplicationContext appContext) {
		super(appContext);
	}
	
	public void genAssertion(AssertionGenerationParams params) throws Exception {
//		FilesBackup backup = null;
//		File origFile = null;
//		AppJavaClassPath appClasspath = appContext.getAppData();
//		String srcFolder = appClasspath.getSrc();
		
		try {
//			String className = params.getTestingClassName();
//			String methodName = params.getMethodName();
			
			// the original file
//			origFile = new File(ClassUtils.getJFilePath(srcFolder, className));
			
			// create new file with assertions
//			File newFile = addAssertionToFile(srcFolder, className, methodName);
			
			// back up the original file
//			backup = FilesBackup.startBackup();
//			backup.backup(origFile);
			
			// recompile new file
//			recompile(origFile, newFile);
			
			// generate test cases
			if (params.getJunitClassNames().size() == 0) {
				log.info("Generating random test cases...\n");
				params.setJunitClassNames(getRandomTestCases(params));
			}
			
			// run random test cases
			JunitResult jresult = runTestCases(params.getJunitClassNames(), params);
//			log.info("Test cases results: {}\n", jresult);
			
			if (jresult.getPassTests().isEmpty() || jresult.getFailTests().isEmpty()) return;
			
			// locations used to learn new assertion
			List<BreakPoint> learnLocs = collectLearningLocations(params);
//			log.info("Learning locations: {}\n", learnLocs);
			
			// learn assertions for new locations
			for (int i = learnLocs.size() - 1; i >= 0; i--) {
				List<BreakpointTemplate> bkpsTemplates = new ArrayList<BreakpointTemplate>();
				templateLearning(learnLocs.get(i), bkpsTemplates, params, jresult);
				
//				if (!bkpsTemplates.isEmpty()) {
//					List<DebugLineData> lines = new ArrayList<DebugLineData>();
//					convertToAssertStmt(bkpsTemplates.get(0), lines);
//					
//					// write the new file
//					DebugLineFileWriter writer = new DebugLineFileWriter(srcFolder);
//					newFile = writer.write(lines, className);
//							
//					// recompile new file
//					recompile(origFile, newFile);
//					
//					// run random test cases again in case the file is changed with new assertions
//					if (i > 0) {
//						jresult = runTestCases(params.getJunitClassNames(), params);
//						log.info("Test cases results: " + jresult);
//					}
//					
//					traceFilePath = null;
//					traceResult = null;
//				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			// restore the original file
//			if (backup != null) {
//				backup.restoreAll();
//				Recompiler recompiler = new Recompiler(new VMConfiguration(appClasspath));
//				recompiler.recompileJFile(appClasspath.getTarget(), origFile);
//			}
		}
		
	}
	
	public void convertToAssertStmt(BreakpointTemplate bkpTemplate, List<DebugLineData> lines) {
		for (Template t : bkpTemplate.getTemplates()) {
			AssertStmt a = t.convertToAssertStmt();
			if (a != null) {
				int lineNo = bkpTemplate.getBreakPoint().getLineNo();
				a.setBeginLine(lineNo);
				
				AddedLineData d = new AddedLineData(lineNo, a);
				lines.add(d);
			}
		}
	}
	
	public void recompile(File origFile, File newFile) throws Exception {
		AppJavaClassPath appClasspath = appContext.getAppData();
		// System.out.println(FileUtils.readFileToString(newFile));
		
		// overwrite the original file with the new file
		FileUtils.copyFile(newFile, origFile, false);
					
		// recompile the new file
		Recompiler recompiler = new Recompiler(new VMConfiguration(appClasspath));
		recompiler.recompileJFile(appClasspath.getTarget(), newFile);
	}
	
	public void templateLearning(BreakPoint learnLoc,
			List<BreakpointTemplate> bkpsTemplates,
			AssertionGenerationParams params,
			JunitResult jresult)
			throws Exception {
//		log.info("Begin learning at location: {}\n", learnLoc);
		
		// collect failure locations
		List<BreakPoint> failureLocs = new ArrayList<BreakPoint>();
		collectFailureLocations(failureLocs, params, jresult);
//		log.info("Failure locations: {}\n", failureLocs);
		
		if (failureLocs.isEmpty()) return;
		
		// collect affected locations, they are locations from slices of the assertions
		// that depend on learning locations
		// and have the line number no less than learning locations
		List<BreakPoint> affectedLocs = new ArrayList<BreakPoint>();
		collectAffectedLocs(learnLoc.getLineNo(), affectedLocs, failureLocs, jresult, params);
//		log.info("Affected locations: {}\n", affectedLocs);
		
		if (affectedLocs.isEmpty()) return;
		
		// collect learning variables
		List<Variable> learnVars = new ArrayList<Variable>();
		collectLearningVariables(learnVars, affectedLocs);
//		log.info("Learning varibles: {}\n", learnVars);
		
//		if (learnVars.isEmpty()) return;
				
		// add learning variables to learning location
		learnLoc.addVars(learnVars);
			
		// collect data at break points
		InvariantMediator im = new InvariantMediator(appContext.getAppData());
		TestcasesExecutor tcExecutor = new TestcasesExecutor(params.getValueRetrieveLevel());
		im.setTcExecutor(tcExecutor);
		
		long stop5 = System.currentTimeMillis();
			
		List<String> tests = new ArrayList<String>();
		for (Pair<String,String> entry : jresult.getTests()) {
			tests.add(entry.a + "." + entry.b);
		}
		
		java.util.Collections.sort(tests, new Comparator<String>() {
			@Override
			public int compare(String s1, String s2) {
				if (s1.length() < s2.length()) return -1;
				else if (s1.length() > s2.length()) return 1;
				else return s1.compareTo(s2);
			}
		});
		
		List<BreakpointData> bkpsData = im.debugTestAndCollectData(tests, CollectionUtils.listOf(learnLoc));
		BreakpointData bkpData = bkpsData.get(0);
//		log.info("Breakpoint data: {}\n", bkpData);
		
		if (bkpData.getPassValues().isEmpty() || bkpData.getFailValues().isEmpty()) return;
		
		// check data with templates
		BreakpointTemplateChecker tc = new BreakpointTemplateChecker(im);
		CollectionUtils.addIfNotNull(bkpsTemplates, tc.checkTemplates(bkpData));
	}
	
	public List<String> getRandomTestCases(AssertionGenerationParams params) {
		List<String> junitClassNames = new ArrayList<String>(params.getJunitClassNames());
		
		while (true) {
			try {
				List<String> randomTests = generateNewTests(
						params.getTestingClassName(),
						params.getMethodName(),
						params.getVerificationMethod(),
						params.getNumberOfTestCases());
				junitClassNames.addAll(randomTests);
				System.out.println(randomTests);
				break;
			} catch (Throwable e) {
				e.printStackTrace();
			}
		}
		
		return junitClassNames;
	}
	
	public JunitResult runTestCases(List<String> junitClassNames, AssertionGenerationParams params)
			throws Exception {
		JunitRunnerParameters junitParams = new JunitRunnerParameters();
		junitParams.setJunitClasses(junitClassNames);
		junitParams.setTestingPkgs(params.getTestingPkgs());
		junitParams.setTestingClassNames(params.getTestingClassNames());
				
		JunitResult jresult = JunitRunner.runTestcases(appContext.getAppData(), junitParams);
		
		return jresult;
	}
	
	public void collectLearningVariables(List<Variable> learnVars, List<BreakPoint> affectedLocs)
			throws Exception {
		AppJavaClassPath appClasspath = appContext.getAppData();
		VariableNameCollector vnc = new VariableNameCollector(VarNameCollectionMode.FULL_NAME, appClasspath.getSrc());
		vnc.updateVariables(affectedLocs);
		
		for (BreakPoint bkp : affectedLocs) {
			CollectionUtils.addIfNotNullNotExist(learnVars, bkp.getVars());
		}
	}

	public void collectAffectedLocs(int lineNo, List<BreakPoint> affectedLocs, List<BreakPoint> failureLocs,
			JunitResult jresult, AssertionGenerationParams params) throws Exception {
		String methodName = params.getMethodName();
		
		JavaSlicer slicer = (JavaSlicer) appContext.getSlicer();
		
		slicer.setFiltering(params.getTestingClassNames(), params.getTestingPkgs());
		slicer.init(appContext.getAppData());
		
		List<String> junitClassMethods = JunitUtils.toClassMethodStrs(jresult.getTests());
		if (traceFilePath == null) traceFilePath = slicer.createTraceFile(junitClassMethods);
		// String traceFilePath = slicer.createTraceFile(junitClassMethods);
		
		List<BreakPoint> collectLocs = new ArrayList<BreakPoint>();

		for (BreakPoint failureLoc : failureLocs) {
			if (failureLoc.getLineNo() >= lineNo) {
				collectLocs.add(failureLoc);
			}
		}
		
		if (traceResult == null) traceResult = slicer.readTraceFile(traceFilePath);
		// TraceResult traceResult = slicer.readTraceFile(traceFilePath);
				
		// when traceResult == null, there are some errors
		if (traceResult == null) return;
		
		List<BreakPoint> slicedLocs = slicer.sliceFromTraceResult(traceResult,
				new HashSet<BreakPoint>(collectLocs), junitClassMethods);
//		log.info("Sliced locations: {}\n", slicedLocs);
		
		for (BreakPoint bkp : slicedLocs) {
			if (methodName.contains(bkp.getMethodName())) {
				CollectionUtils.addIfNotNullNotExist(affectedLocs, bkp);
			}
		}
		
//		outer:
//		for (BreakPoint bkp1 : slicedLocs) {
//			if (methodName.equals(bkp1.getMethodName()) && bkp1.getLineNo() <= lineNo) {
//				for (BreakPoint bkp2 : slicedLocs) {
//					if (methodName.equals(bkp2.getMethodName()) && bkp2.getLineNo() >= lineNo) {
//						CollectionUtils.addIfNotNullNotExist(affectedLocs, bkp2);
//					}
//				}
//				break outer;
//			}
//		}
		
		for (BreakPoint bkp : collectLocs) {
			CollectionUtils.addIfNotNullNotExist(affectedLocs, bkp);
		}
		
	}
	
	public void collectFailureLocations(List<BreakPoint> locations, AssertionGenerationParams params,
			JunitResult jresult) throws Exception {
		String className = params.getTestingClassName();
		String methodName = params.getMethodName();
		Set<BreakPoint> failureTraces = jresult.getFailureTraces();
		
		for (BreakPoint bkp : failureTraces) {
			if (bkp.getClassCanonicalName().equals(className) &&
					methodName.contains(bkp.getMethodName())) {
				locations.add(bkp);
			}
		}
		
//		// the original file
//		File file = new File(ClassUtils.getJFilePath(appContext.getAppData().getSrc(), params.getTestingClassName()));
//				
//		// parse the original file
//		FileInputStream in = new FileInputStream(file);
//		CompilationUnit cu = JavaParser.parse(in);
//				
//		// the visitor used to add assertions
//		CollectAssertLocsVisitor visitor = new CollectAssertLocsVisitor(params.getTestingClassName(),
//				params.getMethodName(), params.getListOfMethods());
//				
//		// visit the original file
//		visitor.visit(cu, locations);
	}
	
	public List<BreakPoint> collectLearningLocations(AssertionGenerationParams params) throws Exception {
		String srcFolder = appContext.getAppData().getSrc();
		String className = params.getTestingClassName();
		String methodName = params.getMethodName();
		
		List<BreakPoint> locations = new ArrayList<BreakPoint>();
		
		File file = new File(ClassUtils.getJFilePath(srcFolder, className));
		
		FileInputStream in = new FileInputStream(file);
		CompilationUnit cu = JavaParser.parse(in);
		
		/*
		GetLearningLocationsVisitor visitor = new GetLearningLocationsVisitor(className, methodName);
		
		visitor.visit(cu, locations);
		*/
		
		for (TypeDeclaration type : cu.getTypes()) {
			for (BodyDeclaration body : type.getMembers()) {
				if (body instanceof MethodDeclaration || body instanceof ConstructorDeclaration) {
					String currMethodName = "";
					
					if (body instanceof MethodDeclaration) {
						MethodDeclaration currMethod = (MethodDeclaration) body;
						if (methodName.indexOf("(") > 0) {
							currMethodName = Utility.getSigType(currMethod);
						} else {
							currMethodName = currMethod.getName();
						}
					} else {
						ConstructorDeclaration currMethod = (ConstructorDeclaration) body;
						if (methodName.indexOf("(") > 0) {
							currMethodName = Utility.getSigType(currMethod);
						} else {
							currMethodName = currMethod.getName();
						}
					}
					
					if (currMethodName.equals(methodName)) {
						CFG cfg = CfgFactory.createCFG(body);
							
						CfgEntryNode entry = cfg.getEntry();
							
						List<CfgNode> decisionNodes = new ArrayList<CfgNode>();
						// entry is not a decision node, we add it here to simplify the code below
						decisionNodes.add(entry);
						// cfg.getDecisionNode(entry, decisionNodes, new HashSet<CfgNode>());
						
						for (CfgNode node : decisionNodes) {
							for (CfgEdge edge : cfg.getOutEdges(node)) {
								int line = edge.getDest().getAstNode().getBeginLine();
								
								BreakPoint bkp = new BreakPoint(className, methodName, line);
								locations.add(bkp);
							}
						}
					}
				}
			}
		}
		
		return locations;
	}
	
	public File addAssertionToFile(String srcFolder, String className, String methodName) throws Exception {
		// the original file
		File file = new File(ClassUtils.getJFilePath(srcFolder, className));
		
		// get class analyser
		JParser parser = new JParser(srcFolder, new ArrayList<String>());
		ClassAnalyzer classAnalyser = new ClassAnalyzer(srcFolder, parser);
		
		// get variable substitution with type information for variable
		List<ClassDescriptor> classDecriptors = classAnalyser.analyzeJavaFile(file);
		VariableSubstitution subst = new VariableSubstitution(classDecriptors.get(0));
		
		// parse the original file
		FileInputStream in = new FileInputStream(file);
		CompilationUnit cu = JavaParser.parse(in);
		
		// the visitor used to add assertions
		AddAssertStmtVisitor visitor = new AddAssertStmtVisitor(cu.getImports(), subst, methodName);
		
		// visit the original file
		List<DebugLineData> arg = new ArrayList<DebugLineData>();
		visitor.visit(cu, arg);
		
		// number of initial assertion
		log.info("Number of initial assertions: {}\n", arg.size());
		
		// write the new file
		DebugLineFileWriter writer = new DebugLineFileWriter(srcFolder);
		File newFile = writer.write(arg, className);
		
		// return the new file
		return newFile;
	}
	
}
