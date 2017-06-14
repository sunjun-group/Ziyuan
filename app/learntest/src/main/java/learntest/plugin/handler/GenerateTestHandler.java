package learntest.plugin.handler;

import java.io.File;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import learntest.exception.LearnTestException;
import learntest.main.LearnTest;
import learntest.main.LearnTestConfig;
import learntest.main.LearnTestParams;
import learntest.main.RunTimeInfo;
import learntest.main.TestGenerator;
import sav.common.core.utils.StopTimer;
import sav.settings.SAVTimer;
import sav.strategies.dto.AppJavaClassPath;
import sav.strategies.vm.JavaCompiler;
import sav.strategies.vm.VMConfiguration;

public class GenerateTestHandler extends AbstractLearntestHandler {

	@Override
	protected IStatus execute(IProgressMonitor monitor) {
		generateTest(LearnTestConfig.isL2TApproach);
		return Status.OK_STATUS;
	}
	
	@Override
	protected String getJobName() {
		return "Do evaluation for single method";
	}
	
	public RunTimeInfo generateTest(boolean isL2T){
		try {
			SAVTimer.enableExecutionTimeout = true;
			SAVTimer.exeuctionTimeout = 50000000;
			
			List<File> newTests = new TestGenerator(getAppClasspath()).genTest().getJunitfiles();
			new JavaCompiler(new VMConfiguration(getAppClasspath())).compile(getAppClasspath().getTestTarget(), newTests);
			refreshProject();
			StopTimer timer = new StopTimer("learntest");
			timer.start();
			timer.newPoint("learntest");
//			RunTimeInfo runtimeInfo = runLearntest(isL2T, getAppClasspath());
			RunTimeInfo runtimeInfo = runLearntest2(isL2T, getAppClasspath());
			
			if(runtimeInfo != null) {
				String type = isL2T ? "l2t" : "randoop";
				System.out.println(type + " time: " + runtimeInfo.getTime() + "; coverage: " + runtimeInfo.getCoverage());
			}
			timer.newPoint("end");
			System.out.println(timer.getResults());
			refreshProject();
			
			return runtimeInfo;
			
		} catch (Exception e) {
			e.printStackTrace();
		} 
		
		return null;
	}

	private RunTimeInfo runLearntest(boolean isL2T, AppJavaClassPath appClasspath) throws Exception {
		LearnTest engine = new LearnTest(appClasspath);
		RunTimeInfo runtimeInfo = engine.run(!isL2T);
		return runtimeInfo;
	}

	/**
	 * To test new version of learntest which uses another cfg and jacoco for code coverage. 
	 */
	private RunTimeInfo runLearntest2(boolean isL2T, AppJavaClassPath appClasspath) throws Exception {
		learntest.core.LearnTest learntest = new learntest.core.LearnTest(appClasspath);
		try {
			LearnTestParams params = LearnTestParams.initFromLearnTestConfig();
			RunTimeInfo runtimeInfo = learntest.run(params);
			return runtimeInfo;
		} catch (Exception e) {
			throw new LearnTestException(e);
		}
	}
}
