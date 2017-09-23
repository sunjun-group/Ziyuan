package tzuyu.core.main;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.apache.commons.io.FileUtils;

import datastructure.CollectAffectVarsVisitor;
import datastructure.CollectConstsVisitor;
import datastructure.CollectLearnLocsVisitor;
import datastructure.CollectVisibleVarsVisitor;
import datastructure.DataStructureTemplate;
import japa.parser.JavaParser;
import japa.parser.ast.CompilationUnit;
import japa.parser.ast.visitor.VoidVisitorAdapter;
import sav.common.core.Pair;
import sav.common.core.utils.ClassUtils;
import sav.strategies.IApplicationContext;
import sav.strategies.dto.AppJavaClassPath;
import sav.strategies.dto.BreakPoint;
import sav.strategies.dto.BreakPoint.Variable;
import sav.strategies.junit.JunitResult;
import sav.strategies.junit.JunitRunner;
import sav.strategies.junit.JunitRunnerParameters;

public class DataStructureGeneration extends TzuyuCore {
	
	public List<DataStructureTemplate> heapTemplates;
	
	public List<DataStructureTemplate> pureTemplates;
	
	public List<DataStructureTemplate> bagTemplates;
	
	public List<Integer> consts;
	
	private List<String> allTests;
	
	private List<String> passTests;
	
	private String templatesPath = "/Users/HongLongPham/Workspace/testdata/data-structure/templates/";
	
	private int indexTest = 0;
	
	private enum TemplateType {
		HEAP, PURE, BAG
	}
	
	public enum CodeType {
		CHECKING, NULL, NEW, ITSELF, OTHERS, SWAP, BAG, FIELD, CONST, INC
	}
	
	public DataStructureGeneration(IApplicationContext appContext) {
		super(appContext);
	}
	
	public void genAssertion(DataStructureGenerationParams params) throws Exception {
		heapTemplates = getTemplates(TemplateType.HEAP);
		pureTemplates = getTemplates(TemplateType.PURE);
		bagTemplates = getTemplates(TemplateType.BAG);
		
		allTests = new ArrayList<String>();
		passTests = new ArrayList<String>();
		
		normalizeCode(params);
		
		String target = appContext.getAppData().getTarget();
		String resultPath = target.substring(0, target.lastIndexOf('/')) + "/results/";
		
		if (!pureTemplates.isEmpty()) {
			consts = collectConsts(params);
			System.out.println(consts);
		}
		
		List<BreakPoint> learnLocs = collectLearningLocations(params);
		params.setJunitClassNames(getRandomTestCases(params));
		
		System.out.println(params.getJunitClassNames());
		
		List<Boolean> testResults = new ArrayList<Boolean>();//runTestCases(params, params.getJunitClassNames(), false);
		
//		for (int i = 0; i >= 0; i--) {
		for (int i = learnLocs.size() - 2; i >= 1; i--) {
			params.setStartTime(System.currentTimeMillis());
			
			FileUtils.cleanDirectory(new File(resultPath)); 
			
			System.out.println(learnLocs.get(i));
			templateLearning(params, learnLocs.get(i), testResults);
		}
	}

