package learntest.evaluation.random;

import java.util.List;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.jobs.Job;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import learntest.activelearning.core.settings.LearntestSettings;
import learntest.activelearning.plugin.ValidMethodsLoader;
import learntest.activelearning.plugin.handler.ActiveLearntestUtils;
import learntest.core.commons.data.classinfo.MethodInfo;
import learntest.plugin.LearnTestConfig;
import learntest.plugin.LearntestLogger;
import learntest.plugin.ProjectSetting;
import learntest.plugin.handler.gentest.GentestSettings;
import learntest.plugin.utils.IMethodUtils;
import learntest.plugin.utils.IStatusUtils;
import sav.strategies.dto.AppJavaClassPath;

public class RandomGenTestHandler extends AbstractHandler implements IHandler {
	private Logger log = LoggerFactory.getLogger(RandomGenTestHandler.class);
	
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
//		List<String> projects = WorkbenchUtils.getAllProjects();
//		for (Iterator<String> it = projects.iterator(); it.hasNext();) {
//			String projectam
//		}
//		for (String project : projects) {
//			runProject(project);
//		}
		runProject(LearnTestConfig.getInstance().getProjectName());
	}

	private void runProject(String project) throws Exception {
		AppJavaClassPath appClasspath = GentestSettings.getConfigAppClassPath(project);
		LearntestLogger.initLog4j(project);
		ValidMethodsLoader methodLoader = new ValidMethodsLoader();
		List<LearnTestConfig> validMethods = methodLoader.loadValidMethodInfos(project);
		String outputFolder = ProjectSetting.getLearntestOutputFolder(project) + "/random";
		RandomGentest rtest = new RandomGentest(outputFolder);
		for (LearnTestConfig config : validMethods) {
			MethodInfo methodInfo = IMethodUtils.initTargetMethod(config);
			LearntestSettings learntestSettings = ActiveLearntestUtils.getDefaultLearntestSettings();
			//try {
			rtest.generateTestcase(appClasspath, methodInfo, learntestSettings);
			//}
			//catch(Exception e){log.error(e.getStackTrace() != null ? e.getStackTrace().toString() : e.getMessage());}
		}
	}

}
