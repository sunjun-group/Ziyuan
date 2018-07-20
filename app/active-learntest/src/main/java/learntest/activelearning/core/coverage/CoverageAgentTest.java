package learntest.activelearning.core.coverage;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import microbat.instrumentation.cfgcoverage.CoverageAgentParams;
import microbat.instrumentation.cfgcoverage.CoverageOutput;
import microbat.model.ClassLocation;
import sav.common.core.SavException;
import sav.strategies.dto.AppJavaClassPath;

public class CoverageAgentTest {

	@Test
	public void runProgram() throws SavException {
		String agentJarPath = "E:/lyly/eclipse-java-mars-clean/eclipse/../../Projects/Ziyuan/learntest-nn/Ziyuan/app/active-learntest/bin/microbat_instrumentator.jar";
		String savJunitRunnerJarPath = "E:/lyly/eclipse-java-mars-clean/eclipse/../../Projects/Ziyuan/learntest-nn/Ziyuan/app/active-learntest/bin/sav.testrunner.jar";
		AppJavaClassPath appClasspath = new AppJavaClassPath();
		appClasspath.setJavaHome("E:/lyly/Tools/jdk/jdk1.8.171_64");
		appClasspath.addClasspath("E:/lyly/Projects/TestData/learntest-benchmark/master/benchmark/bin");
		appClasspath.addClasspath("E:/lyly/eclipse-java-mars-clean/eclipse/plugins/org.hamcrest.core_1.3.0.v201303031735.jar");
		appClasspath.addClasspath("E:/lyly/eclipse-java-mars-clean/eclipse/plugins/org.junit_4.12.0.v201504281640/junit.jar");
		int cdgLayer = 2;
		CoverageAgentRunner coverageAgent = new CoverageAgentRunner(agentJarPath, savJunitRunnerJarPath, appClasspath);
		/* for building program params */
		List<String> junitMethods = Arrays.asList("testdata.learntest.program.max.Program2.test1");
		/* build agent params */
		CoverageAgentParams agentParams = new CoverageAgentParams();
		agentParams.setCdgLayer(cdgLayer);
		agentParams.setClassPaths(appClasspath.getClasspaths());
		agentParams.setTargetMethodLoc(new ClassLocation("com.Program", "Max(III)I", -1));
		agentParams.setInclusiveMethodIds(Arrays.asList("com.Program.Max(III)I"));
		agentParams.setWorkingDirectory("E:/lyly/Projects/TestData/learntest-benchmark/master/benchmark");
		CoverageOutput coverageOutput = coverageAgent.run(agentParams, -1, junitMethods);
		System.out.println(coverageOutput);
	}
}
