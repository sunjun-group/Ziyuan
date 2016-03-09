package microbat.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.AbstractTypeDeclaration;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.internal.core.JarPackageFragmentRoot;
import org.eclipse.jdt.internal.corext.refactoring.typeconstraints.types.TypeTuple;

public class JavaUtil {
	
	private static HashMap<String, CompilationUnit> compilationUnitMap = new HashMap<>();
	
	/**
	 * generate signature such as methodName(java.lang.String)L
	 * @param md
	 * @return
	 */
	public static String generateMethodSignature(MethodDeclaration md){
		IMethodBinding mBinding = md.resolveBinding();
		
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
	
	public static String getFullNameOfCompilationUnit(CompilationUnit cu){
		String packageName = cu.getPackage().getName().toString();
		AbstractTypeDeclaration typeDeclaration = (AbstractTypeDeclaration) cu.types().get(0);
		String typeName = typeDeclaration.getName().getIdentifier();
		
		return packageName + "." + typeName; 
	}
	
	public static CompilationUnit findCompilationUnitInProject(String qualifiedName){
		//CompilationUnit cu = compilationUnitMap.get(qualifiedName);
		CompilationUnit cu = null;
		//if(null == cu){
			try{
				ICompilationUnit icu = findICompilationUnitInProject(qualifiedName);
				cu = convertICompilationUnitToASTNode(icu);	
				return cu;
			}
			catch(IllegalStateException e){
				e.printStackTrace();
			} 
		//}
		
		
		return cu;
	}
	
	public static ICompilationUnit findICompilationUnitInProject(String qualifiedName){
		IJavaProject project = JavaCore.create(getSpecificJavaProjectInWorkspace());
		try {
			IType type = project.findType(qualifiedName);
			if(type != null){
				return type.getCompilationUnit();
			}
			
		} catch (JavaModelException e1) {
			e1.printStackTrace();
		}
		
		return null;
	}
	
	public static IPackageFragmentRoot findTestPackageRootInProject(){
		IJavaProject project = JavaCore.create(getSpecificJavaProjectInWorkspace());
		try {
			for(IPackageFragmentRoot packageFragmentRoot: project.getPackageFragmentRoots()){
				if(!(packageFragmentRoot instanceof JarPackageFragmentRoot) && packageFragmentRoot.toString().contains("test")){
					
					return packageFragmentRoot;
//					IPackageFragment packageFrag = packageFragmentRoot.getPackageFragment(packageName);
//					
//					String fragName = packageFrag.getElementName();
//					if(packageFrag.exists() && fragName.equals(packageName)){
//						return packageFrag;
//					}
					
				}
			}
			
		} catch (JavaModelException e1) {
			e1.printStackTrace();
		}
		
		return null;
	}
	
	@SuppressWarnings("rawtypes")
	public static CompilationUnit convertICompilationUnitToASTNode(ICompilationUnit iunit){
		ASTParser parser = ASTParser.newParser(AST.JLS4);
		Map options = JavaCore.getOptions();
		JavaCore.setComplianceOptions(JavaCore.VERSION_1_7, options);
		parser.setCompilerOptions(options);
		parser.setKind(ASTParser.K_COMPILATION_UNIT);
		parser.setResolveBindings(true);
		parser.setSource(iunit);
		CompilationUnit cu = (CompilationUnit) parser.createAST(null);
		
		return cu;
	}
	
	public static IProject getSpecificJavaProjectInWorkspace(){
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		IWorkspaceRoot root = workspace.getRoot();
		IProject[] projects = root.getProjects();
		
		for(int i=0; i<projects.length; i++){
			if(Settings.projectName.equals(projects[i].getName())){
				return projects[i];
				//return JavaCore.create(projects[i]);
			}
		}
		
		return null;
	}

	public static boolean isTheLocationHeadOfClass(String sourceName, int lineNumber) {
		CompilationUnit cu = findCompilationUnitInProject(sourceName);
		AbstractTypeDeclaration type = (AbstractTypeDeclaration) cu.types().get(0);
		int headLine = cu.getLineNumber(type.getName().getStartPosition());
		
		return headLine==lineNumber;
	}

	public static boolean isCompatibleMethodSignature(String thisSig, String thatSig) {
		if(thatSig.equals(thisSig)){
			return true;
		}
		
		String thisClassName = thisSig.substring(0, thisSig.indexOf("#"));
		String thisMethodSig = thisSig.substring(thisSig.indexOf("#")+1, thisSig.length());
		
		String thatClassName = thatSig.substring(0, thatSig.indexOf("#"));
		String thatMethodSig = thatSig.substring(thatSig.indexOf("#")+1, thatSig.length());
		
		if(thisMethodSig.equals(thatMethodSig)){
			CompilationUnit thisCU = JavaUtil.findCompilationUnitInProject(thisClassName);
			CompilationUnit thatCU = JavaUtil.findCompilationUnitInProject(thatClassName);
			
			AbstractTypeDeclaration thisType = (AbstractTypeDeclaration) thisCU.types().get(0);
			AbstractTypeDeclaration thatType = (AbstractTypeDeclaration) thatCU.types().get(0);
			
			ITypeBinding thisTypeBinding = thisType.resolveBinding();
			ITypeBinding thatTypeBinding = thatType.resolveBinding();
			
			boolean isCom1 = thisTypeBinding.isSubTypeCompatible(thatTypeBinding);
			boolean isCom2 = thatTypeBinding.isSubTypeCompatible(thisTypeBinding);
			
			return isCom1 || isCom2;
		}
		
		return false;
	}
}
