package learntest.util;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.AbstractTypeDeclaration;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.internal.core.JarPackageFragmentRoot;
import org.eclipse.jdt.internal.core.PackageFragmentRoot;
import org.eclipse.jdt.launching.JavaRuntime;

import learntest.main.LearnTestConfig;
import sav.common.core.ModuleEnum;
import sav.common.core.SavException;
import sav.common.core.SavRtException;
import sav.common.core.utils.ObjectUtils;

@SuppressWarnings("restriction")
public class LearnTestUtil {
	private LearnTestUtil(){}
	
	public static CompilationUnit findCompilationUnitInProject(String qualifiedName){
		try{
			ICompilationUnit icu = findICompilationUnitInProject(qualifiedName);
			CompilationUnit cu = convertICompilationUnitToASTNode(icu);	
			return cu;
		}
		catch(IllegalStateException e){
			e.printStackTrace();
		} 
		
		return null;
	} 
	
	public static ICompilationUnit findICompilationUnitInProject(String qualifiedName){
		IJavaProject project = getJavaProject();
		try {
			IType type = project.findType(qualifiedName);
			if(type == null){
				type = project.findType(qualifiedName, new NullProgressMonitor());
			}
			
			if(type != null){
				ICompilationUnit icu = type.getCompilationUnit();
				return icu;
			}
			
		} catch (JavaModelException e1) {
			e1.printStackTrace();
		}
		
		return null;
	}
	
	@SuppressWarnings({ "rawtypes", "deprecation" })
	public static CompilationUnit convertICompilationUnitToASTNode(ICompilationUnit iunit){
		ASTParser parser = ASTParser.newParser(AST.JLS4);
		Map options = JavaCore.getOptions();
		JavaCore.setComplianceOptions(JavaCore.VERSION_1_7, options);
		parser.setCompilerOptions(options);
		parser.setKind(ASTParser.K_COMPILATION_UNIT);
		parser.setResolveBindings(true);
		parser.setSource(iunit);
		
		CompilationUnit cu = null;
		try{
			cu = (CompilationUnit) parser.createAST(null);		
			return cu;
		}
		catch(java.lang.IllegalStateException e){
			return null;
		}
	}
	
	public static IProject getSpecificJavaProjectInWorkspace(){
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		IWorkspaceRoot root = workspace.getRoot();
		IProject[] projects = root.getProjects();
		
		for(int i=0; i<projects.length; i++){
			if(LearnTestConfig.projectName.equals(projects[i].getName())){
				return projects[i];
				//return JavaCore.create(projects[i]);
			}
		}
		
		return null;
	}
	
	public static String getProjectPath(){
		IWorkspaceRoot myWorkspaceRoot = ResourcesPlugin.getWorkspace().getRoot();
		IProject iProject = myWorkspaceRoot.getProject(LearnTestConfig.projectName);
		return iProject.getLocation().toOSString();
	}
	
	public static IPackageFragmentRoot findMainPackageRootInProject(){
		IJavaProject project = getJavaProject();
		try {
			for(IPackageFragmentRoot packageFragmentRoot: project.getPackageFragmentRoots()){
				if(!(packageFragmentRoot instanceof JarPackageFragmentRoot) 
						&& packageFragmentRoot.getResource().toString().contains("main")){
					
					return packageFragmentRoot;
				}
			}
			
		} catch (JavaModelException e1) {
			e1.printStackTrace();
		}
		
		return null;
	}
	
	public static List<IPackageFragmentRoot> findMainPackageRootInProjects(){
		List<IPackageFragmentRoot> roots = new ArrayList<>();
		IJavaProject project = getJavaProject();
		try {
			for(IPackageFragmentRoot packageFragmentRoot: project.getPackageFragmentRoots()){
				if(!(packageFragmentRoot instanceof JarPackageFragmentRoot) 
						&& packageFragmentRoot.getResource().toString().contains("main")){
					
					roots.add(packageFragmentRoot);
				}
			}
			
		} catch (JavaModelException e1) {
			e1.printStackTrace();
		}
		
		return roots;
	}
	
