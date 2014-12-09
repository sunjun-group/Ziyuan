/**
 * Copyright TODO
 */
package gentest;

import gentest.commons.utils.TypeUtils;
import gentest.data.statement.RArrayConstructor;
import gentest.data.statement.RAssignment;
import gentest.data.statement.RConstructor;
import gentest.data.variable.GeneratedVariable;

import java.lang.reflect.Modifier;

import sav.common.core.SavException;
import sav.strategies.gentest.IReferencesAnalyzer;

/**
 * @author LLT
 * 
 */
public class ValueGenerator {
	protected Class<?> type;
	protected ParamGeneratorFactory generatorFactory;
	protected int maxLevel = 3;
	/* TODO LLT: just temporary, to refactor */
	private static IReferencesAnalyzer refAnalyzer;
	
	public ValueGenerator(ParamGeneratorFactory generatorFactory) {
		this.generatorFactory = generatorFactory;
	}

	public void setGeneratorFactory(ParamGeneratorFactory generatorFactory) {
		this.generatorFactory = generatorFactory;
	}

	public GeneratedVariable generate(Class<?> type, int firstStmtIdx, int firstVarId)
			throws SavException {
		this.type = type;
		GeneratedVariable variable = new GeneratedVariable(firstStmtIdx, firstVarId);
		append(variable, 1, type);
		return variable;
	}
	
	private boolean isInterfaceOrAbstract(Class<?> type){
		return (type.isInterface() || Modifier.isAbstract(type.getModifiers()));
	}
	
	public void append(GeneratedVariable variable, int level, Class<?> type) throws SavException {
		if (level == maxLevel) {
			variable.append(RAssignment.assignmentFor(type, null));
			return;
		}
		if (isSimpleType(type)) {
			Object value = generatorFactory.getGeneratorFor(type).next();
			variable.append(RAssignment.assignmentFor(type, value));
		} else if (isInterfaceOrAbstract(type)) {
//			Class<?> randomImpl = new ReferenceAnalyser().getRandomImplClzz(type);
//			append(variable, level, randomImpl);
		} else if (type.isArray()) {
			// Generate the array
			final int dimension = 1 + type.getName().lastIndexOf('[');
			final RArrayConstructor arrayConstructor = new RArrayConstructor(dimension, type);
			variable.append(arrayConstructor);
			
			// Generate the array content
			arrayConstructor.getOutVarId();
			// TODO NPN
		} else {
			RConstructor rconstructor = RConstructor.of(type.getConstructors()[0]);
			for (Class<?> paramType : rconstructor.getInputTypes()) {
				append(variable, level, paramType);
			}
			variable.append(rconstructor);
		}
	}

	private static boolean isSimpleType(Class<?> type) {
		return TypeUtils.isPrimitive(type) || TypeUtils.isPrimitiveObject(type)
				|| TypeUtils.isString(type) || TypeUtils.isEnumType(type);
	}
	
	public static void setRefAnalyzer(IReferencesAnalyzer refAnalyzer) {
		ValueGenerator.refAnalyzer = refAnalyzer;
	}
	
	public static IReferencesAnalyzer getRefAnalyzer() {
		return refAnalyzer;
	}
}
