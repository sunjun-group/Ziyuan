package learntest.core.gentest.generator;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

import gentest.core.data.MethodCall;
import gentest.core.data.Sequence;
import gentest.core.data.statement.RArrayAssignment;
import gentest.core.data.statement.RqueryMethod;
import gentest.core.data.type.IType;
import gentest.core.data.type.ITypeCreator;
import gentest.core.data.variable.GeneratedVariable;
import gentest.core.data.variable.ISelectedVariable;
import gentest.core.value.generator.ValueGeneratorMediator;
import icsetlv.common.dto.BreakpointValue;
import learntest.core.commons.utils.DomainUtils;
import learntest.core.gentest.generator.FixLengthArrayValueGenerator.LocationHolderExecValue;
import sav.common.core.SavException;
import sav.common.core.utils.ArrayTypeUtils;
import sav.common.core.utils.CollectionUtils;
import sav.strategies.dto.BreakPoint.Variable.VarScope;
import sav.strategies.dto.execute.value.ArrayValue;
import sav.strategies.dto.execute.value.ExecValue;
import sav.strategies.dto.execute.value.ExecVar;
import sav.strategies.dto.execute.value.ExecVarType;
import sav.strategies.dto.execute.value.ReferenceValue;

public class TestSeqGenerator {
	private static final Logger log = LoggerFactory.getLogger(TestSeqGenerator.class);
	@Inject
	private ITypeCreator typeCreator;
	@Inject
	private ValueGeneratorMediator valueGenerator;
	@Inject
	private PrimitiveFixValueGenerator fixValueGenerator;
	@Inject
	private FixLengthArrayValueGenerator arrayValueGenerator;

	private static final String RECEIVER_PARAM_KEY = VarScope.THIS.getDisplayName();
	private Map<String, Class<?>> classMap;
	private Map<Class<?>, IType> typeMap;
	private MethodCall target;
	private IType receiverType;

	public TestSeqGenerator() {
		classMap = new HashMap<String, Class<?>>();
		typeMap = new HashMap<Class<?>, IType>();
	}

	public void setTarget(MethodCall target) {
		this.target = target;

		receiverType = null;
		if (target.requireReceiver()) {
			receiverType = typeCreator.forClass(target.getReceiverType());
			classMap.put(RECEIVER_PARAM_KEY, receiverType.getRawType());
			typeMap.put(receiverType.getRawType(), receiverType);
		}

		IType[] paramTypes = null;
		if (receiverType == null) {
			paramTypes = typeCreator.forType(target.getMethod().getGenericParameterTypes());
		} else {
			paramTypes = receiverType.resolveType(target.getMethod().getGenericParameterTypes());
		}

		String[] params = target.getParamNames();
		int index = 0;
		for (String param : params) {
			Class<?> clazz = paramTypes[index].getRawType();
			if (Modifier.isPublic(clazz.getModifiers())) {
				classMap.put(param, clazz);
				typeMap.put(clazz, paramTypes[index++]);
			}
		}
	}

	public Sequence generateSequence(double[] solution, List<ExecVar> vars, Set<String> failToSetVars)
			throws SavException {
		BreakpointValue breakpointValue = DomainUtils.toHierachyBreakpointValue(solution, vars);
		Sequence sequence = generateSequence(breakpointValue, failToSetVars);
		resetFailValues(vars, failToSetVars, solution, breakpointValue);
		return sequence;
	}
	
	/**
	 * LLT: If solution is still not up-to-date, consider to retrieve values from breakpointValue back to solution[].
	 * 9NOV2017
	 */
	private void resetFailValues(List<ExecVar> vars, Set<String> failToSetVars, double[] solution, BreakpointValue breakpointValue) {
		for (int i = 0; i < solution.length; i++) {
			String varId = vars.get(i).getVarId();
			if (failToSetVars.contains(varId)) {
				solution[i] = 0.0;
			} else {
				if (varId.endsWith(ReferenceValue.NULL_CODE)) {
					ExecValue value = breakpointValue.findVariableById(varId);
					solution[i] = value.getDoubleVal();
				}
			}
		}
	}