	public static MethodDeclaration findSpecificMethod(String className, String methodName, String lineNumberString){
		return findSpecificMethod(className, methodName, ObjectUtils.toInteger(lineNumberString, 0));
	}

	public static MethodDeclaration findSpecificMethod(String className, String methodName, int lineNumber) {
		CompilationUnit cu = findCompilationUnitInProject(className);
		MethodFinder finder = new MethodFinder(cu, methodName, lineNumber);
		cu.accept(finder);
		return finder.getResult();
	}
	
	private static String getMethodWthSignatureByBindingKey(String className, String methodName, int lineNumber) {
		MethodDeclaration md = LearnTestUtil.findSpecificMethod(className, methodName, lineNumber);
		String key = md.resolveBinding().getKey();
		String mSig = key.substring(key.indexOf(".")+1);
		return mSig;
	}
	
	public static String getMethodWthSignature(String className, String methodName, int lineNumber)
			throws SavException {
		/* we cannot be sure if resolveBinding key is build in the same way as method signature, but this still works for now and faster,
		 * otherwise, call getMethodWthSignatureBySignParser instead. */
//		return getMethodWthSignatureByBindingKey(className, methodName, lineNumber);
		return getMethodWthSignatureBySignParser(className, methodName, lineNumber);
	}
	
	/**
	 * only the signature of method is returned, not include methodName. 
	 * if you need full method name with signature, use
	 * {@link LearnTestUtil#getMethodWthSignature(String, String, int)}
	 */
	public static String getMethodSignature(String className, String methodName, int lineNumber)
			throws SavException {
		IMethod method = findMethod(className, methodName, lineNumber);
		return getMethodSignature(method);
	}

	public static String getMethodSignature(IMethod method) throws SavException {
		try {
			return SignatureParser.getMethodSignature(method);
		} catch (JavaModelException e) {
			throw new SavException(ModuleEnum.UNSPECIFIED, e, e.getMessage());
		}
	}

	public static IMethod findMethod(String className, String methodName, int lineNumber) {
		MethodDeclaration methodDecl = findSpecificMethod(className, methodName, lineNumber);
		IMethod method = toIMethod(methodDecl);
		return method;
	}

	private static IMethod toIMethod(MethodDeclaration methodDecl) {
		return (IMethod) methodDecl.resolveBinding().getJavaElement();
	}

	public static String getMethodWthSignatureBySignParser(String className, String methodName, int lineNumber)
			throws SavException {
		IMethod method = findMethod(className, methodName, lineNumber);
		String signature = SignatureParser.parse(method);
		return signature;
	}

	public static List<IPackageFragmentRoot> findAllPackageRootInProject(){
		List<IPackageFragmentRoot> rootList = new ArrayList<>();
		IJavaProject project = getJavaProject();
		try {
			for(IPackageFragmentRoot packageFragmentRoot: project.getPackageFragmentRoots()){
				if(!(packageFragmentRoot instanceof JarPackageFragmentRoot)){
					rootList.add(packageFragmentRoot);
//					return packageFragmentRoot;
				}
			}
			
		} catch (JavaModelException e1) {
			e1.printStackTrace();
		}
		
		return rootList;
	}
	
