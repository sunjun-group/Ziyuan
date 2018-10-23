package learntest.activelearning.plugin.handler;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;

import learntest.activelearning.core.data.ClassInfo;
import learntest.activelearning.core.data.MethodInfo;
import learntest.activelearning.core.distribution.RandomTestDistributionRunner;
import learntest.activelearning.core.settings.LearnTestResources;
import learntest.activelearning.core.settings.LearntestSettings;
import learntest.activelearning.plugin.ActiveLearntestPlugin;
import learntest.activelearning.plugin.settings.GentestSettings;
import learntest.activelearning.plugin.settings.LearntestLogger;
import learntest.activelearning.plugin.utils.IStatusUtils;
import learntest.activelearning.plugin.utils.ActiveLearnTestConfig;
import learntest.activelearning.plugin.utils.PluginUtils;
import sav.common.core.SavException;
import sav.common.core.utils.CollectionUtils;
import sav.eclipse.plugin.IProjectUtils;
import sav.eclipse.plugin.IResourceUtils;
import sav.settings.SAVTimer;
import sav.strategies.dto.AppJavaClassPath;

public class OneTestcaseDistributionHandler extends AbstractHandler implements IHandler {
	
	public OneTestcaseDistributionHandler() {
	}
	
	@Override
	public Object execute(ExecutionEvent event) throws org.eclipse.core.commands.ExecutionException {
		Job job = new Job("Run One Random Testcases For Distribution") {

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
		ActiveLearnTestConfig config = ActiveLearnTestConfig.getInstance();
		AppJavaClassPath appClasspath = GentestSettings.getConfigAppClassPath(config);
		MethodInfo method = initTargetMethod(config);
		LearntestLogger.initLog4j(config.getProjectName());
		LearnTestResources resources = new LearnTestResources();
		resources.setMicrobatInstrumentationJarPath(IResourceUtils.getResourceAbsolutePath(ActiveLearntestPlugin.PLUGIN_ID, 
				"microbat_instrumentator.jar"));
		resources.setSavJunitRunnerJarPath(IResourceUtils.getResourceAbsolutePath(ActiveLearntestPlugin.PLUGIN_ID, 
				"sav.testrunner.jar"));
		LearntestSettings learntestSettings = new LearntestSettings(resources);
		IJavaProject project = IProjectUtils.getJavaProject(config.getProjectName());
//		SAVTimer.enableExecutionTimeout = true;
		SAVTimer.exeuctionTimeout = 50000000;

		run(appClasspath, learntestSettings, monitor, method);
		
	}
	
	public void run(AppJavaClassPath appClasspath, LearntestSettings learntestSettings, IProgressMonitor monitor, MethodInfo method) {
		/*----------*/

		RandomTestDistributionRunner distributionRunner = new RandomTestDistributionRunner();

		try {
			learntestSettings.setInitRandomTestNumber(1);
			long startTime = System.currentTimeMillis();
			long endTime;
			while(true) {
			distributionRunner.run(appClasspath, method, learntestSettings);
			endTime = System.currentTimeMillis();
			if((endTime - startTime)>=180000)break;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		/*----------*/
	}

	private MethodInfo initTargetMethod(ActiveLearnTestConfig config) throws SavException, JavaModelException {
		ClassInfo targetClass = new ClassInfo(config.getTargetClassName());
		MethodInfo method = new MethodInfo(targetClass);
		method.setMethodName(config.getTargetMethodName());
		method.setLineNum(config.getMethodLineNumber());
		MethodDeclaration methodDeclaration = PluginUtils.findSpecificMethod(method.getClassName(),
				method.getMethodName(), method.getLineNum());
		method.setMethodSignature(PluginUtils.getMethodSignature(methodDeclaration));
		List<String> paramNames = new ArrayList<String>(CollectionUtils.getSize(methodDeclaration.parameters()));
		List<String> paramTypes = new ArrayList<String>(paramNames.size());
		for (Object obj : methodDeclaration.parameters()) {
			if (obj instanceof SingleVariableDeclaration) {
				SingleVariableDeclaration svd = (SingleVariableDeclaration) obj;
				paramNames.add(svd.getName().getIdentifier());
				paramTypes.add(svd.getType().toString());
			}
		}
		method.setParams(paramNames);
		method.setParamTypes(paramTypes);
		return method;
	}

}
