package learntest.plugin.handler;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import learntest.core.LearnTestParams;
import learntest.core.RunTimeInfo;

public class GenerateTestHandler extends AbstractLearntestHandler {
	private static Logger log = LoggerFactory.getLogger(GenerateTestHandler.class);
	
	@Override
	protected IStatus execute(IProgressMonitor monitor) throws CoreException {
		evaluateLearntestForSingleMethod(initLearntestParamsFromPreference());
		log.debug("Finish!");
		return Status.OK_STATUS;
	}
	
	@Override
	protected String getJobName() {
		return "Run single learntest for a single method";
	}
	
	public RunTimeInfo generateTest(){
		try {
			LearnTestParams params = initLearntestParamsFromPreference();
			RunTimeInfo runtimeInfo = runLearntest(params);
			return runtimeInfo;
		} catch (Exception e) {
			e.printStackTrace();
		} 
		
		return null;
	}

}
