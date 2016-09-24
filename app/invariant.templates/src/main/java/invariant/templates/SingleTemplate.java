package invariant.templates;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import libsvm.core.Machine;
import libsvm.extension.ByDistanceNegativePointSelection;
import libsvm.extension.NegativePointSelection;
import libsvm.extension.PositiveSeparationMachine;
import sav.common.core.formula.Eq;
import sav.common.core.formula.Var;
import sav.strategies.dto.execute.value.ExecValue;
import sav.strategies.dto.execute.value.ExecVarType;

public abstract class SingleTemplate extends Template {

	protected List<List<ExecValue>> passValues;
	
	protected List<List<ExecValue>> failValues;
	
	public int roundNum = 4;
	
	protected double offset = 0.01; // 1 / Math.pow(10, roundNum);
	
	public SingleTemplate(List<List<ExecValue>> passValues, List<List<ExecValue>> failValues) {
		this.passValues = passValues;
		this.failValues = failValues;
	}
	
	public List<List<ExecValue>> getPassExecValuesList() {
		return passValues;
	}
	
	public List<List<ExecValue>> getFailExecValuesList() {
		return failValues;
	}
	
	public void addPassValues(List<ExecValue> newPassValues) {
		passValues.add(newPassValues);
	}
	
	public void addFailValues(List<ExecValue> newFailValues) {
		failValues.add(newFailValues);
	}
	
	public boolean checkPassValue(List<ExecValue> evl) {
		return false;
	}
	
	public boolean checkFailValue(List<ExecValue> evl) {
		return false;
	}
	
	public boolean checkPassValues(List<List<ExecValue>> passExecValuesList) {
		for (List<ExecValue> evl : passExecValuesList) {
			if (!checkPassValue(evl)) {
				return false;
			}
		}
	
		return true;
	}
	
	public boolean checkFailValues(List<List<ExecValue>> failExecValuesList) {
		for (List<ExecValue> evl : failExecValuesList) {
			if (!checkFailValue(evl)) {
				return false;
			}
		}
		
		return true;
	}
	
	public boolean check(List<List<ExecValue>> passValues,
			List<List<ExecValue>> failValues) {
		boolean b1 = checkPassValues(passValues);
		boolean b2 = checkFailValues(failValues);
		return b1 && b2; 
	}
	
	public boolean check() {
		return check(passValues, failValues);
	}

	public boolean validateInput() {
		List<List<ExecValue>> allValues = new ArrayList<List<ExecValue>>();
		allValues.addAll(passValues);
		allValues.addAll(failValues);
		
		for (List<ExecValue> evl : allValues) {
			for (ExecValue ev : evl) {
				if (ev.getDoubleVal() == null) return false;
			}
		}
		
		return true;
	}
	
	public List<List<Eq<?>>> sampling() {
		return new ArrayList<List<Eq<?>>>();
	}
	
	public double round(double value) {
		String s = "#.";
		
		for (int i = 0; i < roundNum; i++) s += "#";
		
		DecimalFormat df = new DecimalFormat(s);
		df.setRoundingMode(RoundingMode.HALF_UP);
		
		return Double.valueOf(df.format(value));
	}
	
	public Eq<?> getSample(Var v, ExecVarType t, double value) {
		switch (t) {
		case BYTE:
			return new Eq<Number>(v, (byte) value);
		case SHORT:
			return new Eq<Number>(v, (short) value);
		case INTEGER:
			return new Eq<Number>(v, (int) value);
		case LONG:
			return new Eq<Number>(v, (long) value);
		case FLOAT:
			return new Eq<Number>(v, (float) value);
		case DOUBLE:
			return new Eq<Number>(v, (double) value);
		default:
			return new Eq<Number>(v, (double) value);
		}
	}
	
	public List<Eq<?>> sampling(List<Var> vs, List<ExecVarType> ts, List<Double> values) {
		List<Eq<?>> sample = new ArrayList<Eq<?>>();
		
		if (vs.size() == ts.size() && vs.size() == values.size()) {
			for (int i = 0; i < vs.size(); i++) {
				sample.add(getSample(vs.get(i), ts.get(i), values.get(i)));
			}
		}
		
		return sample;
	}
	
	public List<Eq<?>> sampling(Var v, byte value) {
		List<Eq<?>> sample = new ArrayList<Eq<?>>();
		
		sample.add(new Eq<Number>(v, value));
		
		return sample;
	}
	
	public List<Eq<?>> sampling(Var v, short value) {
		List<Eq<?>> sample = new ArrayList<Eq<?>>();
		
		sample.add(new Eq<Number>(v, value));
		
		return sample;
	}
	
	public List<Eq<?>> sampling(Var v, int value) {
		List<Eq<?>> sample = new ArrayList<Eq<?>>();
		
		sample.add(new Eq<Number>(v, value));
		
		return sample;
	}
	
	public List<Eq<?>> sampling(Var v, long value) {
		List<Eq<?>> sample = new ArrayList<Eq<?>>();
		
		sample.add(new Eq<Number>(v, value));
		
		return sample;
	}
	
	public List<Eq<?>> sampling(Var v, float value) {
		List<Eq<?>> sample = new ArrayList<Eq<?>>();
		
		sample.add(new Eq<Number>(v, value));
		
		return sample;
	}
	
	public List<Eq<?>> sampling(Var v, double value) {
		List<Eq<?>> sample = new ArrayList<Eq<?>>();
		
		sample.add(new Eq<Number>(v, value));
		
		return sample;
	}
	
	public List<Eq<?>> sampling(Var v, ExecVarType t, double value) {
		List<Eq<?>> sample = new ArrayList<Eq<?>>();
		
		sample.add(getSample(v, t, value));
		
		return sample;
	}
	
	public Machine getSimpleMachine() {
		Machine machine = new Machine();
		machine.setDefaultParams();
		return machine;
	}
	
	public Machine getMultiCutMachine() {
		NegativePointSelection negative = new ByDistanceNegativePointSelection();
		PositiveSeparationMachine machine = new PositiveSeparationMachine(negative);
		machine.setDefaultParams();
		return machine;
	}
	
}
