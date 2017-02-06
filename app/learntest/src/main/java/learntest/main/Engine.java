package learntest.main;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jacop.core.Domain;
import org.jacop.floats.core.FloatIntervalDomain;

import icsetlv.DefaultValues;
import icsetlv.common.dto.BreakpointValue;
import japa.parser.JavaParser;
import japa.parser.ParseException;
import japa.parser.ast.CompilationUnit;
import japa.parser.ast.body.BodyDeclaration;
import japa.parser.ast.body.MethodDeclaration;
import japa.parser.ast.body.Parameter;
import japa.parser.ast.body.TypeDeclaration;
import learntest.breakpoint.data.BreakpointBuilder;
import learntest.breakpoint.data.DecisionLocation;
import learntest.cfg.CFG;
import learntest.cfg.CfgCreator;
import learntest.cfg.CfgDecisionNode;
import learntest.cfg.traveller.CfgConditionManager;
import learntest.sampling.JavailpSelectiveSampling;
import learntest.testcase.TestcasesExecutorwithLoopTimes;
import learntest.testcase.data.BreakpointData;
import learntest.testcase.data.BreakpointDataBuilder;
import learntest.util.LearnTestUtil;
import sav.common.core.SavException;
import sav.common.core.utils.CollectionUtils;
import sav.settings.SAVExecutionTimeOutException;
import sav.settings.SAVTimer;
import sav.strategies.dto.AppJavaClassPath;
import sav.strategies.dto.BreakPoint.Variable;
import sav.strategies.dto.execute.value.ExecValue;
import sav.strategies.dto.execute.value.ExecVar;

public class Engine {
	
	private AppJavaClassPath appClassPath;
	private String filePath;
	private String typeName;
	private String className;
	private String methodName;
	private List<String> testcases = new ArrayList<String>();
	private TestcasesExecutorwithLoopTimes tcExecutor;
	private List<Variable> variables;
	private CFG cfg;
	private CfgConditionManager manager;
	private BreakpointBuilder bkpBuilder;
	private BreakpointDataBuilder dtBuilder;
	
	//To handle recursion,can only handle recursion with returns
	private Set<Integer> returns;

	public Engine(AppJavaClassPath appClassPath){
		this.appClassPath = appClassPath;
	}
	
	private void setTarget(String filePath, String typeName, String className, String methodName) {
		this.filePath = filePath;
		this.typeName = typeName;
		this.className = className;
		this.methodName = methodName;
	}

	public void setTcExecutor(TestcasesExecutorwithLoopTimes tcExecutor) {
		this.tcExecutor = tcExecutor;
	}
	
