package invariant.templates;

import java.util.ArrayList;
import java.util.List;

import libsvm.core.Machine;
import sav.common.core.formula.Eq;
import sav.strategies.dto.execute.value.ExecValue;

public class Template {

	protected List<List<ExecValue>> passExecValuesList;
	
	protected List<List<ExecValue>> failExecValuesList;
	
	public Template(List<List<ExecValue>> passExecValuesList, List<List<ExecValue>> failExecValuesList) {
		this.passExecValuesList = passExecValuesList;
		this.failExecValuesList = failExecValuesList;
	}
	
	public void addPassValues(List<ExecValue> passExecValues) {
		passExecValuesList.add(passExecValues);
	}
	
	public void addFailValues(List<ExecValue> failExecValues) {
		failExecValuesList.add(failExecValues);
	}
	
	public boolean check() {
		return false;
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
