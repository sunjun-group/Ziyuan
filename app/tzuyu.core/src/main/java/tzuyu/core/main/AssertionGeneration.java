package tzuyu.core.main;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;

import assertion.template.checker.TemplateChecker;
import assertion.visitor.AddAssertStmtVisitor;
import assertion.visitor.GetLearningLocationsVisitor;
import icsetlv.InvariantMediator;
import icsetlv.common.dto.BreakpointData;
import invariant.templates.Template;
import japa.parser.JavaParser;
import japa.parser.ast.CompilationUnit;
import mutation.io.DebugLineFileWriter;
import mutation.mutator.VariableSubstitution;
import mutation.mutator.insertdebugline.DebugLineData;
import mutation.parser.ClassAnalyzer;
import mutation.parser.ClassDescriptor;
import mutation.parser.JParser;
import sav.common.core.Pair;
import sav.common.core.utils.ClassUtils;
import sav.strategies.IApplicationContext;
import sav.strategies.dto.BreakPoint;
import tzuyu.core.inject.ApplicationData;
import tzuyu.core.mutantbug.FilesBackup;
import tzuyu.core.mutantbug.Recompiler;

public class AssertionGeneration extends TzuyuCore {

	public AssertionGeneration(IApplicationContext appContext, ApplicationData appData) {
		super(appContext, appData);
	}
		
	@Override
	public void genAssertion(AssertionGenerationParams params) throws Exception {
		
		FilesBackup backup = null;
		
		try {
			String srcFolder = appData.getAppSrc();
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
			Recompiler recompiler = new Recompiler(appData.initVmConfig());
			recompiler.recompileJFile(appData.getAppTarget(), newFile);
			
			// add locations used to learn new assertion
			List<BreakPoint> locations = addLearningLocations(srcFolder, className);
			
			// learn assertions for new locations
			// machineLearningForAssertion(locations, params);
			templateLearning(locations, params);
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
			} catch (Throwable exception) {

			}
		}
		
		List<String> tests = new ArrayList<String>();
		for (int i = 1; i <= params.getNumberOfTestCases(); i++) {
			tests.add(junitClassNames.get(0) + "." + "test" + i);
		}
		
		return tests;
	}
	
	public void templateLearning(List<BreakPoint> locations, AssertionGenerationParams params)
			throws Exception
	{
		// get random test cases
		List<String> tests = getRandomTestCases(params);
		
		// collect data at break points
		InvariantMediator im = new InvariantMediator(appData.getAppClassPath());
		List<BreakpointData> bkpsData = im.debugTestAndCollectData(tests, locations);
		
		// check data with templates
		TemplateChecker tc = new TemplateChecker(im);
		List<Pair<BreakpointData, List<Template>>> bkpsTemplates = tc.checkTemplates(bkpsData);
		// SelectiveSampling ss = new SelectiveSampling(im);
	}

	public List<BreakPoint> addLearningLocations(String srcFolder, String className) throws Exception {
		List<BreakPoint> locations = new ArrayList<BreakPoint>();
		
		File file = new File(ClassUtils.getJFilePath(srcFolder, className));
		
		FileInputStream in = new FileInputStream(file);
		CompilationUnit cu = JavaParser.parse(in);
		
		GetLearningLocationsVisitor visitor = new GetLearningLocationsVisitor(className);
		
		visitor.visit(cu, locations);
		
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
