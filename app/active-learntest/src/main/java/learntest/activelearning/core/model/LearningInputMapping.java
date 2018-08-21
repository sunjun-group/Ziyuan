package learntest.activelearning.core.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import icsetlv.common.dto.BreakpointValue;
import sav.strategies.dto.execute.value.ExecValue;
import sav.strategies.dto.execute.value.ExecVar;
import sav.strategies.dto.execute.value.PrimitiveValue;
import sav.strategies.dto.execute.value.ReferenceValue;
import sav.strategies.dto.execute.value.StringValue;

public class LearningInputMapping {
	private List<ExecVar> learningVars;
	
	public LearningInputMapping(List<ExecVar> inputVars) {
		this.learningVars = inputVars;
	}

	public List<List<ExecValue>> getLearningValue(Collection<TestInputData> inputData) {
		List<List<ExecValue>> learningInputValues = new ArrayList<>(inputData.size()); 
		for (TestInputData singleInputData : inputData) {
			learningInputValues.add(extractLearningValues(singleInputData.getInputValue()));
		}
		return learningInputValues;
	}
	
	public List<ExecValue> extractLearningValues(BreakpointValue bkValue) {
		List<ExecValue> input = new ArrayList<>(learningVars.size());
		for (int i = 0; i < learningVars.size(); i++) {
			input.add(getValue(learningVars.get(i), bkValue));
		}
		return input;
	}
	
	public ExecValue getValue(ExecVar execVar, BreakpointValue bkValue) {
		ExecValue value = bkValue.findVariableById(execVar.getVarId());
		if (value != null) {
			return value;
		}
		switch (execVar.getType()) {
		case ARRAY:
		case REFERENCE:
			return new ReferenceValue(execVar.getVarId(), true);
		case STRING:
			return new StringValue(execVar.getVarId(), null);
		case BOOLEAN:
		case BYTE:
		case CHAR:
		case DOUBLE:
		case FLOAT:
		case INTEGER:
		case LONG:
		default:
			return new PrimitiveValue(execVar.getVarId(), "0", execVar.getValueType());
		}
	}

	public static double[] toDatapoint(List<ExecVar> vars, BreakpointValue value) {
		double[] datapoint = new double[vars.size()];
		for (int i = 0; i < vars.size(); i++) {
			datapoint[i] = value.getValue(vars.get(i).getVarId(), 0.0);
		}
		return datapoint;
	}
}
