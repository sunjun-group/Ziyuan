package microbat.handler;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import microbat.Activator;
import microbat.codeanalysis.ast.LocalVariableScope;
import microbat.codeanalysis.ast.VariableScopeParser;
import microbat.codeanalysis.bytecode.MicrobatSlicer;
import microbat.codeanalysis.runtime.ExecutionStatementCollector;
import microbat.codeanalysis.runtime.TestcasesExecutor;
import microbat.model.BreakPoint;
import microbat.model.trace.Trace;
import microbat.util.JavaUtil;
import microbat.util.Settings;
import microbat.views.MicroBatViews;
import microbat.views.TraceView;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;

import sav.common.core.SavException;
import sav.commons.TestConfiguration;
import sav.strategies.dto.AppJavaClassPath;

public class StartDebugHandler extends AbstractHandler {

//	private TestcasesExecutor tcExecutor;
//	private AppJavaClassPath appClassPath;

	protected AppJavaClassPath initAppClasspath() {
		AppJavaClassPath appClasspath = new AppJavaClassPath();
		appClasspath.setJavaHome(TestConfiguration.getJavaHome());
		return appClasspath;
	}
	
	private AppJavaClassPath constructClassPaths(){
		IWorkspaceRoot myWorkspaceRoot = ResourcesPlugin.getWorkspace().getRoot();
		IProject iProject = myWorkspaceRoot.getProject(Settings.projectName);
		String projectPath = iProject.getLocationURI().getPath();
		projectPath = projectPath.substring(1, projectPath.length());
		projectPath = projectPath.replace("/", File.separator);
		
		String binPath = projectPath + File.separator + "bin"; 
		AppJavaClassPath appClassPath = initAppClasspath();
		
		appClassPath.addClasspath(binPath);
		appClassPath.setWorkingDirectory(projectPath);
		
		return appClassPath;
		
	}
	
