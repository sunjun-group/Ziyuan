package learntest.plugin.handler;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import learntest.core.LearnTestParams;
import learntest.core.RunTimeInfo;
import learntest.core.commons.data.LearnTestApproach;
import learntest.plugin.utils.IStatusUtils;
import sav.common.core.utils.TextFormatUtils;

public class GenerateTestHandler extends AbstractLearntestHandler {
	private static Logger log = LoggerFactory.getLogger(GenerateTestHandler.class);
	
	@Override
	protected IStatus execute(IProgressMonitor monitor) throws CoreException {
		generateTest();
		refreshProject();
		log.debug("Finish!");
		return Status.OK_STATUS;
	}
	
	@Override
	protected String getJobName() {
		return "Run single learntest for a single method";
	}
	
//	public RunTimeInfo generateTest() throws CoreException{
//		try {
//			LearnTestParams params = initLearntestParamsFromPreference();
//			RunTimeInfo runtimeInfo = runLearntest(params);
//			return runtimeInfo;
//		} catch (Exception e) {
//			log.debug("Error when generating test: {}", e.getMessage());
//			throw new CoreException(IStatusUtils.exception(e, e.getMessage()));
//		} 
//	}
	
	public RunTimeInfo generateTest() throws CoreException{
		try {
			LearnTestParams l2tParam = initLearntestParamsFromPreference();
			l2tParam.setApproach(LearnTestApproach.L2T);
			RunTimeInfo l2tRuntimeInfo = runLearntest(l2tParam);

			LearnTestParams randoopParam = l2tParam.createNew();
			randoopParam.setApproach(LearnTestApproach.RANDOOP);
			randoopParam.setInitialTests(l2tParam.getInitialTests());
			randoopParam.setMaxTcs(l2tRuntimeInfo.getTestCnt());
			
			log.info("run randoop..");
			RunTimeInfo ranInfo = runLearntest(randoopParam);		
			
			print(l2tRuntimeInfo, l2tParam);
			print(ranInfo, randoopParam);
			
			return l2tRuntimeInfo;
		} catch (Exception e) {
			log.debug("Error when generating test: {}", e.getMessage());
			throw new CoreException(IStatusUtils.exception(e, e.getMessage()));
		} 
	}
	
	public void print(RunTimeInfo runtimeInfo, LearnTestParams params){
		if (runtimeInfo != null) {
			log.info(params.getApproach().getName());
			if (runtimeInfo.getLineCoverageResult() != null) {
				log.info("Line coverage result:");
				log.info(runtimeInfo.getLineCoverageResult().getDisplayText());
			}
			log.info("{} RESULT:", StringUtils.upperCase(params.getApproach().getName()));
			log.info("TIME: {}; COVERAGE: {}; CNT: {}", TextFormatUtils.printTimeString(runtimeInfo.getTime()),
					runtimeInfo.getCoverage(), runtimeInfo.getTestCnt());
			log.info("TOTAL COVERAGE INFO: \n{}", runtimeInfo.getCoverageInfo());
		}
	}
}
