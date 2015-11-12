package microbat.handler;

import static sav.commons.TestConfiguration.SAV_COMMONS_TEST_TARGET;
import icsetlv.common.dto.BreakpointData;
import icsetlv.trial.model.Trace;
import icsetlv.variable.TestcasesExecutor;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

import sav.common.core.SavException;
import sav.common.core.utils.CollectionUtils;
import sav.common.core.utils.JunitUtils;
import sav.commons.TestConfiguration;
import sav.strategies.dto.AppJavaClassPath;
import sav.strategies.dto.BreakPoint;
import sav.strategies.dto.BreakPoint.Variable;
import sav.strategies.dto.BreakPoint.Variable.VarScope;
import testdata.testcasesexecutor.test1.HTMLParser;
import testdata.testcasesexecutor.test1.HTMLParserTest;

;

public class StartDebugHandler extends AbstractHandler {

	private TestcasesExecutor tcExecutor;
	private AppJavaClassPath appClasspath;
	
	protected AppJavaClassPath initAppClasspath() {
		AppJavaClassPath appClasspath = new AppJavaClassPath();
		appClasspath.setJavaHome(TestConfiguration.getJavaHome());
		appClasspath.addClasspath(SAV_COMMONS_TEST_TARGET);
		return appClasspath;
	}
	
	public void setup() {
		appClasspath = initAppClasspath();
		appClasspath.addClasspath("");
		//appClasspath.addClasspath(TestConfiguration.getTestTarget(ICSETLV));
		tcExecutor = new TestcasesExecutor(6);
	}
	
	public Object execute(ExecutionEvent event) throws ExecutionException {
		
		initAppClasspath();
		setup();
		
		List<BreakPoint> breakpoints = new ArrayList<BreakPoint>();
		String clazz = HTMLParser.class.getName();
		BreakPoint bkp1 = new BreakPoint(clazz, null, 35);
		bkp1.addVars(new Variable("c"));
		bkp1.addVars(new Variable("tag", "tag", VarScope.THIS));
		//bkp1.addVars(new Variable("strList", "strList", VarScope.THIS));
		//bkp1.addVars(new Variable("map", "map", VarScope.THIS));
		//bkp1.addVars(new Variable("array", "array", VarScope.THIS));
		bkp1.addVars(new Variable("org", "org", VarScope.THIS));
		bkp1.addVars(new Variable("output"));
		breakpoints.add(bkp1);
		List<String> junitClassNames = CollectionUtils.listOf(HTMLParserTest.class.getName());
		List<String> tests;
		try {
			tests = JunitUtils.extractTestMethods(junitClassNames);
			tcExecutor.setup(appClasspath, tests);
			tcExecutor.run(breakpoints);
			List<BreakpointData> result = tcExecutor.getResult();
			System.out.println(result);
			
			Trace trace = tcExecutor.getTrace();
			System.currentTimeMillis();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SavException e) {
			e.printStackTrace();
		}
		
		return null;
	}

}