	public Object execute(ExecutionEvent event) throws ExecutionException {
		final AppJavaClassPath appClassPath = constructClassPaths();
		
		final TestcasesExecutor tcExecutor = new TestcasesExecutor();
		
		final String classQulifiedName = Settings.buggyClassName;
		final int lineNumber = Integer.valueOf(Settings.buggyLineNumber);
		final String methodSign = convertSignature(classQulifiedName, lineNumber);
		
		System.currentTimeMillis();
		
		try {
			
			Job job = new Job("Preparing for Debugging ...") {
				
				@Override
				protected IStatus run(IProgressMonitor monitor) {
					BreakPoint ap = new BreakPoint(classQulifiedName, methodSign, lineNumber);
					List<BreakPoint> startPoints = Arrays.asList(ap);
					
					ExecutionStatementCollector collector = new ExecutionStatementCollector();
					List<BreakPoint> executingStatements = collector.collectBreakPoints(appClassPath);
					
					MicrobatSlicer slicer = new MicrobatSlicer(executingStatements);
					List<BreakPoint> breakpoints = null;
					try {
						System.out.println("start slicing...");
//						breakpoints = slicer.slice(appClassPath, startPoints);
						breakpoints = slicer.parsingBreakPoints(appClassPath);
						System.out.println("finish slicing!");
					} catch (Exception e1) {
						e1.printStackTrace();
					}
					
					/**
					 * find the variable scope for:
					 * 1) Identifying the same local variable in different trace nodes.
					 * 2) Generating variable ID for local variable.
					 */
					List<String> classScope = parseScope(breakpoints);
					parseLocalVariables(classScope);
					
					if(breakpoints == null){
						System.err.println("Cannot find any slice");
						return Status.OK_STATUS;
					}
					
					monitor.worked(60);
					
//					String methodName = methodSign.substring(0, methodSign.indexOf("("));
//					List<String> tests = Arrays.asList(classQulifiedName + "." + methodName);
//					tcExecutor.setup(appClasspath, tests);
					tcExecutor.setup(appClassPath);
					try {
						tcExecutor.run(breakpoints);
					} catch (SavException e) {
						e.printStackTrace();
					}
					
					monitor.worked(40);
					Trace trace = tcExecutor.getTrace();
					trace.constructDomianceRelation();
					//trace.conductStateDiff();
					
					Activator.getDefault().setCurrentTrace(trace);
					
					Display.getDefault().asyncExec(new Runnable(){
						
						@Override
						public void run() {
							try {
								updateViews();
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
						
					});
					
					
					return Status.OK_STATUS;
				}

				private List<String> parseScope(List<BreakPoint> breakpoints) {
					List<String> classes = new ArrayList<>();
					for(BreakPoint bp: breakpoints){
						if(!classes.contains(bp.getClassCanonicalName())){
							classes.add(bp.getClassCanonicalName());
						}
					}
					return classes;
				}
			};
			job.schedule();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}

	class MethodFinder extends ASTVisitor{
		CompilationUnit cu;
		MethodDeclaration methodDeclaration;
		int lineNumber;
		
		public MethodFinder(CompilationUnit cu, int lineNumber) {
			super();
			this.cu = cu;
			this.lineNumber = lineNumber;
		}

		public boolean visit(MethodDeclaration md){
			int startLine = cu.getLineNumber(md.getStartPosition());
			int endLine = cu.getLineNumber(md.getStartPosition()+md.getLength());
			
			if(startLine <= lineNumber && lineNumber <= endLine){
				methodDeclaration = md;
			}
			
			return false;
		}
	}
	
	private String convertSignature(String classQulifiedName, int lineNumber) {
		CompilationUnit cu = JavaUtil.findCompilationUnitInProject(classQulifiedName);
		
		MethodFinder finder = new MethodFinder(cu, lineNumber);
		cu.accept(finder);
		
		MethodDeclaration methodDeclaration = finder.methodDeclaration;
		IMethodBinding mBinding = methodDeclaration.resolveBinding();
		
		String returnType = mBinding.getReturnType().getKey();
		
		String methodName = mBinding.getName();
		
		List<String> paramTypes = new ArrayList<>();
		for(ITypeBinding tBinding: mBinding.getParameterTypes()){
			String paramType = tBinding.getKey();
			paramTypes.add(paramType);
		}
		
		StringBuffer buffer = new StringBuffer();
		buffer.append(methodName);
		buffer.append("(");
		for(String pType: paramTypes){
			buffer.append(pType);
			//buffer.append(";");
		}
		
		buffer.append(")");
		buffer.append(returnType);
//		
//		String sign = buffer.toString();
//		if(sign.contains(";")){
//			sign = sign.substring(0, sign.lastIndexOf(";")-1);			
//		}
//		sign = sign + ")" + returnType;
		
		String sign = buffer.toString();
		
		return sign;
	}

	/**
	 * This method is used to build the scope of local variables.
	 * @param classScope
	 */
	private void parseLocalVariables(final List<String> classScope) {
		VariableScopeParser vsParser = new VariableScopeParser();
		vsParser.parseLocalVariableScopes(classScope);
		List<LocalVariableScope> lvsList = vsParser.getVariableScopeList();
//		System.out.println(lvsList);
		Settings.localVariableScopes.setVariableScopes(lvsList);
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
	
//	private List<BreakPoint> testSlicing(){
//		List<BreakPoint> breakpoints = new ArrayList<BreakPoint>();
//		String clazz = "com.Main";
//	
//		BreakPoint bkp3 = new BreakPoint(clazz, null, 12);
//		bkp3.addVars(new Variable("c"));
//		bkp3.addVars(new Variable("tag", "tag", VarScope.THIS));
//		bkp3.addVars(new Variable("output"));
//		bkp3.addVars(new Variable("i"));
//	
//		BreakPoint bkp2 = new BreakPoint(clazz, null, 14);
//		bkp2.addVars(new Variable("c"));
//		bkp2.addVars(new Variable("tag", "tag", VarScope.THIS));
//		bkp2.addVars(new Variable("output"));
//	
//		BreakPoint bkp1 = new BreakPoint(clazz, null, 17);
//		bkp1.addVars(new Variable("c"));
//		bkp1.addVars(new Variable("tag", "tag", VarScope.THIS));
//		bkp1.addVars(new Variable("output"));
//	
//		breakpoints.add(bkp3);
//		breakpoints.add(bkp2);
//		breakpoints.add(bkp1);
//	
//		return breakpoints;
//	}
}
