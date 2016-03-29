package learntest.main;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import icsetlv.DefaultValues;
import icsetlv.variable.JunitDebugger;
import japa.parser.JavaParser;
import japa.parser.ParseException;
import japa.parser.ast.CompilationUnit;
import japa.parser.ast.body.BodyDeclaration;
import japa.parser.ast.body.MethodDeclaration;
import japa.parser.ast.body.Parameter;
import japa.parser.ast.body.TypeDeclaration;
import learntest.cfg.CFG;
import learntest.cfg.CfgCreator;
import learntest.cfg.CfgDecisionNode;
import learntest.testcase.TestcasesExecutorwithoutLoopTimes;
import learntest.testcase.data.BreakpointDataBuilder;
import sav.common.core.Pair;
import sav.common.core.SavException;
import sav.common.core.utils.JunitUtils;
import sav.strategies.dto.AppJavaClassPath;
import sav.strategies.dto.BreakPoint;
import sav.strategies.dto.BreakPoint.Variable;
import sav.strategies.dto.ClassLocation;

public class Engine {
	
	private AppJavaClassPath appClassPath;
	private String filePath;
	private String typeName;
	private String className;
	private String methodName;
	private List<String> testcases = new ArrayList<String>();
	private JunitDebugger tcExecutor;
	private List<Variable> variables;
	private CFG cfg;
	private List<BreakPoint> breakPoints;
	private BreakpointDataBuilder builder;

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

	public void setTcExecutor(JunitDebugger tcExecutor) {
		this.tcExecutor = tcExecutor;
	}
	
	public void addEntryBreakpoint(BreakPoint bkp) {
		if (breakPoints == null) {
			breakPoints = new ArrayList<BreakPoint>();
		}
		bkp.addVars(variables);
		breakPoints.add(bkp);
	}
	
	public void setStructure(List<Pair<Integer, Integer>> decisions) {
		Set<BreakPoint> bkps = new HashSet<BreakPoint>();
		List<Pair<ClassLocation, BreakPoint>> decisionList = new ArrayList<Pair<ClassLocation,BreakPoint>>();
		for (Pair<Integer, Integer> decision : decisions) {
			ClassLocation decisionLocation = new ClassLocation(className, methodName, decision.first());
			BreakPoint trueBkp = new BreakPoint(className, methodName, decision.second());
			bkps.add(trueBkp);
			decisionList.add(new Pair<ClassLocation, BreakPoint>(decisionLocation, trueBkp));
		}
		if (breakPoints == null) {
			breakPoints = new ArrayList<BreakPoint>();
		}
		for (BreakPoint bkp : bkps) {
			breakPoints.add(bkp);
		}
		//builder = new BreakpointDataBuilder(decisionList);
	}
	
	public void run() throws ParseException, IOException, SavException {
		//create cfg, bkps and builder
		//createCFG();
		//addEntryBreakpoint(bkp);
		//createBkpsandBuilder();

		//TODO generate test cases randomly
		
		
		ensureTcExecutor();
		tcExecutor.setup(appClassPath, testcases);
		tcExecutor.run(breakPoints);
		//tcExecutor.getResult();
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

	private void createBkpsandBuilder() {
		//TODO get cfg nodes
		List<CfgDecisionNode> decisions = null;
		Set<BreakPoint> bkps = new HashSet<BreakPoint>();
		List<Pair<ClassLocation, BreakPoint>> decisionList = new ArrayList<Pair<ClassLocation,BreakPoint>>();
		for (CfgDecisionNode decision : decisions) {
			ClassLocation decisionLocation = new ClassLocation(className, methodName, decision.getBeginLine());
			BreakPoint trueBkp = new BreakPoint(className, methodName, decision.getTrueBeginLine());
			bkps.add(trueBkp);
			decisionList.add(new Pair<ClassLocation, BreakPoint>(decisionLocation, trueBkp));
		}
		if (breakPoints == null) {
			breakPoints = new ArrayList<BreakPoint>();
		}
		for (BreakPoint bkp : bkps) {
			breakPoints.add(bkp);
		}
		//builder = new BreakpointDataBuilder(decisionList);
	}

	public void ensureTcExecutor() {
		if (tcExecutor == null) {
			tcExecutor = new TestcasesExecutorwithoutLoopTimes(DefaultValues.DEBUG_VALUE_RETRIEVE_LEVEL);
		}
		//tcExecutor.setBuilder(builder);
	}	

}
