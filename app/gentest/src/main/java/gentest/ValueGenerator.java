/**
 * Copyright TODO
 */
package gentest;

import extcos.ReferenceAnalyser;
import gentest.commons.utils.TypeUtils;
import gentest.data.statement.RArrayAssignment;
import gentest.data.statement.RArrayConstructor;
import gentest.data.statement.RAssignment;
import gentest.data.statement.RConstructor;
import gentest.data.variable.GeneratedVariable;

import java.lang.reflect.Modifier;

import sav.common.core.SavException;
import sav.strategies.gentest.IReferencesAnalyzer;

/**
 * @author LLT
 * TODO LLT: TO REMOVE
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
		} else if (type.isArray()) {
			// Generate the array
			final int dimension = 1 + type.getName().lastIndexOf('[');
			final RArrayConstructor arrayConstructor = new RArrayConstructor(dimension, type);
			variable.append(arrayConstructor);

			// Generate the array content
			int[] location = next(null, arrayConstructor.getSizes());
			while (location != null) {
				append(variable, level + 1, arrayConstructor.getContentType());
				int localVariableID = variable.getReturnVarId(); // the last ID
				// get the variable
				RArrayAssignment arrayAssignment = new RArrayAssignment(
						arrayConstructor.getOutVarId(), location, localVariableID);
				variable.append(arrayAssignment);
				location = next(location, arrayConstructor.getSizes());
			}
		} else if (isInterfaceOrAbstract(type)) {
			// (!) NOTE: must check this AFTER the check for array
			// Because Modifier.isAbstract() return true for array types
			Class<?> randomImpl = new ReferenceAnalyser()
					.getRandomImplClzz(type);
			if (randomImpl == null) {
				variable.append(RAssignment.assignmentFor(type, null));
				return;
			}
			append(variable, level, randomImpl);
		} else {
			RConstructor rconstructor = RConstructor.of(type.getConstructors()[0]);
			int[] paramIds = new int[rconstructor.getInputTypes().size()];
			int i = 0;
			for (Class<?> paramType : rconstructor.getInputTypes()) {
				append(variable, level, paramType);
				paramIds[i ++] = variable.getLastVarId();
			}
			variable.append(rconstructor, paramIds);
		}
	}
	
	public static void main(String[] args) {
		int[] size = {0};
		int[] array = next(null, size);
		while (array!= null) {
			for (int i=0; i<array.length; i++) {
				System.out.print(array[i] + " ");
			}
			System.out.println();
			array = next(array, size);
		}
	}

	private static int[] next(int[] array, int[] limit) {
		if (array == null) {
			final int[] result = new int[limit.length];
			for (int i = 0; i < result.length; i++) {
				if (limit[i] > 0) {
					result[i] = 0;
				} else {
					return null;
				}
			}
			return result;
		}
		int i = 0;
		while (i < array.length && array[i] >= limit[i] - 1) {
			i++;
		}
		if (i >= array.length) {
			return null;
		} else {
			array[i]++;
			if (i - 1 >= 0 && array[i - 1] == limit[i - 1] - 1) {
				for (int j = 0; j < i; j++) {
					array[j] = 0;
				}
			}
			return array;
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
