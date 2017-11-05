package learntest.core.gentest.generator;

import gentest.core.data.type.IType;
import gentest.core.data.variable.GeneratedVariable;
import gentest.core.value.generator.ArrayValueGenerator;
import gentest.core.value.generator.ValueGeneratorMediator;

public class FixLengthArrayValueGenerator {
	
//	public GeneratedVariable generate(IType type, int firstVarId, int size) {
//		IType contentType = type;
//		while (contentType.isArray()) {
//			contentType = contentType.getComponentType();
//		}
//		
//		GeneratedVariable root = new GeneratedVariable(firstVarId);
//		GeneratedVariable variable = root.newVariable();
//		RArrayConstructor arrayConstructor = new RArrayConstructor(new int[] {size}, type.getRawType(), contentType.getRawType());
//		variable.append(arrayConstructor);
//		variable.commitReturnVarIdIfNotExist();
//		root.append(variable);
//		return variable;
//	}
	
	public GeneratedVariable generate(IType type, int firstVarId, int size, ValueGeneratorMediator valueGeneratorMediator) {

		IType contentType = type;
		while (contentType.isArray()) {
			contentType = contentType.getComponentType();
		}
		GeneratedVariable root = new GeneratedVariable(firstVarId);
		GeneratedVariable variable = root.newVariable();
		ArrayValueGenerator generator = new ArrayValueGenerator(type);
		int[] sizes = new int[1];
		sizes[0] = size;
		try {
			generator.setValueGeneratorMediator(valueGeneratorMediator);
			generator.doAppendVariable(variable, 1, sizes);
			variable.commitReturnVarIdIfNotExist();
			root.append(variable);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return variable;
	}
	
}
