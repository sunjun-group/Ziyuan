package learntest.main;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jacop.core.BoundDomain;
import org.jacop.core.Domain;

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
import learntest.sampling.JacopSelectiveSampling;
import learntest.sampling.jacop.JacopPathSolver;
import learntest.testcase.TestcasesExecutorwithLoopTimes;
import learntest.testcase.data.BreakpointData;
import learntest.testcase.data.BreakpointDataBuilder;
import sav.common.core.SavException;
import sav.common.core.formula.Formula;
import sav.common.core.utils.JunitUtils;
import sav.strategies.dto.AppJavaClassPath;
import sav.strategies.dto.BreakPoint.Variable;
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
	
	public void run() throws ParseException, IOException, SavException, ClassNotFoundException {
		setTarget(LearnTestConfig.filePath, LearnTestConfig.typeName, LearnTestConfig.className, LearnTestConfig.methodName);
		addTestcases(LearnTestConfig.testPath);
		
		createCFG();
		manager = new CfgConditionManager(cfg);
		bkpBuilder = new BreakpointBuilder(className, methodName, variables, cfg, returns);
		bkpBuilder.buildBreakpoints();
		dtBuilder = new BreakpointDataBuilder(bkpBuilder);
		
		ensureTcExecutor();
		tcExecutor.setup(appClassPath, testcases);
		tcExecutor.run();
		Map<DecisionLocation, BreakpointData> result = tcExecutor.getResult();
		tcExecutor.setjResultFileDeleteOnExit(true);
		//tcExecutor.setSingleMode();
		tcExecutor.setInstrMode(true);
		JacopSelectiveSampling selectiveSampling = new JacopSelectiveSampling(tcExecutor);
		DecisionLearner learner = new DecisionLearner(selectiveSampling, manager);
		learner.learn(result);
		List<BreakpointValue> records = learner.getRecords();
		System.out.println("==============================================");
		System.out.println(cfg);
		System.out.println("==============================================");
		List<List<Formula>> paths = manager.buildPaths();
		System.out.println(paths);
		JacopPathSolver solver = new JacopPathSolver(learner.getOriginVars());
		List<Domain[]> solutions = solver.solve(paths);
		solutions.addAll(getSolutions(records, learner.getOriginVars()));
		new TestGenerator().genTestAccordingToSolutions(solutions, learner.getOriginVars());
		System.out.println("Total test cases number: " + selectiveSampling.getTotalNum());
		//PathSolver pathSolver = new PathSolver();
		//List<Result> results = pathSolver.solve(paths);
		//System.out.println(results);
		//new TestGenerator().genTestAccordingToInput(results, pathSolver.getVariables());
		//new TestGenerator().genTestAccordingToInput(results, variables);
		//new TestGenerator().genTestAccordingToInput(results, learner.getLabels());
	}
		
	private List<Domain[]> getSolutions(List<BreakpointValue> records, List<ExecVar> originVars) {
		List<Domain[]> res = new ArrayList<Domain[]>();
		int size = originVars.size();
		for (BreakpointValue record : records) {
			Domain[] solution = new Domain[size];
			for (int i = 0; i < size; i++) {
				int value = record.getValue(originVars.get(i).getLabel(), 0.0).intValue();
				solution[i] = new BoundDomain(value, value);
			}
			res.add(solution);
		}
		return res;
	}

	private void addTestcases(String testClass) throws ClassNotFoundException {
		this.testcases.addAll(JunitUtils.extractTestMethods(Arrays.asList(testClass)));
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

}
