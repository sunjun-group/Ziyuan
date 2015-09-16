package assertion.template;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import icsetlv.common.dto.ExecValue;
import sav.common.core.formula.Eq;

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
	
	public List<Eq<?>> solve() {
		return new ArrayList<Eq<?>>();
	}

	public List<List<Eq<?>>> sampling() {
		return new ArrayList<List<Eq<?>>>();
	}
	
}
