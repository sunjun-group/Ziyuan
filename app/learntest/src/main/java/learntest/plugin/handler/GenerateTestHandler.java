package learntest.plugin.handler;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import learntest.exception.LearnTestException;
import learntest.main.LearnTestParams;
import learntest.main.RunTimeInfo;
import sav.settings.SAVTimer;

public class GenerateTestHandler extends AbstractLearntestHandler {

	@Override
	protected IStatus execute(IProgressMonitor monitor) {
		generateTest();
		return Status.OK_STATUS;
	}
	
	@Override
	protected String getJobName() {
		return "Do evaluation for single method";
	}
	
	public RunTimeInfo generateTest(){
		try {
			LearnTestParams params = initLearntestParams();
			RunTimeInfo runtimeInfo = runLearntest(params);
			return runtimeInfo;
			
		} catch (Exception e) {
			e.printStackTrace();
		} 
		
		return null;
	}

	/**
	 * To test new version of learntest which uses another cfg and jacoco for code coverage. 
	 */
	 public RunTimeInfo runLearntest(LearnTestParams params) throws Exception {
		try {
			SAVTimer.enableExecutionTimeout = true;
			SAVTimer.exeuctionTimeout = 50000000;
			learntest.core.LearnTest learntest = new learntest.core.LearnTest(getAppClasspath());
			RunTimeInfo runtimeInfo = learntest.run(params);
			refreshProject();

			if(runtimeInfo != null) {
				String type = params.isLearnByPrecond() ? "l2t" : "randoop";
				System.out.println(type + " time: " + runtimeInfo.getTime() + "; coverage: " + runtimeInfo.getCoverage());
			}
			return runtimeInfo;
		} catch (Exception e) {
			throw new LearnTestException(e);
		}
	}

}