	public Sequence generateSequence(BreakpointValue breakpointValue, Set<String> failToSetVars)
			throws SavException {
		Sequence sequence = new Sequence();
		int firstVarIdx = 0;
		Map<String, ISelectedVariable> varMap = new HashMap<String, ISelectedVariable>();
		/* generate variables for method parameters */
		firstVarIdx = appendVariables(breakpointValue, sequence, failToSetVars, varMap, firstVarIdx);

		/* generate method parameters */
		String[] paramNames = target.getParamNames();
		int[] paramIds = new int[paramNames.length];
		for (int i = 0; i < paramIds.length; i++) {
			ISelectedVariable param = varMap.get(paramNames[i]);
			if (param == null) {
				param = valueGenerator.generate(typeMap.get(classMap.get(paramNames[i])), firstVarIdx, false);
				sequence.append(param);
				firstVarIdx += param.getNewVariables().size();
				varMap.put(paramNames[i], param);
			}
			paramIds[i] = param.getReturnVarId();
		}

		RqueryMethod rmethod = null;
		if (target.requireReceiver()) {
			ISelectedVariable receiverParam = varMap.get(RECEIVER_PARAM_KEY);
			if (receiverParam == null) {
				receiverParam = valueGenerator.generate(receiverType, firstVarIdx, true);
				sequence.append(receiverParam);
				firstVarIdx += receiverParam.getNewVariables().size();
			}
			rmethod = new RqueryMethod(target, receiverParam.getReturnVarId());
		} else {
			rmethod = new RqueryMethod(target);
		}
		rmethod.setInVarIds(paramIds);
		sequence.append(rmethod);
		return sequence;
	}

	private int appendVariables(BreakpointValue value, Sequence sequence, Set<String> failToSetVars,
			Map<String, ISelectedVariable> varMap, int firstVarIdx) throws SavException {
		for (ExecValue childVal : CollectionUtils.nullToEmpty(value.getChildren())) {
			firstVarIdx = appendVariables(null, childVal, sequence, failToSetVars, varMap, firstVarIdx);
		}
		return firstVarIdx;
	}
	
	private int appendVariables(ExecValue parent, ExecValue orgValue, Sequence sequence, Set<String> failToSetVars,
			Map<String, ISelectedVariable> varMap, int firstVarIdx) throws SavException {
		ExecValue value = orgValue;
		String receiver = value.getVarId();
		if (!classMap.containsKey(receiver)) {
			/* receiver type does not exist */
			addFailToSetVars(value, failToSetVars);
			return firstVarIdx;
		}
		Class<?> clazz = classMap.get(receiver);
		if (clazz.isArray() && (value.getType() != ExecVarType.ARRAY)) {
			value = ArrayValue.convert(value);
		}

		ExecVarType varType = value.getType();
		/* receiver type exist */
		if (value.isPrimitive()) {
			IType type = typeMap.get(classMap.get(value.getVarId()));
			GeneratedVariable variable = fixValueGenerator.generate(type, firstVarIdx, value.getDoubleVal());
			sequence.append(variable);
			varMap.put(value.getVarId(), variable);
			firstVarIdx += variable.getNewVariables().size();
		} else if (varType == ExecVarType.ARRAY) {
			firstVarIdx = appendArrayVariables(parent, (ArrayValue) value, sequence, failToSetVars, varMap, firstVarIdx,
					clazz);
			/* update back to original value in case arrayValue is updated */
			if (value != orgValue) {
				for (ExecValue child : CollectionUtils.nullToEmpty(value.getChildren())) {
					orgValue.add(child, true);
				}
			}
		} else if (varType == ExecVarType.REFERENCE) {
			firstVarIdx = appendReferenceVariables(parent, (ReferenceValue) value, sequence, failToSetVars, varMap, firstVarIdx);
		}
		
		return firstVarIdx;
	}

	private int appendReferenceVariables(ExecValue parent, ReferenceValue value, Sequence sequence,
			Set<String> failToSetVars, Map<String, ISelectedVariable> varMap, int firstVarIdx)
			throws SavException {
		String receiver = value.getVarId();
		if (value.isNull()) {
			if (value.getChildren().size() > 1) {
				log.warn("reset isNull to false for variable: {}", value.getVarId());
				value.setNull(false);
			} else {
				return firstVarIdx;
			}
		}
		
		ISelectedVariable variable = varMap.get(receiver);
		if (variable == null) {
			variable = valueGenerator.generate(typeMap.get(classMap.get(receiver)), firstVarIdx, true);
			sequence.append(variable);
			firstVarIdx += variable.getNewVariables().size();
			varMap.put(receiver, variable);
		}
		
		/* navigate its children */
		for (ExecValue fieldValue : CollectionUtils.nullToEmpty(value.getChildren())) {
			try {
				String fieldId = fieldValue.getVarId();
				Class<?> clazz = classMap.get(receiver);
				String fieldName = value.getFieldName(fieldValue);
				if (ReferenceValue.NULL_CODE.equals(fieldName)) {
					continue;
				}
				Class<?> fieldClazz = classMap.get(fieldId);
				if (fieldClazz == null) {
					fieldClazz = lookupFieldAndGetType(clazz, fieldName);
					updateClassTypeMap(fieldId, fieldClazz);
				}
				Method setter = findSetMethod(clazz, fieldName, fieldClazz);
				firstVarIdx = appendVariables(value, fieldValue, sequence, failToSetVars, varMap,
						firstVarIdx);
				ISelectedVariable field = varMap.get(fieldId);
				if (field != null) {
					RqueryMethod method = new RqueryMethod(MethodCall.of(setter, classMap.get(receiver)),
							variable.getReturnVarId());
					int[] varId = new int[] { field.getReturnVarId() };
					method.setInVarIds(varId);
					sequence.append(method);
				}
			} catch (Exception e) {
				addFailToSetVars(fieldValue, failToSetVars);
				return firstVarIdx;
			}
		}
		return firstVarIdx;
	}

