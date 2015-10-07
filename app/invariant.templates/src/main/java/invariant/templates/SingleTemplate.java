package invariant.templates;

import java.util.ArrayList;
import java.util.List;

import libsvm.core.Machine;
import sav.common.core.formula.Eq;
import sav.strategies.dto.execute.value.ExecValue;

public class SingleTemplate extends Template {

	protected List<List<ExecValue>> passExecValuesList;
	
	protected List<List<ExecValue>> failExecValuesList;
	
	protected boolean isSatisfiedAllPassValues = false;
	
	protected boolean isSatisfiedAllFailValues = false;
	
	public SingleTemplate(List<List<ExecValue>> passExecValuesList, List<List<ExecValue>> failExecValuesList) {
		this.passExecValuesList = passExecValuesList;
		this.failExecValuesList = failExecValuesList;
	}
	
	public List<List<ExecValue>> getPassExecValuesList() {
		return passExecValuesList;
	}
	
	public List<List<ExecValue>> getFailExecValuesList() {
		return failExecValuesList;
	}
	
	public void addPassValues(List<ExecValue> passExecValues) {
		passExecValuesList.add(passExecValues);
	}
	
	public void addFailValues(List<ExecValue> failExecValues) {
		failExecValuesList.add(failExecValues);
	}
	
	public boolean isSatisfiedAllPassValues() {
		return isSatisfiedAllPassValues;
	}
	
	public boolean isSatisfiedAllFailValues() {
		return isSatisfiedAllFailValues;
	}
	
	public boolean checkPassValue(List<ExecValue> evl) {
		return false;
	}
	
	public boolean checkFailValue(List<ExecValue> evl) {
		return false;
	}
	
	public boolean checkAllPassValues(List<List<ExecValue>> passExecValuesList) {
		for (List<ExecValue> evl : passExecValuesList) {
			if (!checkPassValue(evl)) return false;
		}
		
		isSatisfiedAllPassValues = true;
		return true;
	}
	
	public boolean checkAllFailValues(List<List<ExecValue>> failExecValuesList) {
		for (List<ExecValue> evl : failExecValuesList) {
			if (!checkFailValue(evl)) return false;
		}
		
		isSatisfiedAllFailValues = true;
		return true;
	}
	
	public boolean check(List<List<ExecValue>> passExecValuesList,
			List<List<ExecValue>> failExecValuesList) {
		return checkAllPassValues(passExecValuesList) && checkAllFailValues(failExecValuesList);
	}
	
	public boolean check() {
		return check(passExecValuesList, failExecValuesList);
	}
	
	public boolean isChanged() {
		return false;
	}

	public List<List<Eq<?>>> sampling() {
		return new ArrayList<List<Eq<?>>>();
	}
	
	protected Machine getLearningMachine() {
		Machine machine = new Machine();
		machine.setDefaultParams();
		return machine;
	}
	
}
