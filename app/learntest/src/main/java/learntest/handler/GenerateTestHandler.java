package learntest.handler;

import java.io.IOException;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;

import japa.parser.ParseException;
import learntest.io.excel.Trial;
import learntest.main.Engine;
import learntest.main.LearnTestConfig;
import learntest.main.TestGenerator;
import learntest.util.LearnTestUtil;
import sav.common.core.SavException;
import sav.commons.TestConfiguration;
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
		
		generateTest();
		
		return null;
	}
	
	public Trial generateTest(){
		try {
			new TestGenerator().genTest();
			
			refreshProject();
			
			AppJavaClassPath appClasspath = new AppJavaClassPath();
			appClasspath.setJavaHome(TestConfiguration.getJavaHome());
			
//			appClasspath.addClasspath(SAV_COMMONS_TEST_TARGET);
//			appClasspath.addClasspath(TestConfiguration.getTestTarget(LearnTestConfig.MODULE));
			
			String outputPath = LearnTestUtil.getOutputPath();
			outputPath = outputPath.substring(1, outputPath.length());
			appClasspath.addClasspath(outputPath);
			
			Engine engine = new Engine(appClasspath);
			engine.run(false);
			
			refreshProject();
			
			//TODO
			return null;
			
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SavException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return null;
	}

}
