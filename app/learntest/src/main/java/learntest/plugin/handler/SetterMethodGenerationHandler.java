package learntest.plugin.handler;

import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.core.dom.AbstractTypeDeclaration;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;

import learntest.core.LearnTestParams;
import learntest.plugin.utils.LearnTestUtil;

public class SetterMethodGenerationHandler extends AbstractLearntestHandler {

	
	
	@SuppressWarnings("rawtypes")
	@Override
	protected IStatus execute(IProgressMonitor monitor) {
		LearnTestParams params = initLearntestParamsFromPreference();
		
		CompilationUnit cu = LearnTestUtil.findCompilationUnitInProject(params.getTargetMethod().getClassName());
		List types = cu.types();
		if(!types.isEmpty()){
			AbstractTypeDeclaration atype = (AbstractTypeDeclaration) types.get(0);
			if(atype instanceof TypeDeclaration){
				TypeDeclaration type = (TypeDeclaration)atype;
				for(FieldDeclaration field: type.getFields()){
					if(doesNotHaveSetter(field, type)){
						generateSetterMethod();
					}
				}
			}
		}
		
		
		return Status.OK_STATUS;
	}

	private void generateSetterMethod() {
		// TODO Auto-generated method stub
		
	}

	private boolean doesNotHaveSetter(FieldDeclaration field, TypeDeclaration type) {
		String fieldType = field.getType().toString();
		Object obj = field.fragments().get(0);
		if(obj instanceof VariableDeclarationFragment){
			VariableDeclarationFragment frag = ((VariableDeclarationFragment)obj);
			String fieldName = frag.getName().getIdentifier();
			String firstChar = fieldName.substring(0, 1).toUpperCase();
			String restChars = fieldName.substring(1, fieldName.length());
			String supposedMethodName = "set" + firstChar + restChars;
			
			for(MethodDeclaration method: type.getMethods()){
				Type returnType = method.getReturnType2();
				
				if(returnType!=null && returnType.toString().equals("void")){
					String methodName = method.getName().getIdentifier();
					if(methodName.equals(supposedMethodName)){
						if(method.parameters().size()==1){
							Object o = method.parameters().get(0);
							if(o instanceof SingleVariableDeclaration){
								SingleVariableDeclaration svd = (SingleVariableDeclaration)o;
								String setType = svd.getType().toString();
								if(setType.equals(fieldType)){
									return true;
								}
							}
						}
					}
				}
			}
		}
		
		return false;
	}

	@Override
	protected String getJobName() {
		return "generate setter methods";
	}

}