	public static String retrieveTestSourceFolder() {
		IWorkspaceRoot myWorkspaceRoot = ResourcesPlugin.getWorkspace().getRoot();
		IProject iProject = myWorkspaceRoot.getProject(LearnTestConfig.projectName);
		IJavaProject javaProject = JavaCore.create(iProject);
		
		try {
			for(IPackageFragmentRoot root: javaProject.getAllPackageFragmentRoots()){
				if(root instanceof PackageFragmentRoot && !(root instanceof JarPackageFragmentRoot)){
					String name = root.getPath().toString();
					if(name.contains("test")){
						return getOsPath(root);
					}
					
					
				}
				
			}
		} catch (JavaModelException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	public static String getOsPath(IJavaElement element) {
		try {
			return element.getCorrespondingResource().getLocation().toString();
		} catch (JavaModelException e) {
			throw new SavRtException(e);
		}
	}
	
	public static String getOutputPath(){
		String projectPath = LearnTestUtil.getProjectPath();
		IWorkspaceRoot myWorkspaceRoot = ResourcesPlugin.getWorkspace().getRoot();
		IProject iProject = myWorkspaceRoot.getProject(LearnTestConfig.projectName);
		IJavaProject javaProject = JavaCore.create(iProject);
		
		String outputFolder = "";
		try {
			for(String seg: javaProject.getOutputLocation().segments()){
				if(!seg.equals(LearnTestConfig.projectName)){
					outputFolder += seg + File.separator;
				}
			}
		} catch (JavaModelException e) {
			e.printStackTrace();
		}
		
		String outputPath = projectPath + File.separator + outputFolder; 
		return outputPath;
	}
	
	public static Class<?> retrieveClass(String qualifiedName) throws ClassNotFoundException{
		return getPrjClassLoader().loadClass(qualifiedName);
	}
	
	public static List<MethodDeclaration> findTestingMethod(CompilationUnit cu) {
		boolean isSubclassOfTestCase = isSubclassOfTestCase(cu);
		
		TestingMethodChecker checker = new TestingMethodChecker(isSubclassOfTestCase);
		cu.accept(checker);
		
		List<MethodDeclaration> testingMethods = checker.getTestingMethods();
		
		return testingMethods;
	}
	
	public static boolean isSubclassOfTestCase(CompilationUnit cu) {
		if(cu.types().isEmpty()){
			return false;
		}
		
		AbstractTypeDeclaration typeDel = (AbstractTypeDeclaration) cu.types().get(0);
		ITypeBinding binding = typeDel.resolveBinding();
		
		boolean isSubclassOfTestCase = false;
		String parentName = "";
		while(true){
			if(binding == null){
				break;
			}
			
			ITypeBinding superBinding = binding.getSuperclass();
			if(superBinding == null){
				break;
			}
			
			parentName = superBinding.getQualifiedName();
			if(parentName.equals("junit.framework.TestCase")){
				isSubclassOfTestCase = true;
				break;
			}
			
			binding = superBinding;
		}
		
		return isSubclassOfTestCase;
	}
	
	public static String getFullNameOfCompilationUnit(CompilationUnit cu){
		String packageName = "";
		if(cu.getPackage() != null){
			packageName = cu.getPackage().getName().toString();
		}
		AbstractTypeDeclaration typeDeclaration = (AbstractTypeDeclaration) cu.types().get(0);
		String typeName = typeDeclaration.getName().getIdentifier();
		
		if(packageName.length() == 0){
			return typeName;
		}
		else{
			return packageName + "." + typeName; 			
		}
		
	}

	public static ClassLoader getPrjClassLoader() {
		try {
			List<URL> urlList = new ArrayList<URL>();
			List<String> classPathEntries = getPrjectClasspath();
			for (String cpEntry : classPathEntries) {
				IPath path = new Path(cpEntry);
				URL url = path.toFile().toURI().toURL();
				urlList.add(url);
			}
			ClassLoader parentClassLoader = getJavaProject().getClass().getClassLoader();
			URL[] urls = (URL[]) urlList.toArray(new URL[urlList.size()]);
			URLClassLoader classLoader = new URLClassLoader(urls, parentClassLoader);
			return classLoader;
		} catch (MalformedURLException e) {
			throw new SavRtException(e);
		}
	}
	
	public static List<String> getPrjectClasspath() {
		IJavaProject project = getJavaProject();
		try {
			String[] classPathEntries = JavaRuntime.computeDefaultRuntimeClassPath(project);
			return Arrays.asList(classPathEntries);
		} catch (CoreException e) {
			throw new SavRtException(e);
		}
	}

	public static IJavaProject getJavaProject() {
		return JavaCore.create(getSpecificJavaProjectInWorkspace());
	}

	public static String getMethodSignature(MethodDeclaration methodDecl) throws SavException {
		return getMethodSignature(toIMethod(methodDecl));
	}
}
