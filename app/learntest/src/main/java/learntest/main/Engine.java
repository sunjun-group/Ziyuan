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
import learntest.cfg.CFG;
import learntest.cfg.CfgCreator;
import learntest.cfg.CfgDecisionNode;
import learntest.data.BreakPointDataBuilder;
import learntest.data.BreakpointData;
import learntest.testcase.MyTestcasesExecutor;
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
	private MyTestcasesExecutor tcExecutor;
	private List<Variable> variables;
	private CFG cfg;
	private List<BreakPoint> breakPoints;
	private BreakPointDataBuilder builder;

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

	public void setTcExecutor(MyTestcasesExecutor tcExecutor) {
		this.tcExecutor = tcExecutor;
	}
	
	public void addBreakpoints(List<Pair<Integer, Integer>> decisions) {
		Set<BreakPoint> bkps = new HashSet<BreakPoint>();
		Map<String, String> decisionMap = new HashMap<String, String>();
		for (Pair<Integer, Integer> decision : decisions) {
			BreakPoint branchBkp = new BreakPoint(className, methodName, decision.first());
			branchBkp.addVars(variables);
			BreakPoint trueBkp = new BreakPoint(className, methodName, decision.second());
			trueBkp.addVars(variables);
			bkps.add(branchBkp);
			bkps.add(trueBkp);
			decisionMap.put(branchBkp.getId(), trueBkp.getId());
		}
		breakPoints = new ArrayList<BreakPoint>(bkps);
		builder = new BreakPointDataBuilder(decisionMap);
	}
	
	public void run() throws ParseException, IOException, SavException {
		//create cfg, bkps and builder
		//createCFG();
		//createBkpsandBuilder();

		//TODO generate test cases randomly
		
		
		ensureTcExecutor();
		tcExecutor.setup(appClassPath, testcases);
		tcExecutor.run(breakPoints);
		List<BreakpointData> result = tcExecutor.getResult();
		for (BreakpointData bkpData : result) {
			System.out.println(bkpData);
		}
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
		Map<String, String> decisionMap = new HashMap<String, String>();
		for (CfgDecisionNode decision : decisions) {
			BreakPoint branchBkp = new BreakPoint(className, methodName, decision.getBeginLine());
			branchBkp.addVars(variables);
			BreakPoint trueBkp = new BreakPoint(className, methodName, decision.getTrueBeginLine());
			trueBkp.addVars(variables);
			bkps.add(branchBkp);
			bkps.add(trueBkp);
			decisionMap.put(branchBkp.getId(), trueBkp.getId());
		}
		breakPoints = new ArrayList<BreakPoint>(bkps);
		builder = new BreakPointDataBuilder(decisionMap);
	}

	public void ensureTcExecutor() {
		if (tcExecutor == null) {
			tcExecutor = new MyTestcasesExecutor(DefaultValues.DEBUG_VALUE_RETRIEVE_LEVEL);
		}
		tcExecutor.setBuilder(builder);
	}	

}
