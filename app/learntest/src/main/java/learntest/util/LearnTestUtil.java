package learntest.util;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
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

import learntest.main.LearnTestConfig;
import sav.common.core.SavRtException;

@SuppressWarnings("restriction")
public class LearnTestUtil {
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
		IJavaProject project = JavaCore.create(getSpecificJavaProjectInWorkspace());
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
		IJavaProject project = JavaCore.create(getSpecificJavaProjectInWorkspace());
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
		IJavaProject project = JavaCore.create(getSpecificJavaProjectInWorkspace());
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
		int lineNumber = 0; 
		try{
			lineNumber = Integer.valueOf(lineNumberString);			
		}catch (Exception e){}
		
		CompilationUnit cu = findCompilationUnitInProject(className);
		
		MethodFinder finder = new MethodFinder(lineNumber, cu, methodName);
		
		cu.accept(finder);
		
		return finder.requiredMD;
	}
	
	public static List<IPackageFragmentRoot> findAllPackageRootInProject(){
		List<IPackageFragmentRoot> rootList = new ArrayList<>();
		IJavaProject project = JavaCore.create(getSpecificJavaProjectInWorkspace());
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
//		String projectPath = LearnTestUtil.getProjectPath();
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
	
	@SuppressWarnings({ "rawtypes", "resource" })
	public static Class retrieveClass(String qualifiedName){
		String outputPath = getOutputPath();
		File f = new File(outputPath);
		URL[] cp = new URL[1];
		try {
			cp[0] = f.toURI().toURL();
		} 
		catch (MalformedURLException e){
			e.printStackTrace();
		}
		URLClassLoader urlCL = new URLClassLoader(cp);
		Class clazz = null;
		try {
			clazz = urlCL.loadClass(qualifiedName);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		
		return clazz;
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
}
