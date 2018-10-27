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

import learntest.activelearning.core.data.MethodInfo;
import learntest.activelearning.core.settings.LearntestSettings;
import learntest.activelearning.plugin.ValidMethodsLoader;
import learntest.activelearning.plugin.handler.ActiveLearntestUtils;
import learntest.activelearning.plugin.utils.ActiveLearnTestConfig;
import learntest.activelearning.plugin.utils.IMethodUtils;
import learntest.plugin.LearnTestConfig;
import learntest.plugin.LearntestLogger;
import learntest.plugin.ProjectSetting;
import learntest.plugin.handler.gentest.GentestSettings;
import learntest.plugin.utils.IStatusUtils;
import sav.common.core.utils.FileUtils;
import sav.strategies.dto.AppJavaClassPath;

public class RandomGenTestHandler extends AbstractHandler implements IHandler {
	private Logger log = LoggerFactory.getLogger(RandomGenTestHandler.class);
	
	@Override
	public Object execute(ExecutionEvent event) throws org.eclipse.core.commands.ExecutionException {
		
		Job job = new Job("GenerateTestcases") {

			@Override
			protected IStatus run(IProgressMonitor monitor) {
				try {
					execute(monitor);
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

	protected void execute(IProgressMonitor monitor) throws Exception {
//		List<String> projects = WorkbenchUtils.getAllProjects();
//		for (Iterator<String> it = projects.iterator(); it.hasNext();) {
//			String projectam
//		}
//		for (String project : projects) {
//			runProject(project);
//		}
		runProject(LearnTestConfig.getInstance().getProjectName(), new learntest.activelearning.core.IProgressMonitor() {
			
			@Override
			public boolean isCanceled() {
				return monitor.isCanceled();
			}
		});
	}

	private void runProject(String project, learntest.activelearning.core.IProgressMonitor progressMonitor) throws Exception {
		AppJavaClassPath appClasspath = GentestSettings.getConfigAppClassPath(project);
		LearntestLogger.initLog4j(project);
		ValidMethodsLoader methodLoader = new ValidMethodsLoader();
		List<ActiveLearnTestConfig> validMethods = methodLoader.loadValidMethodInfos(project);
		String outputFolder = ProjectSetting.getLearntestOutputFolder(project) + "/random";
		FileUtils.mkDirs(outputFolder);
		RandomGenTest randomGentest = new RandomGenTest(outputFolder);
		for (ActiveLearnTestConfig config : validMethods) {
			if (progressMonitor.isCanceled()) {
				return;
			}
			MethodInfo methodInfo = IMethodUtils.initTargetMethod(config);
			LearntestSettings learntestSettings = ActiveLearntestUtils.getDefaultLearntestSettings();
			//try {
			randomGentest.generateTestcase(appClasspath, methodInfo, learntestSettings, progressMonitor);
			//}
			//catch(Exception e){log.error(e.getStackTrace() != null ? e.getStackTrace().toString() : e.getMessage());}
		}
	}

}
