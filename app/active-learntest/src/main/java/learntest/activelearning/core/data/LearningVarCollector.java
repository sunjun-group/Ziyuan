package learntest.activelearning.core.data;

import static sav.strategies.dto.execute.value.ExecVarHelper.getFieldId;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gentest.core.commons.utils.MethodUtils;
import microbat.util.PrimitiveUtils;
import sav.common.core.SavRtException;
import sav.common.core.utils.CollectionUtils;
import sav.strategies.dto.AppJavaClassPath;
import sav.strategies.dto.execute.value.ExecValue;
import sav.strategies.dto.execute.value.ExecVar;
import sav.strategies.dto.execute.value.ExecVarType;
import sav.strategies.dto.execute.value.ReferenceValue;

public class LearningVarCollector {
	private Logger log = LoggerFactory.getLogger(LearningVarCollector.class);
	private int variableLayer;
	private int arrSizeThreshold;
	private Map<String, String> varIdTypeMap = new HashMap<>();
	private ClassLoader classLoader;
	
	public LearningVarCollector(int variableLayer, int arrSizeThreshold) {
		this.arrSizeThreshold = arrSizeThreshold;
		this.variableLayer = variableLayer;
	}
	
	public List<ExecVar> collectLearningVars(AppJavaClassPath appClasspath, MethodInfo methodInfo,
			Collection<TestInputData> firstInputData) {
		this.classLoader = appClasspath.getClassLoader();
		List<ExecVar> vars = new ArrayList<>();
		try {
			initVarIdTypeMap(firstInputData);
			Class<?> clazz = classLoader.loadClass(methodInfo.getClassName());
			Method method = MethodUtils.findMethod(clazz, methodInfo.getMethodWithSignature());
			int i = 0;
			for (Class<?> paramType : method.getParameterTypes()) {
				String varId = methodInfo.getParams().get(i++);
				ExecVar execVar = appendVariable(paramType, varId, null, variableLayer);
				vars.add(execVar);
			}
		} catch (Throwable t) {
			throw new SavRtException(t);
		}
		return vars;
	}
	
	private void initVarIdTypeMap(Collection<TestInputData> firstInputData) {
		for (TestInputData input : firstInputData) {
			appendVarType(input.getInputValue().getChildren());
		}
	}

	private void appendVarType(List<ExecValue> values) {
		for (ExecValue value : values) {
			if (value instanceof ReferenceValue) {
				varIdTypeMap.put(value.getVarId(), value.getValueType());
				appendVarType(CollectionUtils.nullToEmpty(value.getChildren()));
			}
		}
	}

	private ExecVar appendVariable(Class<?> type, String varId, ExecVar parent, int retrieveLayer) {
		if (retrieveLayer <= 0) {
			return null;
		}
		ExecVar var = null;
		try {
			if (PrimitiveUtils.isString(type.getName())) {
				var = new ExecVar(varId, ExecVarType.STRING);
				appendVariable(boolean.class, var.getIsNullChildId(), var, retrieveLayer);
				appendVariable(int.class, var.getLengthChildId(), var, retrieveLayer);
				for (int idx = 0; idx < arrSizeThreshold - 2; idx++) {
					appendVariable(char.class, var.getStringCharId(idx), var, retrieveLayer);
				}
			} else if (PrimitiveUtils.isPrimitive(type.getName())) {
				var = new ExecVar(varId, ExecVarType.primitiveTypeOf(type.getName()));
			} else if (type.isArray()) {
				var = new ExecVar(varId, ExecVarType.ARRAY);
				appendVariable(boolean.class, var.getIsNullChildId(), var, retrieveLayer);
				appendVariable(int.class, var.getLengthChildId(), var, retrieveLayer);
				for (int idx = 0; idx < arrSizeThreshold; idx++) {
					appendVariable(type.getComponentType(), var.getElementId(idx), var, retrieveLayer - 1);
				}
			} else {
				String runtimeClass = varIdTypeMap.get(varId);
				if (runtimeClass != null && !runtimeClass.equals(type.getName())) {
					type = classLoader.loadClass(runtimeClass);
				}
				// reference type
				var = new ExecVar(varId, ExecVarType.REFERENCE);
				appendVariable(boolean.class, var.getIsNullChildId(), var, retrieveLayer);
				for (Field field : type.getDeclaredFields()) {
					if (!Modifier.isFinal(field.getModifiers())) {
						appendVariable(field.getType(), getFieldId(varId, field.getName()), var, retrieveLayer - 1);
					}
				}
			}
			var.setValueType(type.getName());
			if (parent != null) {
				parent.add(var);
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.debug(e.getMessage());
		}
		return var;
	}
}
