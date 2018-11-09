package learntest.activelearning.core.testgeneration.communication;

import java.util.ArrayList;
import java.util.List;

import learntest.activelearning.core.data.DpAttribute;
import learntest.activelearning.core.data.LearnTestContext;
import sav.strategies.dto.execute.value.ExecVar;

public class DataPoints {
	public List<ExecVar> varList;
	public List<double[]> values;
	public List<Boolean> labels = new ArrayList<>();
	public List<DpAttribute[]> attributes = new ArrayList<>();

	public DataPoints(List<ExecVar> varList, List<double[]> values) {
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

	public List<Boolean> getLabels() {
		return labels;
	}

	public void setLabels(List<Boolean> labels) {
		this.labels = labels;
	}

	public List<DpAttribute[]> convertToDpAttributeList() {
		return LearnTestContext.getLearnDataSetMapper().toDpAttributeVector(varList, values);
	}

}
