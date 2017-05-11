package learntest.handler;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

import learntest.exception.LearnTestException;
import learntest.main.LearnTest;
import learntest.main.LearnTestConfig;
import learntest.main.LearnTestParams;
import learntest.main.RunTimeInfo;
import learntest.main.TestGenerator;
import learntest.util.LearnTestUtil;
import sav.common.core.SavException;
import sav.common.core.SystemVariables;
import sav.commons.TestConfiguration;
import sav.settings.SAVTimer;
import sav.strategies.dto.AppJavaClassPath;

public class GenerateTestHandler extends AbstractHandler {

	private void refreshProject(){
		IWorkspaceRoot myWorkspaceRoot = ResourcesPlugin.getWorkspace().getRoot();
		IProject iProject = myWorkspaceRoot.getProject(LearnTestConfig.projectName);
		
		try {
			iProject.refreshLocal(IResource.DEPTH_INFINITE, new NullProgressMonitor());
		} catch (CoreException e) {
			e.printStackTrace();
		}
		
	}
	
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		Job job = new Job("Do evaluation") {
			@Override
			protected IStatus run(IProgressMonitor monitor) {
				generateTest(LearnTestConfig.isL2TApproach);
				return Status.OK_STATUS;
			}
		};
		job.schedule();
		
		return null;
	}
	
	public RunTimeInfo generateTest(boolean isL2T){
		try {
			SAVTimer.enableExecutionTimeout = true;
			SAVTimer.exeuctionTimeout = 50000000;
			
			new TestGenerator().genTest();
			
			refreshProject();
			
			AppJavaClassPath appClasspath = initAppJavaClassPath();
			RunTimeInfo runtimeInfo = runLearntest(isL2T, appClasspath);
//			RunTimeInfo runtimeInfo = runLearntest2(isL2T, appClasspath);
			
			if(runtimeInfo != null){
				String type = isL2T ? "l2t" : "randoop";
				System.out.println(type + " time: " + runtimeInfo.getTime() + "; coverage: " + runtimeInfo.getCoverage());
			}
			
			refreshProject();
			
			return runtimeInfo;
			
		} catch (LearnTestException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SavException e) {
			e.printStackTrace();
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}

	private AppJavaClassPath initAppJavaClassPath() throws CoreException {
//		IProject project = IProjectUtils.getProject(LearnTestConfig.projectName);
//		IJavaProject javaProject = IProjectUtils.getJavaProject(project);
		AppJavaClassPath appClasspath = new AppJavaClassPath();
		appClasspath.setJavaHome(TestConfiguration.getJavaHome());
//		appClasspath.setJavaHome(IProjectUtils.getJavaHome(javaProject));
		appClasspath.addClasspaths(LearnTestUtil.getPrjectClasspath());
		String outputPath = LearnTestUtil.getOutputPath();
		appClasspath.setTarget(outputPath);
		appClasspath.setTestTarget(outputPath);
		appClasspath.getPreferences().set(SystemVariables.PROJECT_CLASSLOADER, LearnTestUtil.getPrjClassLoader());
		return appClasspath;
	}

	private RunTimeInfo runLearntest(boolean isL2T, AppJavaClassPath appClasspath) throws LearnTestException {
		LearnTest engine = new LearnTest(appClasspath);
		RunTimeInfo runtimeInfo = engine.run(!isL2T);
		return runtimeInfo;
	}

	/**
	 * To test new version of learntest which uses another cfg and jacoco for code coverage. 
	 * @param isL2T
	 * @param appClasspath
	 * @return
	 * @throws LearnTestException
	 */
	private RunTimeInfo runLearntest2(boolean isL2T, AppJavaClassPath appClasspath) throws LearnTestException {
		learntest.core.LearnTest learntest = new learntest.core.LearnTest(appClasspath);
		try {
			LearnTestParams params = LearnTestParams.initFromLearnTestConfig();
			RunTimeInfo runtimeInfo = learntest.run(params);
			return runtimeInfo;
		} catch (Exception e) {
			throw new LearnTestException(e);
		}
	}
}