	public RunTimeInfo run(boolean random) throws ParseException, IOException, SavException, ClassNotFoundException {
		SAVTimer.startCount();
		
		String filePath = LearnTestConfig.getTestClassFilePath();
		filePath = filePath.substring(6, filePath.length());
		
		setTarget(filePath, LearnTestConfig.getSimpleClassName(), LearnTestConfig.testClassName, LearnTestConfig.testMethodName);
		addTestcases(LearnTestConfig.getTestClass(LearnTestConfig.isL2TApproach));
		
		if (testcases == null || testcases.isEmpty()) {
			return null;
		}
		
		createCFG();
		manager = new CfgConditionManager(cfg);
		bkpBuilder = new BreakpointBuilder(className, methodName, variables, cfg, returns);
		bkpBuilder.buildBreakpoints();
		dtBuilder = new BreakpointDataBuilder(bkpBuilder);
		
		long time = -1;
		double coverage = 0;
		int testCnt = 1;
		
		//JacopSelectiveSampling selectiveSampling = null;
		JavailpSelectiveSampling selectiveSampling = null;
		DecisionLearner learner = null;
		
		try{
			ensureTcExecutor();
			tcExecutor.setup(appClassPath, testcases);
			tcExecutor.run();
			Map<DecisionLocation, BreakpointData> result = tcExecutor.getResult();
			
			if (tcExecutor.getCurrentTestInputValues().isEmpty()) {
				return null;
			}
			
			if (result.isEmpty()) {
				List<BreakpointValue> tests = tcExecutor.getCurrentTestInputValues();
				if (tests != null && !tests.isEmpty()) {
					BreakpointValue test = tests.get(0);
					Set<ExecVar> allVars = new HashSet<ExecVar>();
					collectExecVar(test.getChildren(), allVars);
					List<ExecVar> vars = new ArrayList<ExecVar>(allVars);
					List<BreakpointValue> list = new ArrayList<BreakpointValue>();
					list.add(test);
					new TestGenerator().genTestAccordingToSolutions(getSolutions(list, vars), vars);
					System.out.println("Total test cases number: " + testCnt);
					coverage = 1;
				}
			} else {
				/*List<Domain[]> values = null;
				List<BreakpointValue> tests = tcExecutor.getCurrentTestInputValues();
				if (tests != null) {
					Set<ExecVar> allVars = new HashSet<ExecVar>();
					for (BreakpointValue test : tests) {
						collectExecVar(test.getChildren(), allVars);
					}
					List<ExecVar> vars = new ArrayList<ExecVar>(allVars);
					values = getFullSolutions(tests, vars);
				}*/
				tcExecutor.setjResultFileDeleteOnExit(true);
				//tcExecutor.setSingleMode();
				tcExecutor.setInstrMode(true);
				//selectiveSampling = new JacopSelectiveSampling(tcExecutor);
				selectiveSampling = new JavailpSelectiveSampling(tcExecutor);
				/*if (values != null) {
					selectiveSampling.addPrevValues(values);
				}*/
				selectiveSampling.addPrevValues(tcExecutor.getCurrentTestInputValues());
				learner = new DecisionLearner(selectiveSampling, manager, random);
				learner.learn(result);
				//List<BreakpointValue> records = learner.getRecords();
				/*System.out.println("==============================================");
				System.out.println(cfg);
				System.out.println("==============================================");*/
				/*List<List<Formula>> paths = manager.buildPaths();
				System.out.println(paths);
				JacopPathSolver solver = new JacopPathSolver(learner.getOriginVars());
				List<Domain[]> solutions = solver.solve(paths);*/
				//solutions.addAll(getSolutions(records, learner.getOriginVars()));
				//new TestGenerator().genTestAccordingToSolutions(solutions, learner.getOriginVars());
				
				if (learner != null) {
					coverage = learner.getCoverage();
				}
				
				List<Domain[]> domainList = getSolutions(learner.getRecords(), learner.getOriginVars());
				
				new TestGenerator().genTestAccordingToSolutions(domainList, learner.getOriginVars());
				testCnt = selectiveSampling.getTotalNum();
				System.out.println("Total test cases number: " + testCnt);
			}
			
			time = SAVTimer.getExecutionTime();		
		} catch(SAVExecutionTimeOutException e){
			if (learner != null) {
				coverage = learner.getCoverage();
				List<Domain[]> domainList = getSolutions(learner.getRecords(), learner.getOriginVars());
				new TestGenerator().genTestAccordingToSolutions(domainList, learner.getOriginVars());
				testCnt = selectiveSampling.getTotalNum();
				System.out.println("Total test cases number: " + testCnt);
			}
			e.printStackTrace();
		}		
		//PathSolver pathSolver = new PathSolver();
		//List<Result> results = pathSolver.solve(paths);
		//System.out.println(results);
		//new TestGenerator().genTestAccordingToInput(results, pathSolver.getVariables());
		//new TestGenerator().genTestAccordingToInput(results, variables);
		//new TestGenerator().genTestAccordingToInput(results, learner.getLabels());
		
		
		
		RunTimeInfo info = new RunTimeInfo(time, coverage, testCnt);
		return info;
	}
	
