package learntest.gentest;

import gentest.core.data.statement.RArrayConstructor;
import gentest.core.data.type.IType;
import gentest.core.data.variable.GeneratedVariable;

public class FixLengthArrayValueGenerator {
	
	public GeneratedVariable generate(IType type, int firstVarId, int size) {
		IType contentType = type;
		while (contentType.isArray()) {
			contentType = contentType.getComponentType();
		}
		
		GeneratedVariable root = new GeneratedVariable(firstVarId);
		GeneratedVariable variable = root.newVariable();
		RArrayConstructor arrayConstructor = new RArrayConstructor(new int[] {size}, type.getRawType(), contentType.getRawType());
		variable.append(arrayConstructor);
		variable.commitReturnVarIdIfNotExist();
		root.append(variable);
		return variable;
	}
	
}
