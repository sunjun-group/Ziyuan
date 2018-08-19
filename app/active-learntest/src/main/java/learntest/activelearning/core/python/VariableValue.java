package learntest.activelearning.core.python;

import java.util.List;

import sav.strategies.dto.execute.value.ExecVar;

public class VariableValue{
	List<ExecVar> varList;
	List<double[]> values;
	public VariableValue(List<ExecVar> varList, List<double[]> values) {
		super();
		this.varList = varList;
		this.values = values;
	}
	public List<ExecVar> getVarList() {
		return varList;
	}
	public void setVarList(List<ExecVar> varList) {
		this.varList = varList;
	}
	public List<double[]> getValues() {
		return values;
	}
	public void setValues(List<double[]> values) {
		this.values = values;
	}
	
	
}