	public List<String> getRandomTestCases(DataStructureGenerationParams params) {
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
	
	public void normalizeCode(DataStructureGenerationParams params) throws Exception {
		String srcFolder = appContext.getAppData().getSrc();
		String className = params.getTestingClassName();
		
		File file = new File(ClassUtils.getJFilePath(srcFolder, className));
		
		FileInputStream in = new FileInputStream(file);
		CompilationUnit cu = JavaParser.parse(in);
		
		PrintWriter out = new PrintWriter(file);
		
		out.write(cu.toString());
		out.flush();
		
		in.close();
		out.close();
	}
	
	public void setTemplatesPath(String path) {
		templatesPath = path;
	}
	
	public List<Boolean> runTestCases(DataStructureGenerationParams params,
			List<String> junitClassNames, boolean runPassTests) throws Exception {
		List<Boolean> testResults = new ArrayList<Boolean>();
		
		JunitRunnerParameters junitParams = new JunitRunnerParameters();
		junitParams.setJunitClasses(junitClassNames);
		junitParams.setTestingPkgs(params.getTestingPkgs());
		junitParams.setTestingClassNames(params.getTestingClassNames());
		
		if (allTests.isEmpty()) {
			java.util.Collections.sort(junitParams.getClassMethods(), new Comparator<String>() {
					@Override
					public int compare(String s1, String s2) {
						if (s1.length() < s2.length())
							return -1;
						else if (s1.length() > s2.length())
							return 1;
						else
							return s1.compareTo(s2);
					}
				});
				
			allTests = new ArrayList<String>(junitParams.getClassMethods());
		}
		
		List<String> tmp = new ArrayList<String>();
		List<String> tests = new ArrayList<String>();
		
		if (runPassTests) tests = new ArrayList<String>(passTests);
		else tests = new ArrayList<String>(allTests);
		
		for (String test : tests) {
			junitParams.setClassMethods(Arrays.asList(test));
			
			ExecutorService executor = Executors.newSingleThreadExecutor();
			Future<JunitResult> future = executor.submit(new Task(appContext.getAppData(), junitParams));
			
			try {
	            JunitResult jresult = future.get(3, TimeUnit.SECONDS);
	            
	            if (jresult.getTestResult().get(0)) {
//	            	fillInTestResult(testResults, true);
	            	testResults.add(true);
//	            	if (passTests.isEmpty()) tmp.add(test);
	            } else {
//	            	fillInTestResult(testResults, false);
	            	testResults.add(false);
	            }
	        } catch (TimeoutException e) {
	            future.cancel(true);
//	            fillInTestResult(testResults, false);
	            testResults.add(false);
	        }

	        executor.shutdownNow();
		}
		
		if (passTests.isEmpty()) passTests.addAll(tmp);
		
		return testResults;
	}
	
	private void fillInTestResult(List<Boolean> testResults, boolean b) {
		String target = appContext.getAppData().getTarget();
		String resultPath = target.substring(0, target.lastIndexOf('/')) + "/results/";
		
		File folder = new File(resultPath);
		File[] files = folder.listFiles(new FilenameFilter() {

			@Override
			public boolean accept(File dir, String name) {
				return !name.equals(".DS_Store");
			}
		});
//				(dir, name) -> !name.equals(".DS_Store"));
		
		if (files.length > 0) {
			File fst = files[0];
			try {
				int lines = countLines(fst.getAbsolutePath());
				
				for (int i = indexTest; i < lines; i++) {
					testResults.add(b);
				}
				
				indexTest = lines;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			testResults.add(b);
		}
	}
	
	public static int countLines(String filename) throws IOException {
	    InputStream is = new BufferedInputStream(new FileInputStream(filename));
	    try {
	        byte[] c = new byte[1024];
	        int count = 0;
	        int readChars = 0;
	        boolean empty = true;
	        while ((readChars = is.read(c)) != -1) {
	            empty = false;
	            for (int i = 0; i < readChars; ++i) {
	                if (c[i] == '\n') {
	                    ++count;
	                }
	            }
	        }
	        return (count == 0 && !empty) ? 1 : count;
	    } finally {
	        is.close();
	    }
	}
	
	class Task implements Callable<JunitResult> {
		private AppJavaClassPath javaPath;

		private JunitRunnerParameters junitParams;

		public Task(AppJavaClassPath javaPath, JunitRunnerParameters junitParams) {
			this.javaPath = javaPath;
			this.junitParams = junitParams;
		}

		@Override
		public JunitResult call() throws Exception {
			JunitResult jresult = JunitRunner.runTestcases(appContext.getAppData(), junitParams);
			return jresult;
		}
	}
	
	private void templateLearning(DataStructureGenerationParams params,
			BreakPoint learnLoc, List<Boolean> testResults) throws Exception {
		List<Variable> learnVars = collectLearningVariables(params, learnLoc);
		System.out.println(learnVars);
		
		DataStructureLearning learner = new DataStructureLearning(this);
		learner.learnInv(params, learnLoc, learnVars, testResults);
	}
	private List<DataStructureTemplate> getTemplates(TemplateType index)
			throws Exception {
		List<DataStructureTemplate> templates = new ArrayList<DataStructureTemplate>();
		
		BufferedReader br = null;
		
		switch (index) {
		case HEAP:
			br = new BufferedReader(new FileReader(templatesPath + "heap.txt"));
			break;
		case PURE:
			br = new BufferedReader(new FileReader(templatesPath + "pure.txt"));
			break;
		case BAG:
			br = new BufferedReader(new FileReader(templatesPath + "bag.txt"));
			break;
		}
		
		String s = br.readLine();
		
		while (s != null) {
			if (s.startsWith("%")) {
				s = br.readLine();
				continue;
			}
			
			String[] sl = s.split(";");
			String templateName = sl[0];
			String heapArgc = sl[1];
			String pureArgc = sl[2];
			
			DataStructureTemplate t = new DataStructureTemplate(templateName,
					Integer.parseInt(heapArgc), Integer.parseInt(pureArgc));
			for (int i = 3; i < sl.length; i++) {
				t.addProperty(sl[i]);
			}
			
			templates.add(t);
			
			s = br.readLine();
		}
		
		br.close();
		
		return templates;
	}
	
	public List<BreakPoint> collectLearningLocations(DataStructureGenerationParams params)
			throws Exception {
		String srcFolder = appContext.getAppData().getSrc();
		String className = params.getTestingClassName();
		String methodName = params.getMethodName();
		
		List<BreakPoint> locations = new ArrayList<BreakPoint>();
		
		File file = new File(ClassUtils.getJFilePath(srcFolder, className));
		
		FileInputStream in = new FileInputStream(file);
		CompilationUnit cu = JavaParser.parse(in);
		
		VoidVisitorAdapter visitor = null;
		visitor = new CollectLearnLocsVisitor(className, methodName);
		
		visitor.visit(cu, locations);

		return locations;
	}
	
	private List<Variable> collectLearningVariables(DataStructureGenerationParams params,
			BreakPoint learnLoc) throws Exception {
		String srcFolder = appContext.getAppData().getSrc();
		String className = params.getTestingClassName();
		String methodName = params.getMethodName();
		
		int lineNo = learnLoc.getLineNo();
		
		File file = new File(ClassUtils.getJFilePath(srcFolder, className));
		
		FileInputStream in = new FileInputStream(file);
		CompilationUnit cu = JavaParser.parse(in);
		
		VoidVisitorAdapter visitor = null;
		
		List<String> affectVars = new ArrayList<String>();
		visitor = new CollectAffectVarsVisitor(className, methodName, lineNo);
		visitor.visit(cu, affectVars);
		
//		System.out.println(affectVars);
		
		List<Pair<String,String>> visibleVars = new ArrayList<Pair<String,String>>();
		visitor = new CollectVisibleVarsVisitor(className, methodName, lineNo);
		visitor.visit(cu, visibleVars);
		
//		System.out.println(visibleVars);
		
		List<Variable> vars = new ArrayList<Variable>();
		
		for (Pair<String,String> visibleVar : visibleVars) {
			String type = visibleVar.a;
			String name = visibleVar.b;
			
			if (affectVars.contains(name)) {
				Variable var = new Variable(name, type, false);
				vars.add(var);
			}
		}
		
//		InvariantMediator im = new InvariantMediator(appContext.getAppData());
//		TestcasesExecutor tcExecutor = new TestcasesExecutor(params.getValueRetrieveLevel());
//		
//		tcExecutor.setValueExtractor(new VisibleVariableExtractor());
//		im.setTcExecutor(tcExecutor);
//		
//		List<String> tests = Arrays.asList(passTests.get(0));
//				
//		// a lot of overhead
//		// it is better if we have another way to collect visible variables
//		List<BreakpointData> bkpsData = im.debugTestAndCollectData(tests, CollectionUtils.listOf(learnLoc));
//		BreakpointValue bkpVal = bkpsData.get(0).getPassValues().get(0);
//			
//		for (com.sun.jdi.LocalVariable lv : bkpVal.getVars()) {
//			String name = lv.name();
//			if (affectVars.contains(name)) {
//				Variable var = new Variable(lv.name(), lv.typeName(), false);
//				vars.add(var);
//			}
//		}
		
		return vars;
	}
	
	private List<Integer> collectConsts(DataStructureGenerationParams params) 
			throws Exception {
		String srcFolder = appContext.getAppData().getSrc();
		String className = params.getTestingClassName();
		String methodName = params.getMethodName();
		
		List<Integer> consts = new ArrayList<Integer>();
		consts.add(new Integer(0));
		
		File file = new File(ClassUtils.getJFilePath(srcFolder, className));
		
		FileInputStream in = new FileInputStream(file);
		CompilationUnit cu = JavaParser.parse(in);
		
		VoidVisitorAdapter visitor = null;
		visitor = new CollectConstsVisitor(className, methodName);
		
		visitor.visit(cu, consts);

		return consts;
	}

}
