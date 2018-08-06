package learntest.evaluation.jdart;

import java.util.List;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.jobs.Job;

import learntest.activelearning.core.settings.LearntestSettings;
import learntest.activelearning.plugin.ValidMethodsLoader;
import learntest.activelearning.plugin.handler.ActiveLearntestUtils;
import learntest.core.commons.data.classinfo.MethodInfo;
import learntest.plugin.LearnTestConfig;
import learntest.plugin.LearntestLogger;
import learntest.plugin.ProjectSetting;
import learntest.plugin.handler.gentest.GentestSettings;
import learntest.plugin.utils.IMethodUtils;
import learntest.plugin.utils.IResourceUtils;
import learntest.plugin.utils.IStatusUtils;
import learntest.plugin.utils.JdartConstants;
import learntest.plugin.utils.WorkbenchUtils;
import sav.strategies.dto.AppJavaClassPath;

public class JDartGentestHandler extends AbstractHandler implements IHandler {

	@Override
	public Object execute(ExecutionEvent event) throws org.eclipse.core.commands.ExecutionException {
		
		Job job = new Job("GenerateTestcases-JDart") {

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
		List<String> projects = WorkbenchUtils.getAllProjects();
		for (String project : projects) {
			if (project.contains("math")) {
				runProject(project, monitor);
			}
		}
	}

	private void runProject(String project, IProgressMonitor monitor) throws Exception {
		AppJavaClassPath appClasspath = GentestSettings.getConfigAppClassPath(project);
		LearntestLogger.initLog4j(project);
		ValidMethodsLoader methodLoader = new ValidMethodsLoader();
		List<LearnTestConfig> validMethods = methodLoader.loadValidMethodInfos(project);
		String jdartFolder = ProjectSetting.getLearntestOutputFolder(project) + "/jdart";
		JDartGentest jdartGentest = new JDartGentest(
				IResourceUtils.getResourceAbsolutePath(JdartConstants.BUNDLE_ID, "libs/jdart/jpf.properties"),
				IResourceUtils.getResourceAbsolutePath(JdartConstants.BUNDLE_ID, "libs/jpf.properties"),
				jdartFolder);
		for (LearnTestConfig config : validMethods) {
			if (monitor.isCanceled()) {
				return;
			}
			MethodInfo methodInfo = IMethodUtils.initTargetMethod(config);
			LearntestSettings settings = ActiveLearntestUtils.getDefaultLearntestSettings();
			settings.setMethodExecTimeout(500l);
			jdartGentest.generateTestcase(appClasspath, methodInfo, settings);
		}
	}

}
