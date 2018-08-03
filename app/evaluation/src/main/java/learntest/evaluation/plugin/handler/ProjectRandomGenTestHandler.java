package learntest.evaluation.plugin.handler;

import java.util.List;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jdt.core.JavaModelException;

import learntest.evaluation.core.RandomGentest;
import learntest.activelearning.core.NeuralActiveLearnTest;
import learntest.activelearning.core.settings.LearntestSettings;
import learntest.activelearning.plugin.ValidMethodsLoader;
import learntest.activelearning.plugin.handler.ActiveLearntestUtils;
import learntest.core.commons.data.classinfo.MethodInfo;
import learntest.plugin.LearnTestConfig;
import learntest.plugin.LearntestLogger;
import learntest.plugin.commons.PluginException;
import learntest.plugin.handler.gentest.GentestSettings;
import learntest.plugin.utils.IMethodUtils;
import learntest.plugin.utils.IStatusUtils;
import learntest.plugin.utils.WorkbenchUtils;
import sav.common.core.SavException;
import sav.strategies.dto.AppJavaClassPath;

public class ProjectRandomGenTestHandler extends AbstractHandler implements IHandler {
	
	@Override
	public Object execute(ExecutionEvent event) throws org.eclipse.core.commands.ExecutionException {
		
		Job job = new Job("GenerateTestcases") {

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
		List<String> projects = WorkbenchUtils.getAllProjects();
		for (String project : projects) {
			runProject(project);
		}
	}

	private void runProject(String project) throws Exception {
		AppJavaClassPath appClasspath = GentestSettings.getConfigAppClassPath(project);
		LearntestLogger.initLog4j(project);
		ValidMethodsLoader methodLoader = new ValidMethodsLoader();
		List<LearnTestConfig> validMethods = methodLoader.loadValidMethodInfos(project);
		for (LearnTestConfig config : validMethods) {
			MethodInfo methodInfo = IMethodUtils.initTargetMethod(config);
			LearntestSettings learntestSettings = ActiveLearntestUtils.getDefaultLearntestSettings();
			// TODO Guanji
			RandomGentest rtest = new RandomGentest();
			rtest.generateTestcase(appClasspath, methodInfo, learntestSettings);
			
		}
	}

}
