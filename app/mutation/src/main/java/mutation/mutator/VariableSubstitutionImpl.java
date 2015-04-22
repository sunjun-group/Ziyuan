package mutation.mutator;

import japa.parser.ast.type.Type;

import java.util.ArrayList;
import java.util.List;

import mutation.parser.ClassDescriptor;
import mutation.parser.MethodDescriptor;
import mutation.parser.VariableDescriptor;
import mutation.parser.VariableScope;

public class VariableSubstitutionImpl implements VariableSubstitution{

	private Type type;
	private int lineNumber;
	private ClassDescriptor descriptor;
	
	public VariableSubstitutionImpl(Type type, int lineNumber, ClassDescriptor descriptor) {
		this.type = type;
		this.lineNumber = lineNumber;
		this.descriptor = descriptor;
	}
	
	@Override
	public List<VariableDescriptor> find() {
		List<VariableDescriptor> result = new ArrayList<VariableDescriptor>();
		result.addAll(findOnSingleClassDescriptor(descriptor));
		
		for(ClassDescriptor innerClass: descriptor.getInnerClasses()){
			result.addAll(findOnSingleClassDescriptor(innerClass));
		}
		return result;
	}
	
	private List<VariableDescriptor> findOnSingleClassDescriptor(ClassDescriptor singleClassDescriptor) {
		
		List<VariableDescriptor> result = new ArrayList<VariableDescriptor>();
		
		for(VariableDescriptor field: singleClassDescriptor.getFields()){
			if(field.getType().equals(type)){
				result.add(field);
			}
		}
		
		for(MethodDescriptor method: singleClassDescriptor.getMethods()){
			if(method.containsLine(lineNumber)){
				for(VariableScope scope: method.getLocalVars()){
					if(scope.containsLine(lineNumber)){
						for(VariableDescriptor localVarInMethod: scope.getVars().values()){
							if(localVarInMethod.getType().equals(type)){
								result.add(localVarInMethod);
							}
						}
					}
				}
			}
		}
		return result;
	}
}
