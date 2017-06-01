package jdart.handler;

import java.util.List;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;

import learntest.main.LearnTestConfig;
import learntest.util.LearnTestUtil;
import main.RunJPF;
import sav.common.core.SystemVariables;
import sav.commons.TestConfiguration;
import sav.strategies.dto.AppJavaClassPath;

public class RunJDartHandler extends AbstractHandler {

	class MethodFinder extends ASTVisitor{
		
		MethodDeclaration method;
		
		String methodName;
		int lineNumber;
		CompilationUnit cu;
		
		public MethodFinder(String methodName, int lineNumber, CompilationUnit cu) {
			super();
			this.methodName = methodName;
			this.lineNumber = lineNumber;
			this.cu = cu;
		}

		public boolean visit(MethodDeclaration md){
			
			if(this.method!=null){
				return false;
			}
			
			if(md.getName().getFullyQualifiedName().equals(methodName)){
				int startLine = cu.getLineNumber(md.getStartPosition());
				int endLine = cu.getLineNumber(md.getStartPosition()+md.getLength());
				
				if(startLine<=lineNumber && lineNumber<=endLine){
					this.method = md;
					return false;
				}
				
			}
			
			return true;
			
		}
	}
	
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		
		List<String> classpaths = LearnTestUtil.getPrjectClasspath();
		StringBuffer buffer = new StringBuffer();
		for(String classpath: classpaths){
			buffer.append(classpath);
			buffer.append(";");
		}
		String pathString = buffer.toString();
		
		String className = LearnTestConfig.targetClassName;
		String methodName = LearnTestConfig.targetMethodName;
		String lineNumber = LearnTestConfig.targetMethodLineNum;
		CompilationUnit cu = LearnTestUtil.findCompilationUnitInProject(className);
		
		MethodFinder finder = new MethodFinder(methodName, Integer.valueOf(lineNumber), cu);
		cu.accept(finder);
		MethodDeclaration md = finder.method;
		
		StringBuffer buffer2 = new StringBuffer();
		buffer2.append("(");
		for(Object obj: md.parameters()){
			if(obj instanceof SingleVariableDeclaration){
				SingleVariableDeclaration svd = (SingleVariableDeclaration)obj;
				String paramName = svd.getName().getIdentifier();
				String paramType = svd.getType().toString();
				
				buffer2.append(paramName);
				buffer2.append(":");
				buffer2.append(paramType);
				
				buffer2.append(",");
			}
		}
		String paramString = buffer2.toString();
		paramString = paramString.substring(0, paramString.length()-1);
		paramString = paramString + ")";
		
		String[] quicksortConfig = new String[]{
				"+app=libs/jdart/jpf.properties",
				"+site=libs/jpf.properties",
				"+jpf-jdart.classpath+=" + pathString,
				"+target=" + className,
				"+concolic.method=" + methodName,
				"+concolic.method" + methodName + "=${target}." + methodName + paramString,
				"+concolic.method" + methodName + ".config=all_fields_symbolic"
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
