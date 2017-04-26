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
import learntest.main.RunTimeInfo;
import learntest.main.TestGenerator;
import learntest.util.LearnTestUtil;
import sav.common.core.SavException;
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
			
			AppJavaClassPath appClasspath = new AppJavaClassPath();
			appClasspath.setJavaHome(TestConfiguration.getJavaHome());
			appClasspath.addClasspaths(LearnTestUtil.getPrjectClasspath());
			
			LearnTest engine = new LearnTest(appClasspath);
			RunTimeInfo runtimeInfo = engine.run(!isL2T);
			
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
		}
		
		return null;
	}

}
