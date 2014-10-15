/**
 * Copyright TODO
 */
package gentest;

import gentest.commons.utils.TypeUtils;
import gentest.data.statement.RAssignment;
import gentest.data.statement.RConstructor;
import gentest.data.variable.GeneratedVariable;
import sav.common.core.SavException;
import sav.common.core.SavRtException;

/**
 * @author LLT
 *
 */
public class ValueGenerator {
	protected Class<?> type;
	protected ParamGeneratorFactory generatorFactory;
	protected int maxLevel = 3;
	
	public ValueGenerator(ParamGeneratorFactory generatorFactory) {
		this.generatorFactory = generatorFactory;
	}

	public void setGeneratorFactory(ParamGeneratorFactory generatorFactory) {
		this.generatorFactory = generatorFactory;
	}
	
	public GeneratedVariable generate(Class<?> type, int firstStmtIdx,
			int firstVarId) throws SavException {
		this.type = type;
		GeneratedVariable variable = new GeneratedVariable(firstStmtIdx,
				firstVarId);
		append(variable, 1, type);
		return variable;
	}
	
	public void append(GeneratedVariable variable, int level, Class<?> type)
			throws SavException {
		if (level == maxLevel) {
			variable.append(RAssignment.assignmentFor(type, null));
			return;
		}
		if (isSimpleType(type)) {
			Object value = generatorFactory.getGeneratorFor(type).next();
			variable.append(RAssignment.assignmentFor(type, value));
		} else if (type.isInterface()) {
			throw new SavRtException("Generate for interface: NOT YET UNSUPPORTED!!");
		} else if (type.isArray()) {
			//TODO LLT: complete this
		} else {
			RConstructor rconstructor = RConstructor
					.of(type.getConstructors()[0]);
			for (Class<?> paramType : rconstructor.getInputTypes()) {
				append(variable, level, paramType);
			}
			variable.append(rconstructor);
		}
	}

	private static boolean isSimpleType(Class<?> type) {
		 return TypeUtils.isPrimitive(type)
			|| TypeUtils.isPrimitiveObject(type)
			|| TypeUtils.isString(type)
			|| TypeUtils.isEnumType(type);
	}
}
