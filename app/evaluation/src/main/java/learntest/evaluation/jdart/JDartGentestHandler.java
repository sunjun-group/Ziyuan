package learntest.evaluation.jdart;

import java.util.List;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.jobs.Job;

import learntest.activelearning.core.handler.Tester;
import learntest.activelearning.core.model.UnitTestSuite;
import learntest.activelearning.core.settings.LearntestSettings;
import learntest.activelearning.plugin.ValidMethodsLoader;
import learntest.activelearning.plugin.handler.ActiveLearntestUtils;
import learntest.core.commons.data.classinfo.MethodInfo;
import learntest.plugin.LearnTestConfig;
import learntest.plugin.LearntestLogger;
import learntest.plugin.handler.gentest.GentestSettings;
import learntest.plugin.utils.IMethodUtils;
import learntest.plugin.utils.IResourceUtils;
import learntest.plugin.utils.IStatusUtils;
import learntest.plugin.utils.JdartConstants;
import learntest.plugin.utils.WorkbenchUtils;
import sav.common.core.utils.Randomness;
import sav.common.core.utils.SingleTimer;
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
		validMethods = Randomness.randomSubList1(validMethods, 10);
		JDartGentest jdartGentest = new JDartGentest(
				IResourceUtils.getResourceAbsolutePath(JdartConstants.BUNDLE_ID, "libs/jdart/jpf.properties"),
				IResourceUtils.getResourceAbsolutePath(JdartConstants.BUNDLE_ID, "libs/jpf.properties"));
		
		for (LearnTestConfig config : validMethods) {
			MethodInfo methodInfo = IMethodUtils.initTargetMethod(config);
			SingleTimer timer = SingleTimer.start("Run " + methodInfo.getMethodId());
			while (timer.getExecutionTime() < 60000) {
				LearntestSettings settings = ActiveLearntestUtils.getDefaultLearntestSettings();
				settings.setInitRandomTestNumber(1);
				try {
					Tester tester = new Tester(settings, false);
					UnitTestSuite testsuite = tester.createRandomTest(methodInfo, settings, appClasspath);
				} catch(Exception e) {
					// ignore
				}
			}
		}
	}

}