	/*private List<Domain[]> getFullSolutions(List<BreakpointValue> records, List<ExecVar> originVars) {
		List<Domain[]> res = new ArrayList<Domain[]>();
		int size = originVars.size();
		for (BreakpointValue record : records) {
			Domain[] solution = new Domain[size + (size + 1) * size / 2];
			int i = 0;
			for (; i < size; i++) {
				double value = record.getValue(originVars.get(i).getLabel(), 0.0).doubleValue();
				solution[i] = new FloatIntervalDomain(value, value);
			}
			for(int j = 0; j < size; j ++) {
				double value = record.getValue(originVars.get(j).getLabel(), 0.0).doubleValue();
				for(int k = j; k < size; k ++) {
					double tmp = value * record.getValue(originVars.get(k).getLabel(), 0.0).doubleValue();
					solution[i ++] = new FloatIntervalDomain(tmp, tmp);
				}
			}
			res.add(solution);
		}
		return res;
	}*/
		
	private List<Domain[]> getSolutions(List<BreakpointValue> records, List<ExecVar> originVars) {
		List<Domain[]> res = new ArrayList<Domain[]>();
		int size = originVars.size();
		for (BreakpointValue record : records) {
			Domain[] solution = new Domain[size];
			for (int i = 0; i < size; i++) {
				double value = record.getValue(originVars.get(i).getLabel(), 0.0).doubleValue();
				solution[i] = new FloatIntervalDomain(value, value);
			}
			res.add(solution);
		}
		return res;
	}

	private void addTestcases(String testClass) throws ClassNotFoundException {
		
		org.eclipse.jdt.core.dom.CompilationUnit cu = LearnTestUtil.findCompilationUnitInProject(testClass);
		List<org.eclipse.jdt.core.dom.MethodDeclaration> mList = LearnTestUtil.findTestingMethod(cu);
		
		List<String> result = new ArrayList<String>();
		for(org.eclipse.jdt.core.dom.MethodDeclaration m: mList){
			String testcaseName = testClass + "." + m.getName();
			result.add(testcaseName);
		}
		
		this.testcases.addAll(result);
	}
	
	private void createCFG() throws ParseException, IOException {
		CompilationUnit cu = JavaParser.parse(new File(filePath));
		for (TypeDeclaration type : cu.getTypes()) {
			if (type.getName().equals(typeName)) {
				for (BodyDeclaration body : type.getMembers()) {
					if (body instanceof MethodDeclaration) {
						MethodDeclaration method = (MethodDeclaration) body;
						if (method.getName().equals(methodName)) {
							variables = new ArrayList<Variable>();
							List<Parameter> parameters = method.getParameters();
							if (parameters != null) {
								for (Parameter parameter : parameters) {
									variables.add(new Variable(parameter.getId().getName()));
								}
							}							
							CfgCreator creator = new CfgCreator();
							cfg = creator.dealWithBreakStmt(creator.dealWithReturnStmt(creator.toCFG(method)));
							returns = new HashSet<Integer>();
							List<CfgDecisionNode> returnNodeList = creator.getReturnNodeList();
							for (CfgDecisionNode returnNode : returnNodeList) {
								returns.add(returnNode.getBeginLine());
							}
							return;
						}
					}
				}
				return;
			}
		}
	}

	private void ensureTcExecutor() {
		if (tcExecutor == null) {
			tcExecutor = new TestcasesExecutorwithLoopTimes(DefaultValues.DEBUG_VALUE_RETRIEVE_LEVEL);
		}
		tcExecutor.setBuilder(dtBuilder);
		tcExecutor.setBkpBuilder(bkpBuilder);
	}	
	
	private void collectExecVar(List<ExecValue> vals, Set<ExecVar> vars) {
		if (CollectionUtils.isEmpty(vals)) {
			return;
		}
		for (ExecValue val : vals) {
			if (val == null || CollectionUtils.isEmpty(val.getChildren())) {
				String varId = val.getVarId();
				vars.add(new ExecVar(varId, val.getType()));
			}
			collectExecVar(val.getChildren(), vars);
		}
	}

}
