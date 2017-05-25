package jdart.handler;

import java.util.List;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.CoreException;

import learntest.util.LearnTestUtil;
import main.RunJPF;
import sav.common.core.SystemVariables;
import sav.commons.TestConfiguration;
import sav.strategies.dto.AppJavaClassPath;

public class RunJDartHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		
		List<String> classpaths = LearnTestUtil.getPrjectClasspath();
		StringBuffer buffer = new StringBuffer();
		for(String classpath: classpaths){
			buffer.append(classpath);
			buffer.append(";");
		}
		String pathString = buffer.toString();
		
		String[] quicksortConfig = new String[]{
				"+app=libs/jdart/jpf.properties",
				"+site=libs/jpf.properties",
				"+jpf-jdart.classpath+=" + pathString,
				"+target=com.Sorting",
				"+concolic.method=quicksort",
				"+concolic.method.quicksort=${target}.quicksort(a:int[])",
				"+concolic.method.quicksort.config=all_fields_symbolic"
		};
		
		RunJPF.run(quicksortConfig);
		
		return null;
	}

	public static void main(String[] args) throws ExecutionException {
		String[] quicksortConfig = new String[]{
				"+app=libs/jdart/jpf.properties",
				"+site=libs/jpf.properties",
//				"+jpf-jdart.classpath+=../../bin",
//				"+jpf-jdart.classpath=" + classpaths.get(0),
				"+target=com.Sorting",
				"+concolic.method=quicksort",
				"+concolic.method.quicksort=${target}.quicksort(a:int[])",
				"+concolic.method.quicksort.config=all_fields_symbolic",
				"+jpf-jdart.classpath=E:\\workspace\\JPF\\data\\apache-common-math-2.2\\bin",
//				"+target=features.simple.Input",
//				"+concolic.method=foo",
//				"+concolic.method.foo=${target}.foo(i:int)",
//				"+concolic.method.foo.config=all_fields_symbolic"
		};
		RunJPF.run(quicksortConfig);
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

}
