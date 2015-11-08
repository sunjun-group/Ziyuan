package icsetlv;

import icsetlv.common.dto.BreakpointData;
import icsetlv.variable.TestcasesExecutor;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import sav.common.core.utils.CollectionUtils;
import sav.common.core.utils.JunitUtils;
import sav.commons.AbstractTest;
import sav.commons.TestConfiguration;
import sav.strategies.dto.AppJavaClassPath;
import sav.strategies.dto.BreakPoint;
import sav.strategies.dto.BreakPoint.Variable;
import sav.strategies.dto.BreakPoint.Variable.VarScope;
import testdata.testcasesexecutor.test1.HTMLParser;
import testdata.testcasesexecutor.test1.HTMLParserTest;

public class TestcaseTest extends AbstractTest{

	private TestcasesExecutor tcExecutor;
	private AppJavaClassPath appClasspath;
	
	@Before
	public void setup() {
		appClasspath = initAppClasspath();
		appClasspath.addClasspath(TestConfiguration.getTestTarget(ICSETLV));
		tcExecutor = new TestcasesExecutor(6);
	}
	
	@Test
	public void testExecute() throws Exception {
		// breakpoints
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
		List<String> tests = JunitUtils.extractTestMethods(junitClassNames);
		tcExecutor.setup(appClasspath, tests);
		tcExecutor.run(breakpoints);
		List<BreakpointData> result = tcExecutor.getResult();
		System.out.println(result);
	}
}
