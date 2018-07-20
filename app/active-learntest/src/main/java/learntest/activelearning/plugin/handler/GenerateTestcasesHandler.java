package learntest.activelearning.plugin.handler;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;

import learntest.activelearning.core.NeuralActiveLearnTest;
import learntest.activelearning.core.settings.LearnTestResources;
import learntest.activelearning.core.settings.LearntestSettings;
import learntest.activelearning.plugin.ActiveLearntestPlugin;
import learntest.core.commons.data.classinfo.ClassInfo;
import learntest.core.commons.data.classinfo.MethodInfo;
import learntest.plugin.LearnTestConfig;
import learntest.plugin.LearntestLogger;
import learntest.plugin.handler.gentest.GentestSettings;
import learntest.plugin.utils.IResourceUtils;
import learntest.plugin.utils.IStatusUtils;
import learntest.plugin.utils.LearnTestUtil;
import sav.common.core.SavException;
import sav.common.core.utils.CollectionUtils;
import sav.strategies.dto.AppJavaClassPath;

public class GenerateTestcasesHandler extends AbstractHandler implements IHandler {

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
		LearnTestConfig config = LearnTestConfig.getInstance();
		AppJavaClassPath appClasspath = GentestSettings.getConfigAppClassPath(config);
		LearntestLogger.initLog4j(config.getProjectName());
		MethodInfo methodInfo = initTargetMethod(config);
		NeuralActiveLearnTest learntest = new NeuralActiveLearnTest();
		LearnTestResources resources = new LearnTestResources();
		resources.setMicrobatInstrumentationJarPath(IResourceUtils.getResourceAbsolutePath(ActiveLearntestPlugin.PLUGIN_ID, 
				"microbat_instrumentator.jar"));
		resources.setSavJunitRunnerJarPath(IResourceUtils.getResourceAbsolutePath(ActiveLearntestPlugin.PLUGIN_ID, 
				"sav.testrunner.jar"));
		LearntestSettings learntestSettings = new LearntestSettings(resources);
		learntest.generateTestcase(appClasspath, methodInfo, learntestSettings);
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
