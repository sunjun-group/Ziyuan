package learntest.handler;

import java.io.File;
import java.util.List;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

import learntest.exception.LearnTestException;
import learntest.main.LearnTest;
import learntest.main.LearnTestConfig;
import learntest.main.LearnTestParams;
import learntest.main.RunTimeInfo;
import learntest.main.TestGenerator;
import sav.common.core.SavException;
import sav.common.core.utils.StopTimer;
import sav.settings.SAVTimer;
import sav.strategies.dto.AppJavaClassPath;
import sav.strategies.vm.JavaCompiler;
import sav.strategies.vm.VMConfiguration;

public class GenerateTestHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		Job job = new Job("Do evaluation") {
			@Override
			protected IStatus run(IProgressMonitor monitor) {
				generateTest(LearnTestConfig.isL2TApproach);
				return Status.OK_STATUS;
			}
		};
		job.schedule();
		
		return null;
	}
	
	public RunTimeInfo generateTest(boolean isL2T){
		try {
			SAVTimer.enableExecutionTimeout = true;
			SAVTimer.exeuctionTimeout = 50000000;
			
			AppJavaClassPath appClasspath = HandlerUtils.initAppJavaClassPath();
			List<File> newTests = new TestGenerator(appClasspath).genTest();
			new JavaCompiler(new VMConfiguration(appClasspath)).compile(appClasspath.getTestTarget(), newTests);
			HandlerUtils.refreshProject();
			StopTimer timer = new StopTimer("learntest");
			timer.start();
			timer.newPoint("learntest");
//			RunTimeInfo runtimeInfo = runLearntest(isL2T, appClasspath);
			RunTimeInfo runtimeInfo = runLearntest2(isL2T, appClasspath);
			
			if(runtimeInfo != null){
				String type = isL2T ? "l2t" : "randoop";
				System.out.println(type + " time: " + runtimeInfo.getTime() + "; coverage: " + runtimeInfo.getCoverage());
			}
			timer.newPoint("end");
			System.out.println(timer.getResults());
			HandlerUtils.refreshProject();
			
			return runtimeInfo;
			
		} catch (LearnTestException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SavException e) {
			e.printStackTrace();
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}

	private RunTimeInfo runLearntest(boolean isL2T, AppJavaClassPath appClasspath) throws LearnTestException {
		LearnTest engine = new LearnTest(appClasspath);
		RunTimeInfo runtimeInfo = engine.run(!isL2T);
		return runtimeInfo;
	}

	/**
	 * To test new version of learntest which uses another cfg and jacoco for code coverage. 
	 * @param isL2T
	 * @param appClasspath
	 * @return
	 * @throws LearnTestException
	 */
	private RunTimeInfo runLearntest2(boolean isL2T, AppJavaClassPath appClasspath) throws LearnTestException {
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
