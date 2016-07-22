package learntest.testcase.data;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import icsetlv.common.dto.BreakpointValue;
import learntest.breakpoint.data.DecisionLocation;
import libsvm.core.Category;
import libsvm.core.Machine.DataPoint;

public abstract class BreakpointData implements Comparable<BreakpointData> {
	
	protected DecisionLocation location;
	protected List<BreakpointValue> trueValues;
	protected List<BreakpointValue> falseValues;
	protected List<BreakpointValue> oneTimeValues;
	protected List<BreakpointValue> moreTimesValues;
	
	public BreakpointData(DecisionLocation location) {
		this.location = location;
		trueValues = new ArrayList<BreakpointValue>();
		falseValues = new ArrayList<BreakpointValue>();
	}
	
	public void addFalseValue(BreakpointValue bkpValue) {
		falseValues.add(bkpValue);
	}	

	public DecisionLocation getLocation() {
		return location;
	}
	
	public List<BreakpointValue> getTrueValues() {
		return trueValues;
	}
	
	public List<BreakpointValue> getFalseValues() {
		return falseValues;
	}
	
	public boolean merge(BreakpointData bkpData) {
		if (bkpData == null) {
			return false;
		}
		if (location.equals(bkpData.getLocation())) {
			trueValues.addAll(bkpData.getTrueValues());
			falseValues.addAll(bkpData.getFalseValues());
			return true;
		}
		return false;
	}
	
	public List<DataPoint> toTrueFalseDatapoints(List<String> labels) {
		Set<DataPoint> datapoints = new HashSet<DataPoint>();
		for (BreakpointValue bValue : trueValues) {
			datapoints.add(toDataPoint(labels, bValue, Category.POSITIVE));
		}

		for (BreakpointValue bValue : falseValues) {
			datapoints.add(toDataPoint(labels, bValue, Category.NEGATIVE));
		}
		return new ArrayList<DataPoint>(datapoints);
	}
	
	protected static DataPoint toDataPoint(List<String> labels, BreakpointValue bValue,
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
	public int compareTo(BreakpointData data) {
		return location.compareTo(data.location);
	}
	
}
