/**
 * Copyright TODO
 */
package gentest.junit.variable;

import gentest.core.commons.utils.TypeUtils;
import gentest.core.data.Sequence;
import japa.parser.ast.type.PrimitiveType.Primitive;

import java.util.HashMap;
import java.util.Map;

/**
 * @author LLT 
 * 
 * Generate variable name based on its type name and its index
 * ex:
 * int var0 = 1 will be transformed
 * to int i0 = 1 ClassName var0 = new ClassName() will be transformed to
 * ClassName className = new ClassName() Class var0 = null will be
 * transformed to Class clazz = null
 * */
public class VariableNamer implements IVariableNamer {
	private static final int VAR_FIRST_IDX = 0;
	private static final String ARRAY_VAR_PREFIX = "arr";
	private Map<Integer, String> vars;
	private Map<String, Integer> varPrefixIdxMap;
	private static Map<String, String> varPrefixMap;
	static {
		//Boolean, Char, Byte, Short, Int, Long, Float, Double
		varPrefixMap = new HashMap<String, String>();
		varPrefixMap.put(Primitive.Boolean.name(), "b");
		varPrefixMap.put(Primitive.Char.name(), "c");
		varPrefixMap.put(Primitive.Byte.name(), "byte");
		varPrefixMap.put(Primitive.Short.name(), "s");
		varPrefixMap.put(Primitive.Int.name(), "i");
		varPrefixMap.put(Primitive.Long.name(), "l");
		varPrefixMap.put(Primitive.Float.name(), "f");
		varPrefixMap.put(Primitive.Double.name(), "d");
		varPrefixMap.put(Class.class.getSimpleName(), "clazz");
		varPrefixMap.put(Object.class.getSimpleName(), "obj");
		varPrefixMap.put(String.class.getSimpleName(), "str");
	}
	
	public VariableNamer() {
		varPrefixIdxMap = new HashMap<String, Integer>();
	}
	
	@Override
	public String getName(Class<?> type, int varId) {
		String var = vars.get(varId);
		if (var == null) {
			if (type.isArray()) {
				var = getArrVarName(varId);
			} else {
				String varPrefix = null;
				Primitive primitive = TypeUtils.getAssociatePrimitiveType(type);
				if (primitive != null) {
					varPrefix = primitive.name();
				} else {
					varPrefix = type.getSimpleName();
				}
				var = nextVar(varPrefix, varId);
			}
		}
		return var;
	}
	
	@Override
	public String getArrVarName(int varId) {
		return nextVar(ARRAY_VAR_PREFIX, varId);
	}
	
	@Override
	public String getExistVarName(int varIdx) {
		return vars.get(varIdx);
	}

	private String nextVar(String prefix, int varId) {
		String usedPrefix = varPrefixMap.get(prefix);
		if (usedPrefix == null) {
			usedPrefix = prefix.toLowerCase();
		}
		Integer curIdx = varPrefixIdxMap.get(usedPrefix);
		int nextIdx;
		if (curIdx == null) {
			nextIdx = VAR_FIRST_IDX;
		} else {
			nextIdx = curIdx + 1;
		}
		varPrefixIdxMap.put(usedPrefix, nextIdx);
		String newVar = usedPrefix + nextIdx;
		vars.put(varId, newVar);
		return newVar;
	}

	public void reset(Sequence method) {
		vars = new HashMap<Integer, String>();
		varPrefixIdxMap.clear();
	}
}
