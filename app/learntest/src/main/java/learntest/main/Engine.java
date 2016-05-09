package learntest.main;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import icsetlv.DefaultValues;
import japa.parser.JavaParser;
import japa.parser.ParseException;
import japa.parser.ast.CompilationUnit;
import japa.parser.ast.body.BodyDeclaration;
import japa.parser.ast.body.MethodDeclaration;
import japa.parser.ast.body.Parameter;
import japa.parser.ast.body.TypeDeclaration;
import learntest.breakpoint.data.BreakpointBuilder;
import learntest.cfg.CFG;
import learntest.cfg.CfgCreator;
import learntest.sampling.SelectiveSampling;
import learntest.testcase.TestcasesExecutorwithLoopTimes;
import learntest.testcase.data.BreakpointData;
import learntest.testcase.data.BreakpointDataBuilder;
import sav.common.core.SavException;
import sav.common.core.utils.JunitUtils;
import sav.strategies.dto.AppJavaClassPath;
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
	private BreakpointBuilder bkpBuilder;
	private BreakpointDataBuilder dtBuilder;

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
		bkpBuilder = new BreakpointBuilder(className, methodName, variables, cfg);
		bkpBuilder.buildBreakpoints();
		dtBuilder = new BreakpointDataBuilder(bkpBuilder);
		
		ensureTcExecutor();
		tcExecutor.setup(appClassPath, testcases);
		tcExecutor.run();
		List<BreakpointData> result = tcExecutor.getResult();
		tcExecutor.setjResultFileDeleteOnExit(true);
		new DecisionLearner(new SelectiveSampling(tcExecutor)).learn(result);
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

	private void ensureTcExecutor() {
		if (tcExecutor == null) {
			tcExecutor = new TestcasesExecutorwithLoopTimes(DefaultValues.DEBUG_VALUE_RETRIEVE_LEVEL);
		}
		tcExecutor.setBuilder(dtBuilder);
		tcExecutor.setBkpBuilder(bkpBuilder);
	}	

}
