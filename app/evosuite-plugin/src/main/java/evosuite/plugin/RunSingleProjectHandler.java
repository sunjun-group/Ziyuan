package evosuite.plugin;

import java.util.Arrays;
import java.util.List;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jdt.core.IJavaProject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import evosuite.core.Configuration;
import evosuite.core.EvosuitEvaluation;
import learntest.activelearning.core.settings.LearntestSettings;
import learntest.activelearning.plugin.handler.ActiveLearntestUtils;
import sav.common.core.SavRtException;
import sav.common.core.SystemVariables;
import sav.common.core.utils.FileUtils;
import sav.eclipse.plugin.IProjectUtils;
import sav.eclipse.plugin.IResourceUtils;
import sav.strategies.dto.AppJavaClassPath;
import sav.strategies.vm.VMConfiguration;

public class RunSingleProjectHandler extends AbstractHandler {
	private static Logger log = LoggerFactory.getLogger(RunSingleProjectHandler.class);

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		Job job = new Job("Run Single Project") {

			@Override
			protected IStatus run(IProgressMonitor monitor) {
				try {
					runEvosuite(monitor);
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					monitor.done();
				}
				log.info("Complete!");
				return Status.OK_STATUS;
			}

		};
		job.schedule();

		return null;
	}

	protected void runEvosuite(final IProgressMonitor monitor) throws Exception {
		EvosuitePreferenceData data = EvosuitePreference.getEvosuitePreferenceData();
		String evosuiteStandaloneRtJar = IResourceUtils.getResourceAbsolutePath(EvosuitePlugin.PLUGIN_ID, "libs/evosuite-standalone-runtime-1.0.5.jar");
		AppJavaClassPath appClasspath = initAppClasspath(data.getProjectName(), Arrays.asList(evosuiteStandaloneRtJar));
		String excelReport = FileUtils.getFilePath(data.getEvosuiteWorkingDir(), "evosuite.xlsx");
		String graphCoverageReport = FileUtils.getFilePath(data.getEvosuiteWorkingDir(), "evosuite_graphCoverage.csv");
		Configuration config = new Configuration(excelReport, graphCoverageReport);
		config.setEvosuitSrcFolder(FileUtils.getFilePath(data.getEvosuiteWorkingDir(), "generated_tests"));
		config.setCoverageInfoLogFile(FileUtils.getFilePath(data.getEvosuiteWorkingDir(), "evosuite.log"));
		config.setEvoBaseDir(data.getEvosuiteWorkingDir());
		config.setMethodListFile(data.getTargetMethodListFile());
		if (!hasLib(appClasspath, "evosuite-standalone-runtime")) {
			appClasspath.addClasspath(evosuiteStandaloneRtJar);
		}
		if (!hasLib(appClasspath, "junit")) {
			appClasspath.addClasspath(IResourceUtils.getResourceAbsolutePath(EvosuitePlugin.PLUGIN_ID, "libs/junit-4.12.jar"));
		}
		VMConfiguration evosuiteConfig = new VMConfiguration();
		evosuiteConfig.addClasspath(IResourceUtils.getResourceAbsolutePath(EvosuitePlugin.PLUGIN_ID, "libs/evosuite-generated-1.0.5.jar"));
		evosuiteConfig.addClasspath(IResourceUtils.getResourceAbsolutePath(EvosuitePlugin.PLUGIN_ID, "libs/evosuite-master-1.0.5.jar"));
		evosuiteConfig.addClasspath(IResourceUtils.getResourceAbsolutePath(EvosuitePlugin.PLUGIN_ID, "libs/evosuite-shaded-1.0.5.jar"));
		evosuiteConfig.addClasspath(evosuiteStandaloneRtJar);
		evosuiteConfig.addClasspath(IResourceUtils.getResourceAbsolutePath(EvosuitePlugin.PLUGIN_ID, "libs/evosuite.runner.jar"));
		evosuiteConfig.setJavaHome(appClasspath.getJavaHome());
		
		LearntestSettings learntestSettings = ActiveLearntestUtils.getDefaultLearntestSettings();
		learntestSettings.setRunCoverageAsMethodInvoke(true);
//		learntestSettings.setMethodExecTimeout(1000l);
		learntestSettings.setMethodExecTimeout(-1);
		learntestSettings.setCfgExtensionLayer(1);
		EvosuitEvaluation evosuite = new EvosuitEvaluation(evosuiteConfig, learntestSettings);
		evosuite.run(appClasspath, config, new evosuite.core.commons.IProgressMonitor() {
			
			@Override
			public boolean isCanceled() {
				return monitor.isCanceled();
			}
		});
	}
	
	private boolean hasLib(AppJavaClassPath appClasspath, String jarPrefix) {
		for (String path : appClasspath.getClasspaths()) {
			if (path.startsWith(jarPrefix) && path.endsWith(".jar")) {
				return true;
			}
		}
		return false;
	}

	private AppJavaClassPath initAppClasspath(String projectName, List<String> extJars) {
		try {
			IJavaProject javaProject = IProjectUtils.getJavaProject(projectName);
			AppJavaClassPath appClasspath = new AppJavaClassPath();
			appClasspath.setJavaHome(IProjectUtils.getJavaHome(javaProject));
			String outputPath = IProjectUtils.getTargetFolder(javaProject);
			appClasspath.setTarget(outputPath);
			appClasspath.setTestTarget(outputPath);
			appClasspath.setSrc(IProjectUtils.getSourceFolder(javaProject));
			appClasspath.setTestSrc(IProjectUtils.getTestSourceFolder(javaProject));
			appClasspath.addClasspaths(IProjectUtils.getPrjectClasspath(javaProject));
			appClasspath.getPreferences().set(SystemVariables.PROJECT_CLASSLOADER, IProjectUtils.getPrjClassLoader(javaProject, extJars));
			String projectPath = IResourceUtils.relativeToAbsolute(javaProject.getPath()).toOSString();
			appClasspath.setWorkingDirectory(projectPath);
			return appClasspath;
		} catch (Exception ex) {
			throw new SavRtException(ex);
		}
	}
	
}
