package tzuyu.core.main;

import icsetlv.InvariantMediator;
import icsetlv.common.dto.BreakpointData;
import icsetlv.common.exception.IcsetlvException;
import icsetlv.variable.VarNameVisitor.VarNameCollectionMode;
import icsetlv.variable.VariableNameCollector;
import japa.parser.JavaParser;
import japa.parser.ast.CompilationUnit;
import japa.parser.ast.body.BodyDeclaration;
import japa.parser.ast.body.MethodDeclaration;
import japa.parser.ast.body.TypeDeclaration;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import mutation.io.DebugLineFileWriter;
import mutation.mutator.VariableSubstitution;
import mutation.mutator.insertdebugline.DebugLineData;
import mutation.parser.ClassAnalyzer;
import mutation.parser.ClassDescriptor;
import mutation.parser.JParser;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.spi.LoggerFactory;
import org.slf4j.Logger;

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
import sav.strategies.slicing.ISlicer;
import sav.strategies.vm.VMConfiguration;
import tzuyu.core.mutantbug.FilesBackup;
import tzuyu.core.mutantbug.Recompiler;
import assertion.template.checker.BreakpointTemplateChecker;
import assertion.visitor.AddAssertStmtVisitor;
import assertion.visitor.CollectAssertStmtVisitor;
import assertion.visitor.GetLearningLocationsVisitor;

public class AssertionGeneration extends TzuyuCore {
	
	private static Logger log = org.slf4j.LoggerFactory.getLogger(AssertionGeneration.class);

	public AssertionGeneration(IApplicationContext appContext) {
		super(appContext);
	}
		
	@Override
	public void genAssertion(AssertionGenerationParams params) throws Exception {
		
		FilesBackup backup = null;
		
		AppJavaClassPath appClasspath = appContext.getAppData();
		String srcFolder = appClasspath.getSrc();
		try {
			String className = params.getTestingClassName();
		
			// the original file
			File origFile = new File(ClassUtils.getJFilePath(srcFolder, className));
			
			// create new file with assertions
			File newFile = addAssertionToFile(srcFolder, className);
	
			// back up the original file
			backup = FilesBackup.startBackup();
			backup.backup(origFile);
			
			// overwrite the original file with the new file
			FileUtils.copyFile(newFile, origFile, false);
						
			// recompile the new file
			Recompiler recompiler = new Recompiler(new VMConfiguration(appClasspath));
			recompiler.recompileJFile(appClasspath.getTarget(), newFile);
			
			// add locations used to learn new assertion
			List<BreakPoint> learningLocs = addLearningLocations(params);
			System.out.println("Learning locations: " + learningLocs);
			
			// learn assertions for new locations
			for (int i = learningLocs.size() - 1; i >= 0; i--) {
				templateLearning(learningLocs.get(i), params);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			// restore the original file
			if (backup != null) {
				backup.restoreAll();
			}
		}
		
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
				break;
			} catch (Throwable e) {
				e.printStackTrace();
			}
		}
		
