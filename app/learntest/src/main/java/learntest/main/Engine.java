package learntest.main;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import icsetlv.DefaultValues;
import japa.parser.JavaParser;
import japa.parser.ParseException;
import japa.parser.ast.CompilationUnit;
import japa.parser.ast.body.BodyDeclaration;
import japa.parser.ast.body.MethodDeclaration;
import japa.parser.ast.body.Parameter;
import japa.parser.ast.body.TypeDeclaration;
import learntest.breakpoint.data.BreakpointBuilder;
import learntest.breakpoint.data.DecisionLocation;
import learntest.breakpoint.data.LoopTimesBkpBuilder;
import learntest.breakpoint.data.TrueBkpBuilder;
import learntest.cfg.CFG;
import learntest.cfg.CfgCreator;
import learntest.testcase.TestcasesExecutorwithLoopTimes;
import learntest.testcase.data.BreakpointData;
import learntest.testcase.data.BreakpointDataBuilder;
import sav.common.core.Pair;
import sav.common.core.SavException;
import sav.common.core.utils.JunitUtils;
import sav.strategies.dto.AppJavaClassPath;
import sav.strategies.dto.BreakPoint;
import sav.strategies.dto.BreakPoint.Variable;

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
	private List<BreakPoint> breakPoints;
	private BreakpointBuilder bkpBuilder;
	private BreakpointDataBuilder dtBuilder;

	public Engine(AppJavaClassPath appClassPath){
		this.appClassPath = appClassPath;
	}
	
	public void setTarget(String filePath, String typeName, String className, String methodName) {
		this.filePath = filePath;
		this.typeName = typeName;
		this.className = className;
		this.methodName = methodName;
	}
	
	public void addTestcases(String testClass) throws ClassNotFoundException {
		addTestcases(JunitUtils.extractTestMethods(Arrays.asList(testClass)));
	}

	private void addTestcases(List<String> testcases) {
		this.testcases.addAll(testcases);
	}

	public void setTcExecutor(TestcasesExecutorwithLoopTimes tcExecutor) {
		this.tcExecutor = tcExecutor;
	}
	
	public void addEntryBreakpoint(BreakPoint bkp) {
		if (breakPoints == null) {
			breakPoints = new ArrayList<BreakPoint>();
		}
		breakPoints.remove(bkp);
		bkp.addVars(variables);
		breakPoints.add(bkp);
	}
	
	public void setStructure(List<Pair<Integer, Integer>> decisions) {
		Set<BreakPoint> bkps = new HashSet<BreakPoint>();
		Map<BreakPoint, List<DecisionLocation>> decisionMap = new HashMap<BreakPoint, List<DecisionLocation>>();
		for (Pair<Integer, Integer> decision : decisions) {
			DecisionLocation location = new DecisionLocation(className, methodName, decision.first(), true);
			BreakPoint trueBkp = new BreakPoint(className, methodName, decision.second());
			bkps.add(trueBkp);
			List<DecisionLocation> locations = decisionMap.get(trueBkp);
			if (locations == null) {
				locations = new ArrayList<DecisionLocation>();
				decisionMap.put(trueBkp, locations);
			}
			locations.add(location);
		}
		if (breakPoints == null) {
			breakPoints = new ArrayList<BreakPoint>();
		}
		for (BreakPoint bkp : bkps) {
			breakPoints.add(bkp);
		}
		dtBuilder = new BreakpointDataBuilder(decisionMap);
	}
	
	public void run(boolean loopMode) throws ParseException, IOException, SavException {
		//createCFG();
		/*if (loopMode) {
			bkpBuilder = new LoopTimesBkpBuilder(className, methodName, variables, cfg);
		} else {
			bkpBuilder = new TrueBkpBuilder(className, methodName, variables, cfg);
		}
		Map<BreakPoint, List<DecisionLocation>> decisionMap = bkpBuilder.buildBreakpoints();
		dtBuilder = new BreakpointDataBuilder(decisionMap);
		breakPoints = bkpBuilder.getBreakPoints();*/		

		//TODO generate test cases randomly		
		
		ensureTcExecutor();
		tcExecutor.setup(appClassPath, testcases);
		tcExecutor.run(breakPoints);
		List<BreakpointData> result = tcExecutor.getResult();
		tcExecutor.setjResultFileDeleteOnExit(true);
	}

	public void createCFG() throws ParseException, IOException {
		CompilationUnit cu = JavaParser.parse(getClass().getResourceAsStream(filePath));
		for (TypeDeclaration type : cu.getTypes()) {
			if (type.getName().equals(typeName)) {
				for (BodyDeclaration body : type.getMembers()) {
					if (body instanceof MethodDeclaration) {
						MethodDeclaration method = (MethodDeclaration) body;
						if (method.getName().equals(methodName)) {
							variables = new ArrayList<Variable>();
							List<Parameter> parameters = method.getParameters();
							for (Parameter parameter : parameters) {
								variables.add(new Variable(parameter.getId().getName()));
							}
							cfg = CfgCreator.createCFG(method);
							return;
						}
					}
				}
				return;
			}
		}
	}

	public void ensureTcExecutor() {
		if (tcExecutor == null) {
			tcExecutor = new TestcasesExecutorwithLoopTimes(DefaultValues.DEBUG_VALUE_RETRIEVE_LEVEL);
		}
		tcExecutor.setBuilder(dtBuilder);
	}	

}