	public void updateClassTypeMap(String varId, Class<?> varClazz) {
		classMap.put(varId, varClazz);
		IType type = typeMap.get(varClazz);
		if (type == null) {
			type = typeCreator.forClass(varClazz);
			typeMap.put(varClazz, type);
		}
	}

	private int appendArrayVariables(ExecValue parent, ArrayValue value, Sequence sequence, Set<String> failToSetVars,
			Map<String, ISelectedVariable> varMap, int firstVarIdx, Class<?> arrayClazz) throws SavException {
		String varId = value.getVarId();
		ISelectedVariable variable = varMap.get(varId);
		int dimension = ArrayTypeUtils.getArrayDimension(arrayClazz);
		Class<?> arrContentClazz = ArrayTypeUtils.getContentClass(classMap.get(varId));
		int[] arrayLength = FixLengthArrayValueGenerator.customizeArrayAndDetermineLength(value, arrContentClazz,
				dimension);
		if (dimension == 0) {
			if (!value.isNull()) {
				log.warn("reset isNull to true for variable: {}", value.getVarId());
			}
			value.setNull(true);
			return firstVarIdx;
		}
		IType type = typeMap.get(classMap.get(varId));
		if (variable == null) {
			/* TODO NICE TO HAVE-LLT: fix duplicate code */
			variable = arrayValueGenerator.generate(type, firstVarIdx, arrayLength);
			firstVarIdx += variable.getNewVariables().size();
			varMap.put(varId, variable);
		}
		IType arrContentType = getContentType(type);
		
		for (ExecValue element : value.collectAllValue(new ArrayList<>())) {
			classMap.put(element.getVarId(), arrContentClazz);
			IType eleType = typeMap.get(arrContentClazz);
			if (eleType == null) {
				typeMap.put(arrContentClazz, arrContentType);
				eleType = arrContentType;
			}
			if (element instanceof LocationHolderExecValue) {
				/* randomly generating value */
				GeneratedVariable eVariable = valueGenerator.generate(eleType, firstVarIdx, false);
				sequence.append(eVariable);
				firstVarIdx += eVariable.getNewVariables().size();
				varMap.put(element.getVarId(), eVariable);
			} else {
				/* provided value */
				firstVarIdx = appendVariables(value, element, sequence, failToSetVars, varMap,
						firstVarIdx);
			}
			ISelectedVariable elementVar = varMap.get(element.getVarId());
			int[] location = value.getLocation(element, dimension);
			RArrayAssignment arrayAssignment = new RArrayAssignment(
					variable.getReturnVarId(), location, elementVar.getReturnVarId());
			((GeneratedVariable)variable).append(arrayAssignment);
		}
		
		sequence.append(variable);
		return firstVarIdx;
	}

	private IType getContentType(IType type) {
		IType contentType = type;
		while (contentType .isArray()) {
			contentType = contentType.getComponentType();
		}
		return contentType;
	}

	private void addFailToSetVars(ExecValue value, Set<String> failToSetVars) {
		failToSetVars.add(value.getVarId());
	}

	private Method findSetMethod(Class<?> clazz, String fieldName, Class<?> fieldType) throws Exception {
		String methodName = new StringBuilder("set").append(StringUtils.capitalize(fieldName)).toString();
		Method setter = clazz.getMethod(methodName, fieldType);
		if (!Modifier.isPublic(setter.getModifiers())) {
			throw new SavException("Setter method [%s] is invisible!", methodName);
		}
		return setter;
	}

	private Class<?> lookupFieldAndGetType(Class<?> clazz, String fieldName) {
		if (clazz == Object.class || clazz == null) {
			return null;
		}
		Field field = null;
		try {
			field = clazz.getDeclaredField(fieldName);
		} catch (Exception e) {
			try {
				field = clazz.getField(fieldName);
			} catch (Exception e1) {
				return lookupFieldAndGetType(clazz.getSuperclass(), fieldName);
			}
		}
		return field.getType();
	}

}
