package learntest.activelearning.plugin.handler;

import java.util.ArrayList;
import java.util.Arrays;
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
import learntest.activelearning.core.distribution.RandomTestDistributionRunner;
import learntest.activelearning.core.settings.LearnTestResources;
import learntest.activelearning.core.settings.LearntestSettings;
import learntest.activelearning.plugin.ActiveLearntestPlugin;
import learntest.core.commons.data.classinfo.ClassInfo;
import learntest.core.commons.data.classinfo.MethodInfo;
import learntest.plugin.LearnTestConfig;
import learntest.plugin.LearntestLogger;
import learntest.plugin.handler.filter.classfilter.TestableClassFilter;
import learntest.plugin.handler.filter.methodfilter.IMethodFilter;
import learntest.plugin.handler.filter.methodfilter.NestedBlockChecker;
import learntest.plugin.handler.filter.methodfilter.TestableMethodFilter;
import learntest.plugin.handler.gentest.GentestSettings;
import learntest.plugin.utils.IProjectUtils;
import learntest.plugin.utils.IResourceUtils;
import learntest.plugin.utils.IStatusUtils;
import learntest.plugin.utils.LearnTestUtil;
import sav.common.core.SavException;
import sav.common.core.utils.CollectionUtils;
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
		LearnTestConfig config = LearnTestConfig.getInstance();
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

		run(project, appClasspath, learntestSettings, monitor, method);
		
	}
	
	private void run(IJavaProject project, AppJavaClassPath appClasspath, LearntestSettings learntestSettings, IProgressMonitor monitor, MethodInfo method) {
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

	private MethodInfo initTargetMethod(LearnTestConfig config) throws SavException, JavaModelException {
		ClassInfo targetClass = new ClassInfo(config.getTargetClassName());
		MethodInfo method = new MethodInfo(targetClass);
		method.setMethodName(config.getTargetMethodName());
		method.setLineNum(config.getMethodLineNumber());
		MethodDeclaration methodDeclaration = LearnTestUtil.findSpecificMethod(method.getClassName(),
				method.getMethodName(), method.getLineNum());
		method.setMethodSignature(LearnTestUtil.getMethodSignature(methodDeclaration));
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
