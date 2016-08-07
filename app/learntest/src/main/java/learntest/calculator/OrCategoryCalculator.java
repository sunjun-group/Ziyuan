package learntest.calculator;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import icsetlv.common.dto.BreakpointValue;
import learntest.testcase.data.BreakpointData;
import libsvm.core.Category;
import libsvm.core.CategoryCalculator;
import libsvm.core.Divider;
import libsvm.core.Machine.DataPoint;
import libsvm.extension.MultiDividerBasedCategoryCalculator;
import net.sf.javailp.Constraint;
import net.sf.javailp.Linear;
import net.sf.javailp.Operator;
import sav.strategies.dto.execute.value.ExecVar;

public class OrCategoryCalculator implements CategoryCalculator {
	
	private List<List<CategoryCalculator>> calculators;
	private List<ExecVar> vars;
	private List<ExecVar> originalVars;
	
	public OrCategoryCalculator(List<List<CategoryCalculator>> calculators, List<ExecVar> vars, 
			List<ExecVar> originalVars) {
		this.calculators = calculators;
		this.vars = vars;
		this.originalVars = originalVars;
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
		int cnt = originalVars.size();
		int size = cnt + (1 + cnt) * cnt / 2;
		double[] lineVals = new double[size];
		int i = 0;
		for (ExecVar var : originalVars) {
			Double v = value.getValue(var.getLabel(), 0.0);
			lineVals[i++] = v;
		}
		for (int j = 0; j < cnt; j++) {
			for (int k = j; k < cnt; k++) {
				lineVals[i ++] = lineVals[j] * lineVals[k];
			}
		}
		DataPoint dp = new DataPoint(size);
		dp.setCategory(Category.NEGATIVE);
		dp.setValues(lineVals);
		return dp;
	}
	
	public List<List<Constraint>> getConstraints() {
		List<List<Constraint>> res = new ArrayList<List<Constraint>>();
		for (List<CategoryCalculator> calculatorList : calculators) {
			getConstraints(calculatorList, res);
		}
		return res;
	}

	private void getConstraints(List<CategoryCalculator> calculators, List<List<Constraint>> res) {
		for (CategoryCalculator calculator : calculators) {
			if (calculator instanceof MultiDividerBasedCategoryCalculator) {
				List<Constraint> cur = new ArrayList<Constraint>();
				List<Divider> dividers = ((MultiDividerBasedCategoryCalculator) calculator).getDividers();
				for (Divider divider : dividers) {
					Linear linear = new Linear();
					double[] thetas = divider.getThetas();
					double theta0 = divider.getTheta0();
					int size = Math.min(vars.size(), thetas.length);
					for (int i = 0; i < size; i++) {
						linear.add(thetas[i], vars.get(i).getLabel());
					}
					Constraint constraint = new Constraint(linear, Operator.GE, theta0);
					cur.add(constraint);
				}
				if (res.isEmpty()) {
					res.add(cur);
				} else {
					for (List<Constraint> list : res) {
						list.addAll(cur);
					}
				}
			} else if (calculator instanceof MultiNotDividerBasedCategoryCalculator) {
				List<Constraint> cur = new ArrayList<Constraint>();
				List<Divider> dividers = ((MultiNotDividerBasedCategoryCalculator) calculator).getDividers();
				for (Divider divider : dividers) {
					Linear linear = new Linear();
					double[] thetas = divider.getThetas();
					double theta0 = divider.getTheta0();
					int size = Math.min(vars.size(), thetas.length);
					for (int i = 0; i < size; i++) {
						linear.add(thetas[i], vars.get(i).getLabel());
					}
					Constraint constraint = new Constraint(linear, Operator.LE, theta0 - 1);
					cur.add(constraint);
				}
				if (res.isEmpty()) {
					for (Constraint constraint : cur) {
						List<Constraint> list = new ArrayList<Constraint>();
						list.add(constraint);
						res.add(list);
					}
				} else {
					List<List<Constraint>> tmp = new ArrayList<List<Constraint>>();
					for (List<Constraint> list : res) {
						for (Constraint constraint : cur) {
							List<Constraint> tmpList = new ArrayList<Constraint>(list);
							tmpList.add(constraint);
							tmp.add(tmpList);
						}
					}
					res.clear();
					res.addAll(tmp);
				}
			}
		}
	}

	public List<ExecVar> getVars() {
		return vars;
	}

	public List<List<CategoryCalculator>> getCalculators() {
		return calculators;
	}

}