		return junitClassNames;
	}
	
	public void templateLearning(BreakPoint learnLoc, AssertionGenerationParams params)
		throws Exception
	{
		// get random test cases
		List<String> junitClassNames = getRandomTestCases(params);
				
		JunitRunnerParameters junitParams = new JunitRunnerParameters();
		junitParams.setJunitClasses(junitClassNames);
		junitParams.setTestingPkgs(params.getTestingPkgs());
		junitParams.setTestingClassNames(params.getTestingClassNames());
				
		JunitResult jresult = JunitRunner.runTestcases(appContext.getAppData(), junitParams);
				
		// collect assert statements locations
		List<BreakPoint> assertLocs = new ArrayList<BreakPoint>();
		collectAssertStatements(assertLocs, params);
		System.out.println("Assert locations: " + assertLocs);
		
		// collect sliced locations
		List<BreakPoint> affectedLocs = new ArrayList<BreakPoint>();
		collectAffectedLocs(learnLoc.getLineNo(), affectedLocs, assertLocs, jresult, params);
		System.out.println("Affected locations: " + affectedLocs);
		
		// collect learning variables
		List<Variable> learnVars = new ArrayList<Variable>();
		collectLearningVariables(learnVars, affectedLocs);
		learnLoc.addVars(learnVars);
		System.out.println("Learning varibles: " + learnVars);
		
		// collect data at break points
		InvariantMediator im = new InvariantMediator(appContext.getAppData());
		List<String> tests = new ArrayList<String>();
		for (int i = 1; i <= params.getNumberOfTestCases(); i++) {
			tests.add(junitClassNames.get(0) + "." + "test" + i);
		}
		List<BreakpointData> bkpsData = im.debugTestAndCollectData(tests, CollectionUtils.listOf(learnLoc));
				
		// check data with templates
		BreakpointTemplateChecker tc = new BreakpointTemplateChecker(im);
		tc.checkTemplates(bkpsData);
	}
	
	public void collectLearningVariables(List<Variable> learnVars, List<BreakPoint> affectedLocs) throws Exception {
		AppJavaClassPath appClasspath = appContext.getAppData();
		VariableNameCollector vnc = new VariableNameCollector(VarNameCollectionMode.FULL_NAME, appClasspath .getSrc());
		vnc.updateVariables(affectedLocs);
		
		for (BreakPoint bkp : affectedLocs) {
			CollectionUtils.addIfNotNullNotExist(learnVars, bkp.getVars());
		}
	}

	public void collectAffectedLocs(int lineNo, List<BreakPoint> affectedLocs, List<BreakPoint> assertLocs,
			JunitResult jresult, AssertionGenerationParams params) throws Exception {
		for (BreakPoint assertLoc : assertLocs) {
			if (assertLoc.getLineNo() >= lineNo) {
				ISlicer slicer = appContext.getSlicer();
				slicer.setFiltering(params.getTestingClassNames(), params.getTestingPkgs());
								
				List<BreakPoint> slicedLocs = slicer.slice(appContext.getAppData(),
					CollectionUtils.listOf(assertLoc),
					JunitUtils.toClassMethodStrs(jresult.getFailTests()));
				
				for (BreakPoint bkp1 : slicedLocs) {
					if (bkp1.getLineNo() == lineNo) {
						for (BreakPoint bkp2 : slicedLocs) {
							if (bkp2.getLineNo() >= lineNo) {
								CollectionUtils.addIfNotNullNotExist(affectedLocs, bkp2);
							}
						}
						break;
					}
				}
			}
		}	
	}
	
	public void templateLearning(List<BreakPoint> learningLocs, AssertionGenerationParams params)
			throws Exception
	{
		
		
		// get random test cases
		List<String> junitClassNames = getRandomTestCases(params);
		
		JunitRunnerParameters junitParams = new JunitRunnerParameters();
		junitParams.setJunitClasses(junitClassNames);
		junitParams.setTestingPkgs(params.getTestingPkgs());
		junitParams.setTestingClassNames(params.getTestingClassNames());
		
		JunitResult jresult = JunitRunner.runTestcases(appContext.getAppData(), junitParams);
		
		// collect assert statements locations
		List<BreakPoint> assertLocs = new ArrayList<BreakPoint>();
		collectAssertStatements(assertLocs, params);
		System.out.println("Assert locations: " + assertLocs);
		
		
		// slice
		ISlicer slicer = appContext.getSlicer();
		slicer.setFiltering(params.getTestingClassNames(), params.getTestingPkgs());
		
		List<BreakPoint> bkps = new ArrayList<BreakPoint>();
		bkps.add(new BreakPoint(params.getTestingClassName(), params.getMethodName(), 7));
		
		System.out.println(jresult.getFailureTraces());
		
		List<BreakPoint> slicedLocs = slicer.slice(appContext.getAppData(),
				// new ArrayList<BreakPoint>(jresult.getFailureTraces()),
				bkps,
				JunitUtils.toClassMethodStrs(jresult.getFailTests()));
		System.out.println("Slicing result: ");
		for (BreakPoint bkp : slicedLocs) System.out.println((bkp.getLineNo()));
		
		AppJavaClassPath appClasspath = appContext.getAppData();
		VariableNameCollector vnc = new VariableNameCollector(VarNameCollectionMode.FULL_NAME, appClasspath .getSrc());
		vnc.updateVariables(slicedLocs);
		
		System.out.println("Updated slicing result: " + slicedLocs);
		
		List<BreakPoint> filterLocations = filterLocations(learningLocs, slicedLocs);
		System.out.println("Filter locations: " + filterLocations);
		
		// collect data at break points
		InvariantMediator im = new InvariantMediator(appClasspath);
		List<String> tests = new ArrayList<String>();
		for (int i = 1; i <= params.getNumberOfTestCases(); i++) {
			tests.add(junitClassNames.get(0) + "." + "test" + i);
		}
		List<BreakpointData> bkpsData = im.debugTestAndCollectData(tests, filterLocations);
		
		// check data with templates
		BreakpointTemplateChecker tc = new BreakpointTemplateChecker(im);
		tc.checkTemplates(bkpsData);
	}

	public void collectAssertStatements(List<BreakPoint> locations, AssertionGenerationParams params) throws Exception {
		// the original file
		File file = new File(ClassUtils.getJFilePath(appContext.getAppData().getSrc(), params.getTestingClassName()));
				
		// parse the original file
		FileInputStream in = new FileInputStream(file);
		CompilationUnit cu = JavaParser.parse(in);
				
		// the visitor used to add assertions
		CollectAssertStmtVisitor visitor = new CollectAssertStmtVisitor(params.getTestingClassName(), params.getMethodName());
				
		// visit the original file
		visitor.visit(cu, locations);
	}
	
	public List<BreakPoint> filterLocations(List<BreakPoint> locations, List<BreakPoint> slicedLocs) {
		List<BreakPoint> filterLocations = new ArrayList<BreakPoint>();
		
		for (BreakPoint location : locations) {
			for (BreakPoint slicedLoc : slicedLocs) {
				if (location.getClassCanonicalName().equals(slicedLoc.getClassCanonicalName()) &&
						location.getMethodName().equals(slicedLoc.getMethodName())) {
					if (location.getLineNo() == slicedLoc.getLineNo()) {
						BreakPoint bp = new BreakPoint(location.getClassCanonicalName(), location.getMethodName(), location.getLineNo());
						bp.setVars(slicedLoc.getVars());
						filterLocations.add(bp);
					}
				}
			}
		}
		
		for (BreakPoint filterLocation : filterLocations) {
			for (BreakPoint slicedLoc : slicedLocs) {
				if (filterLocation.getLineNo() <= slicedLoc.getLineNo()) {
					filterLocation.addVars(slicedLoc.getVars());
				}
			}
		} 
		
		/*
		BreakPoint lastLocation = locations.get(locations.size() - 1);
		BreakPoint bp = new BreakPoint(lastLocation.getClassCanonicalName(), lastLocation.getMethodName(), lastLocation.getLineNo());
		for (BreakPoint filterLocation : filterLocations) {
			bp.addVars(filterLocation.getVars());
		}
		
		filterLocations.add(bp);
		*/
		
		return filterLocations;
	}
	
	public List<BreakPoint> addLearningLocations(AssertionGenerationParams params) throws Exception {
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
				if (body instanceof MethodDeclaration) {
					MethodDeclaration method = (MethodDeclaration) body;
					CFG cfg = CfgFactory.createCFG(method);
					
					CfgEntryNode entry = cfg.getEntry();
					
					List<CfgNode> decisionNodes = new ArrayList<CfgNode>();
					decisionNodes.add(entry);
					cfg.getDecisionNode(entry, decisionNodes, new HashSet<CfgNode>());
					
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
		
		return locations;
	}
	
	public File addAssertionToFile(String srcFolder, String className) throws Exception {
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
		AddAssertStmtVisitor visitor = new AddAssertStmtVisitor(cu.getImports(), subst);
		
		// visit the original file
		List<DebugLineData> arg = new ArrayList<DebugLineData>();
		visitor.visit(cu, arg);
		
		// write the new file
		DebugLineFileWriter writer = new DebugLineFileWriter(srcFolder);
		File newFile = writer.write(arg, className);
		
		// display the new file
		String content = FileUtils.readFileToString(newFile);
		System.out.println(content);
		
		// return the new file
		return newFile;
	}
	
}
