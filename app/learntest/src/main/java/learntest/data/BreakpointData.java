package learntest.data;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import icsetlv.common.dto.BreakpointValue;
import libsvm.core.Category;
import libsvm.core.Machine.DataPoint;
import sav.strategies.dto.BreakPoint;

public class BreakpointData {

	private BreakPoint bkp;
	private List<BreakpointValue> trueValues;
	private List<BreakpointValue> falseValues;
	
	public BreakpointData(BreakPoint bkp){
		this();
		this.bkp = bkp;
	}
	
	public BreakpointData(){
		trueValues = new ArrayList<BreakpointValue>();
		falseValues = new ArrayList<BreakpointValue>();
	}
	
	public void addTrueValue(BreakpointValue bkpValue) {
		trueValues.add(bkpValue);
	}
	
	public void addFalseValue(BreakpointValue bkpValue) {
		falseValues.add(bkpValue);
	}
	
	public BreakPoint getBkp() {
		return bkp;
	}
	
	public void setBkp(BreakPoint bkp) {
		this.bkp = bkp;
	}
	
	public List<BreakpointValue> getTrueValues() {
		return trueValues;
	}
	
	public void setTrueValues(List<BreakpointValue> trueValues) {
		this.trueValues = trueValues;
	}
	
	public List<BreakpointValue> getFalseValues() {
		return falseValues;
	}
	
	public void setFalseValues(List<BreakpointValue> falseValues) {
		this.falseValues = falseValues;
	}
	
	public List<DataPoint> toDatapoints(List<String> labels) {
		Set<DataPoint> datapoints = new HashSet<DataPoint>();
		for (BreakpointValue bValue : trueValues) {
			datapoints.add(toDataPoint(labels, bValue, Category.POSITIVE));
		}

		for (BreakpointValue bValue : falseValues) {
			datapoints.add(toDataPoint(labels, bValue, Category.NEGATIVE));
		}
		return new ArrayList<DataPoint>(datapoints);
	}
	
	public static DataPoint toDataPoint(List<String> labels, BreakpointValue bValue,
			Category category) {
		double[] lineVals = new double[labels.size()];
		int i = 0;
		for (String variableName : labels) {
			final Double value = bValue.getValue(variableName, 0.0);
			lineVals[i++] = value;
		}
		DataPoint dp = new DataPoint(labels.size());
		dp.setCategory(category);
		dp.setValues(lineVals);
		return dp;
	}
	
	@Override
	public String toString() {
		return "BreakpointData (" + bkp + "), \ntrueValues=" + trueValues
				+ ", \nfalseValues=" + falseValues + "]\n";
	}
	
}
