package learntest.activelearning.core.data;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import gentest.core.commons.utils.MethodUtils;
import microbat.util.PrimitiveUtils;
import sav.common.core.SavRtException;
import sav.strategies.dto.AppJavaClassPath;
import sav.strategies.dto.execute.value.ExecVar;
import sav.strategies.dto.execute.value.ExecVarHelper;

public class LearningInputVariableCollector {
	private int variableLayer = 1;
	
	public List<ExecVar> collectLearningVars(AppJavaClassPath appClasspath, MethodInfo methodInfo) {
		try {
			List<ExecVar> vars = new ArrayList<>();
			Class<?> clazz = appClasspath.getClassLoader().loadClass(methodInfo.getClassName());
			Method method = MethodUtils.findMethod(clazz, methodInfo.getMethodWithSignature());
			
			return vars;
		} catch (Throwable t) {
			throw new SavRtException(t);
		}
	}
	
	private void appendVariable(List<ExecVar> vars, Class<?> type, String varId, int retrieveLayer) {
		if (retrieveLayer <= 0) {
			return;
		}
		if (PrimitiveUtils.isString(type.getName())) {
			try {
				String learningFieldOfString = "value";
				Class<?> charArrType = String.class.getField(learningFieldOfString).getType();
				appendVariable(vars, charArrType, ExecVarHelper.getFieldId(varId, learningFieldOfString), retrieveLayer);
			} catch (Exception e) {
			}
		} else if (PrimitiveUtils.isPrimitive(type.getName())) {
			
		}
		
	}
}
