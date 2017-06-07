package jdart.handler;

import java.util.List;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;

import jdart.model.TestInput;
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
		
		List<TestInput> inputList = null;
		
		Job job = new Job("running JDart") {
			
			@Override
			protected IStatus run(IProgressMonitor monitor) {
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
				
				String[] config = new String[]{
						"+app=libs/jdart/jpf.properties",
						"+site=libs/jpf.properties",
						"+jpf-jdart.classpath+=" + pathString,
						"+target=" + className,
						"+concolic.method=" + methodName,
						"+concolic.method." + methodName + "=${target}." + methodName + paramString,
						"+concolic.method." + methodName + ".config=all_fields_symbolic"
				};
				
				List<TestInput> inputList = RunJPF.run(config);
				
				return Status.OK_STATUS;
			}
		};
		job.schedule();
		
		
		return null;
	}

	public static void main(String[] args) throws ExecutionException {

		String  pathString = "E:\\workspace\\JPF\\data\\apache-common-math-2.2\\bin", 
				className = "com.Sorting",				
				methodName = "quicksort",
				paramString = "(a:int[])";
		
		className = "org.apache.commons.math.analysis.integration.TrapezoidIntegrator";
		methodName = "integrate";
		paramString = "(ue:UnivariateRealFunction,mi:double, ma:double)";
		
		className = "org.apache.commons.math.distribution.BetaDistributionImpl";
		methodName = "cumulativeProbability";
		paramString = "(d:double)";
		
		className = "org.apache.commons.math.linear.OpenMapRealVector";
		methodName = "getLInfDistance";
		paramString = "(op:OpenMapRealVector)";
//		
//		className = "org.apache.commons.math.special.Gamma";
//		methodName = "regularizedGammaQ";
//		paramString = "(a:double, b:double)";
//		
		className = "org.apache.commons.math.stat.descriptive.moment.Variance";
		methodName = "evaluate";
		paramString = "(a:double[], b:double[], c:double, i1:int, i2:int)";
//		
		className = "org.apache.commons.math.stat.descriptive.summary.Sum";
		methodName = "evaluate";
		paramString = "(a:double[],i1:int,i2:int)";
//		
//		className = "org.apache.commons.math.stat.descriptive.summary.Sum";
//		methodName = "evaluate";
//		paramString = "(a:double[],b:double[], i1:int,i2:int)";
//
//		className = "org.apache.commons.math.stat.descriptive.summary.SumOfSquares";
//		methodName = "evaluate";
//		paramString = "(a:double[],i1:int,i2:int)";
//		
//		className = "org.apache.commons.math.util.FastMath";
//		methodName = "asinh";
//		paramString = "(a:double)";
//
//		className = "org.apache.commons.math.util.FastMath";
//		methodName = "atan2";
//		paramString = "(a:double, b:double)";
//
//		className = "org.apache.commons.math.util.FastMath";
//		methodName = "cos";
//		paramString = "(a:double)";
//
//		className = "org.apache.commons.math.util.FastMath";
//		methodName = "hypot";
//		paramString = "(a:double, b:double)";
//		
//		className = "org.apache.commons.math.util.FastMath";
//		methodName = "log1p";
//		paramString = "(a:double)";
//
//		className = "org.apache.commons.math.util.FastMath";
//		methodName = "nextAfter1";
//		paramString = "(a:double, b:double)";
//		
//		className = "org.apache.commons.math.util.FastMath";
//		methodName = "nextAfter2";
//		paramString = "(f:float, b:double)";
//
//		className = "org.apache.commons.math.util.FastMath";
//		methodName = "pow";
//		paramString = "(a:double, b:double)";		
//
//		className = "org.apache.commons.math.util.FastMath";
//		methodName = "scalb1";
//		paramString = "(a:double, i:int)";	
//
//		className = "org.apache.commons.math.util.FastMath";
//		methodName = "scalb2";
//		paramString = "(a:float, i:int)";
//		
//		className = "org.apache.commons.math.util.FastMath";
//		methodName = "sin";
//		paramString = "(a:double)";
//		
//		className = "org.apache.commons.math.util.FastMath";
//		methodName = "tan";
//		paramString = "(a:double)";
//
//		className = "org.apache.commons.math.util.MathUtils";
//		methodName = "binomialCoefficient";
//		paramString = "(a:int, b:int)";
//		
//		className = "org.apache.commons.math.util.MathUtils";
//		methodName = "compareTo";
//		paramString = "(a:double, b:double, c:double)";
//
//		className = "org.apache.commons.math.util.MathUtils";
//		methodName = "equals";
//		paramString = "(a:double, b:double)";
//
//		className = "org.apache.commons.math.util.MathUtils";
//		methodName = "equalsIncludingNaN";
//		paramString = "(a:double, b:double)";
//
//		className = "org.apache.commons.math.util.MathUtils";
//		methodName = "mulAndCheck";
//		paramString = "(a:int, b:int)";
//
//		className = "org.apache.commons.math.util.MathUtils";
//		methodName = "nextAfter";
//		paramString = "(a:double, b:double)";
//
//		className = "org.apache.commons.math.util.OpenIntToDoubleHashMap";
//		methodName = "findInsertionIndex";
//		paramString = "(a:int)";
//
//		className = "org.apache.commons.math.util.OpenIntToFieldHashMap";
//		methodName = "findInsertionIndex";
//		paramString = "(a:int)";
				
		String[] config = constructConfig(className, pathString, methodName, paramString);		
		List<TestInput> inputList = RunJPF.run(config);
	}

	private static String[] constructConfig(String className, String pathString, String methodName, String paramString) {
		return  new String[]{
				"+app=libs/jdart/jpf.properties",
				"+site=libs/jpf.properties",
				"+jpf-jdart.classpath+=" + pathString,
				"+target=" + className,
				"+concolic.method=" + methodName,
				"+concolic.method." + methodName + "=${target}." + methodName + paramString,
				"+concolic.method." + methodName + ".config=all_fields_symbolic"
		};
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
