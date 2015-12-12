package microbat.handler;

import static sav.commons.TestConfiguration.SAV_COMMONS_TEST_TARGET;
import icsetlv.trial.model.Trace;
import icsetlv.variable.TestcasesExecutor;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import microbat.Activator;
import microbat.util.Settings;
import microbat.views.MicroBatViews;
import microbat.views.TraceView;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.ui.PlatformUI;

import sav.common.core.SavException;
import sav.common.core.SystemVariables;
import sav.common.core.utils.CollectionUtils;
import sav.common.core.utils.JunitUtils;
import sav.commons.TestConfiguration;
import sav.strategies.common.VarInheritCustomizer.InheritType;
import sav.strategies.dto.AppJavaClassPath;
import sav.strategies.dto.BreakPoint;
import sav.strategies.dto.BreakPoint.Variable;
import sav.strategies.dto.BreakPoint.Variable.VarScope;
import slicer.javaslicer.JavaSlicer;

;

public class StartDebugHandler extends AbstractHandler {

	private TestcasesExecutor tcExecutor;
	private AppJavaClassPath appClasspath;

	protected AppJavaClassPath initAppClasspath() {
		AppJavaClassPath appClasspath = new AppJavaClassPath();
		appClasspath.setJavaHome(TestConfiguration.getJavaHome());
		appClasspath.addClasspath(SAV_COMMONS_TEST_TARGET);
		return appClasspath;
	}

	public void setup() {
		appClasspath = initAppClasspath();
		appClasspath.getPreferences().putBoolean(SystemVariables.SLICE_COLLECT_VAR, true);
		appClasspath.getPreferences().put(SystemVariables.SLICE_BKP_VAR_INHERIT, InheritType.FORWARD.name());

		IWorkspaceRoot myWorkspaceRoot = ResourcesPlugin.getWorkspace()
				.getRoot();
		IProject iProject = myWorkspaceRoot
				.getProject(Settings.projectName);
		
		String projectPath = iProject.getLocationURI().getPath();
		projectPath = projectPath.substring(1, projectPath.length());
		
		appClasspath
				.addClasspath("F://workspace//runtime-debugging//Test//bin");
		tcExecutor = new TestcasesExecutor(6);
	}
	
	private List<BreakPoint> testSlicing(){
		List<BreakPoint> breakpoints = new ArrayList<BreakPoint>();
		String clazz = "com.Main";
		
		BreakPoint bkp3 = new BreakPoint(clazz, null, 12);
		bkp3.addVars(new Variable("c"));
		bkp3.addVars(new Variable("tag", "tag", VarScope.THIS));
		bkp3.addVars(new Variable("output"));
		bkp3.addVars(new Variable("i"));
		
//		BreakPoint bkp2 = new BreakPoint(clazz, null, 14);
//		bkp2.addVars(new Variable("c"));
//		bkp2.addVars(new Variable("tag", "tag", VarScope.THIS));
//		bkp2.addVars(new Variable("output"));
//		
//		BreakPoint bkp1 = new BreakPoint(clazz, null, 17);
//		bkp1.addVars(new Variable("c"));
//		bkp1.addVars(new Variable("tag", "tag", VarScope.THIS));
//		bkp1.addVars(new Variable("output"));
		
		breakpoints.add(bkp3);
//		breakpoints.add(bkp2);
//		breakpoints.add(bkp1);
		
		return breakpoints;
	}
	
	private List<BreakPoint> dynamicSlicing(List<BreakPoint> startPoint, List<String> classScope, List<String> testMethods){
		JavaSlicer slicer = new JavaSlicer();
		slicer.setFiltering(classScope, null);
		List<BreakPoint> result = null;
		try {
			result = 
				slicer.sliceDebug(appClasspath, startPoint, testMethods);
			
			System.currentTimeMillis();
//			List<String> paths = getSourceLocation();
//			VariableNameCollector vnc = new VariableNameCollector(VarNameCollectionMode.FULL_NAME, paths);
//			vnc.updateVariables(result);
			
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SavException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} /*catch (IcsetlvException e) {
			e.printStackTrace();
		}*/
		
		
		return result;
	}

	public Object execute(ExecutionEvent event) throws ExecutionException {
		setup();
		
		List<String> junitClassNames = CollectionUtils.listOf("com.test.MainTest");
		List<String> tests;
		try {
			
			File f0 = new File("F://workspace//runtime-debugging//Test//bin");
			URL[] cp = new URL[1];
			try {
				cp[0] = f0.toURI().toURL();
			} catch (MalformedURLException e) {
				e.printStackTrace();
			}
			URLClassLoader urlcl  = URLClassLoader.newInstance(cp);
			
			tests = JunitUtils.extractTestMethods(junitClassNames, urlcl);
			
			BreakPoint ap = new BreakPoint("com.test.MainTest", "test", 17);
			List<BreakPoint> assertionPoints = Arrays.asList(ap);
			
			List<String> classScope = Arrays.asList("com.Main", "com.Tag");
			List<BreakPoint> breakpoints = dynamicSlicing(assertionPoints, classScope, tests);
//			List<BreakPoint> breakpoints = testSlicing();
			
			tcExecutor.setup(appClasspath, tests);
			tcExecutor.run(breakpoints);
			//List<BreakpointData> result = tcExecutor.getResult();
			//System.out.println(result);
			
			Trace trace = tcExecutor.getTrace();
			Activator.getDefault().setCurrentTrace(trace);
			
			updateViews();
			
			//System.currentTimeMillis();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SavException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	private void updateViews() throws Exception{
		TraceView traceView = (TraceView) PlatformUI.getWorkbench().getActiveWorkbenchWindow().
				getActivePage().showView(MicroBatViews.TRACE);
		traceView.updateData();
	}
	
//	@SuppressWarnings("restriction")
//	private List<String> getSourceLocation(){
//		IProject iProject = JavaUtil.getSpecificJavaProjectInWorkspace();
//		IJavaProject javaProject = JavaCore.create(iProject);
//		
//		List<String> paths = new ArrayList<String>();
//		try {
//			for(IPackageFragmentRoot root: javaProject.getAllPackageFragmentRoots()){
//				if(!(root instanceof JarPackageFragmentRoot)){
//					String path = root.getResource().getLocationURI().getPath();
//					path = path.substring(1, path.length());
//					//path = path.substring(0, path.length()-Settings.projectName.length()-1);
//					path = path.replace("/", "\\");
//					
//					if(!paths.contains(path)){
//						paths.add(path);
//					}					
//				}
//			}
//		} catch (JavaModelException e) {
//			e.printStackTrace();
//		}
//		
//		return paths;
//	}
}
