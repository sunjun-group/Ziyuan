package microbat.util;

import java.util.HashMap;
import java.util.List;

import microbat.evaluation.junit.TestingMethodChecker;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.AbstractTypeDeclaration;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.TypeDeclaration;

import sav.strategies.dto.ClassLocation;

public class JTestUtil {
	public static List<MethodDeclaration> findTestingMethod(CompilationUnit cu) {
		boolean isSubclassOfTestCase = isSubclassOfTestCase(cu);
		
		TestingMethodChecker checker = new TestingMethodChecker(isSubclassOfTestCase);
		cu.accept(checker);
		
		List<MethodDeclaration> testingMethods = checker.getTestingMethods();
		
		return testingMethods;
	}

	public static boolean isSubclassOfTestCase(CompilationUnit cu) {
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

	private static HashMap<String, Boolean> testingClassMap = new HashMap<>();
	
	public static boolean isLocationInTestPackage(ClassLocation location) throws JavaModelException {
		String className = location.getClassCanonicalName();
		Boolean isIn = testingClassMap.get(className);
		if(isIn != null){
			return isIn;
		}
		else{
			IPackageFragmentRoot testRoot = JavaUtil.findTestPackageRootInProject();
			for(IJavaElement ele: testRoot.getChildren()){
				if(ele instanceof IPackageFragment){
					IPackageFragment pack = (IPackageFragment)ele;
					IJavaElement element = find(pack, className);
//					System.currentTimeMillis();
					if(element != null){
						testingClassMap.put(className, true);
						return true;
					}
				}
			}
			
			testingClassMap.put(className, false);
		}
		
		return false;
	}

	private static IJavaElement find(IPackageFragment pack, String className) throws JavaModelException {
		for(IJavaElement ele: pack.getChildren()){
			if(ele instanceof ICompilationUnit){
				ICompilationUnit icu = (ICompilationUnit)ele;
				String name = icu.getElementName();
				name = name.substring(0, name.indexOf(".java"));
				String packName = pack.getElementName();
				
				name = packName + "." + name;
				
				if(name.equals(className)){
					return ele;
				}
			}
			else if(ele instanceof IPackageFragment){
				IJavaElement result = find((IPackageFragment)ele, className);
				if(result != null){
					return result;
				}
			}
		}
		return null;
	}
}
