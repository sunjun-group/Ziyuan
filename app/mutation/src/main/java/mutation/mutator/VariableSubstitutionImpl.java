package mutation.mutator;

import japa.parser.ast.type.Type;

import java.util.ArrayList;
import java.util.List;

import sav.common.core.SavRtException;

import mutation.parser.ClassDescriptor;
import mutation.parser.MethodDescriptor;
import mutation.parser.VariableDescriptor;
import mutation.parser.LocalVariable;

public class VariableSubstitutionImpl implements VariableSubstitution{

	private Type type;
	private int lineNumber;
	private int column;
	private ClassDescriptor descriptor;
	
	public VariableSubstitutionImpl(Type type, int lineNumber, int column, ClassDescriptor descriptor) {
		this.type = type;
		this.lineNumber = lineNumber;
		this.column = column;
		this.descriptor = descriptor;
	}
	
	public VariableSubstitutionImpl(String varName, int lineNumber, int column, ClassDescriptor descriptor) {
		this.type = getType(lineNumber, varName, descriptor);
		this.lineNumber = lineNumber;
		this.column = column;
		this.descriptor = descriptor;
	} 
	
	private Type getType(int lineNumber, String varName, ClassDescriptor descriptor){
		
		Type type = searchVarTypeInMethods(lineNumber, varName, descriptor.getMethods());
		if(type != null){
			return type;
		}
		
		for(ClassDescriptor innerClass: descriptor.getInnerClasses()){
			type = searchVarTypeInMethods(lineNumber, varName, innerClass.getMethods());
			if(type != null){
				return type;
			}
		}
		
		throw new SavRtException("No Variable Found");
		
	}

	private Type searchVarTypeInMethods(int lineNumber, String varName,
			List<MethodDescriptor> methods) {
		for(MethodDescriptor method: methods){
			if(method.containsLine(lineNumber)){
				Type type = searchVarTypeInScopes(lineNumber, varName, method.getLocalVars());
				if(type != null){
					return type;
				}
			}
		}
		
		return null;
	}

	private Type searchVarTypeInScopes(int lineNumber, String varName,
			List<LocalVariable> scopes) {
		for(LocalVariable scope: scopes){
			if(scope.containsLine(lineNumber)){
				for(VariableDescriptor localVarInMethod: scope.getVars().values()){
					if(localVarInMethod.getName().equals(varName)){
						return localVarInMethod.getType();
					}
				}
			}
		}
		
		return null;
	}
	@Override
	public List<VariableDescriptor> find() {
		List<VariableDescriptor> result = new ArrayList<VariableDescriptor>();
		result.addAll(findVariables(descriptor));
		
		for(ClassDescriptor innerClass: descriptor.getInnerClasses()){
			result.addAll(findVariables(innerClass));
		}
		return result;
	}
	
	private List<VariableDescriptor> findVariables(ClassDescriptor classDescriptor) {
		
		List<VariableDescriptor> result = new ArrayList<VariableDescriptor>();
		
		for(VariableDescriptor field: classDescriptor.getFields()){
			if(field.getType().equals(type)){
				result.add(field);
			}
		}
		
		for(MethodDescriptor method: classDescriptor.getMethods()){
			if(method.containsLine(lineNumber)){
				findVariables(result, method.getLocalVars());
			}
		}
		return result;
	}

	private void findVariables(List<VariableDescriptor> result,
			List<LocalVariable> scopes) {
		for(LocalVariable scope: scopes){
			if(scope.containsLine(lineNumber)){
				for(VariableDescriptor localVarInMethod: scope.getVars().values()){
					if(isVisibleAndMatchType(localVarInMethod, lineNumber, column, type)){
						result.add(localVarInMethod);
					}
				}
			}
		}
	}
	
	private boolean isVisibleAndMatchType(VariableDescriptor localVarInMethod, int lineNumber, int column, Type type){
		return localVarInMethod.getPosition().declaredBefore(lineNumber, column) && localVarInMethod.getType().equals(type);
	}
}
