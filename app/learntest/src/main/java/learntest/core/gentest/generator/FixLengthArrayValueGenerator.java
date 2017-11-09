package learntest.core.gentest.generator;

import gentest.core.data.statement.RArrayConstructor;
import gentest.core.data.type.IType;
import gentest.core.data.variable.GeneratedVariable;

public class FixLengthArrayValueGenerator {
	
	public GeneratedVariable generate(IType type, int firstVarId, int[] arrayLength) {
		IType contentType = type;
		while (contentType.isArray()) {
			contentType = contentType.getComponentType();
		}

		GeneratedVariable variable = new GeneratedVariable(firstVarId);
		RArrayConstructor arrayConstructor = new RArrayConstructor(arrayLength, type.getRawType(),
				contentType.getRawType());
		variable.append(arrayConstructor);
		variable.commitReturnVarIdIfNotExist();
		return variable;
	}
	
	@Deprecated
	/**
	 * LLT: TO REMOVE! 9NOV2017
	 */
	public GeneratedVariable generate(IType type, int firstVarId, int size) {
		return generate(type, firstVarId, new int[] { size });
	}
	
}
