package microbat.util;

import java.util.List;

import microbat.evaluation.junit.TestingMethodChecker;

import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.TypeDeclaration;

public class JTestUtil {
	public static List<MethodDeclaration> findTestingMethod(CompilationUnit cu) {
		boolean isSubclassOfTestCase = isSubclassOfTestCase(cu);
		
		TestingMethodChecker checker = new TestingMethodChecker(isSubclassOfTestCase);
		cu.accept(checker);
		
		List<MethodDeclaration> testingMethods = checker.getTestingMethods();
		
		return testingMethods;
	}

	public static boolean isSubclassOfTestCase(CompilationUnit cu) {
		TypeDeclaration typeDel = (TypeDeclaration) cu.types().get(0);
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
}
