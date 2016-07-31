package invariant.templates;

import java.util.ArrayList;
import java.util.List;

import libsvm.core.Machine;
import libsvm.extension.ByDistanceNegativePointSelection;
import libsvm.extension.NegativePointSelection;
import libsvm.extension.PositiveSeparationMachine;
import sav.common.core.formula.Eq;
import sav.strategies.dto.execute.value.ExecValue;

public abstract class SingleTemplate extends Template {

	protected List<List<ExecValue>> passValues;
	
	protected List<List<ExecValue>> failValues;
	
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

	public List<List<Eq<?>>> sampling() {
		return new ArrayList<List<Eq<?>>>();
	}
	
	public Machine getSimpleMachine() {
		Machine machine = new Machine();
		machine.setDefaultParams();
//		machine.setParameter(
//				new Parameter().setMachineType(MachineType.C_SVC).setKernelType(KernelType.LINEAR)
//				.setEps(1.0).setUseShrinking(false).setPredictProbability(false).setC(Double.MAX_VALUE));
		return machine;
	}
	
	public Machine getMultiCutMachine() {
		NegativePointSelection negative = new ByDistanceNegativePointSelection();
		PositiveSeparationMachine machine = new PositiveSeparationMachine(negative);
		machine.setDefaultParams();
		return machine;
	}
	
}
