package learntest.activelearning.plugin.handler;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.jobs.Job;

import learntest.activelearning.core.SearchBasedLearnTest;
import learntest.activelearning.core.settings.LearntestSettings;
import learntest.core.commons.data.classinfo.MethodInfo;
import learntest.plugin.LearnTestConfig;
import learntest.plugin.LearntestLogger;
import learntest.plugin.handler.gentest.GentestSettings;
import learntest.plugin.utils.IMethodUtils;
import learntest.plugin.utils.IStatusUtils;
import sav.strategies.dto.AppJavaClassPath;

public class HillClimbingSearchTestHandler extends AbstractHandler implements IHandler {

	@Override
	public Object execute(ExecutionEvent event) throws org.eclipse.core.commands.ExecutionException {
		Job job = new Job("Hill-Climbing Search Test") {

			@Override
			protected IStatus run(IProgressMonitor monitor) {
				try {
					execute();
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					monitor.done();
				}
				return IStatusUtils.afterRunning(monitor);
			}

		};
		job.schedule();

		return null;
	}

	protected void execute() throws Exception {
		LearnTestConfig config = LearnTestConfig.getInstance();
		AppJavaClassPath appClasspath = GentestSettings.getConfigAppClassPath(config);
		LearntestLogger.initLog4j(config.getProjectName());
		MethodInfo methodInfo = IMethodUtils.initTargetMethod(config);
		SearchBasedLearnTest learntest = new SearchBasedLearnTest();
		LearntestSettings learntestSettings = ActiveLearntestUtils.getDefaultLearntestSettings();
		learntest.generateTestcase(appClasspath, methodInfo, learntestSettings);
	}
	
}
