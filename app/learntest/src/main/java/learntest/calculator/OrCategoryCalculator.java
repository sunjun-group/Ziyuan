package learntest.calculator;

import java.util.Iterator;
import java.util.List;

import icsetlv.common.dto.BreakpointValue;
import learntest.testcase.data.BreakpointData;
import libsvm.core.Category;
import libsvm.core.CategoryCalculator;
import libsvm.core.Machine.DataPoint;
import sav.strategies.dto.execute.value.ExecVar;

public class OrCategoryCalculator implements CategoryCalculator {
	
	private List<List<CategoryCalculator>> calculators;
	private List<ExecVar> vars;
	
	public OrCategoryCalculator(List<List<CategoryCalculator>> calculators, List<ExecVar> vars) {
		this.calculators = calculators;
		this.vars = vars;
	}

	@Override
	public Category getCategory(DataPoint dataPoint) {
		if (calculators.isEmpty()) {
			return Category.POSITIVE;
		}
		for (List<CategoryCalculator> list : calculators) {
			if (getCategory(dataPoint, list) == Category.POSITIVE) {
				return Category.POSITIVE;
			}
		}
		return Category.NEGATIVE;
	}
	
	private Category getCategory(DataPoint dataPoint, List<CategoryCalculator> list) {
		for (CategoryCalculator calculator : list) {
			if (calculator.getCategory(dataPoint) == Category.NEGATIVE) {
				return Category.NEGATIVE;
			}
		}
		return Category.POSITIVE;
	}
	
	public void clear(BreakpointData breakpointData) {
		Iterator<BreakpointValue> values = breakpointData.getFalseValues().iterator();
		while (values.hasNext()) {
			BreakpointValue value = (BreakpointValue) values.next();
			if (getCategory(toDataPoint(value)) == Category.NEGATIVE) {
				values.remove();
			}
		}
	}
	
	private DataPoint toDataPoint(BreakpointValue value) {
		double[] lineVals = new double[vars.size()];
		int i = 0;
		for (ExecVar var : vars) {
			Double v = value.getValue(var.getLabel(), 0.0);
			lineVals[i++] = v;
		}
		DataPoint dp = new DataPoint(vars.size());
		dp.setCategory(Category.NEGATIVE);
		dp.setValues(lineVals);
		return dp;
	}

}
