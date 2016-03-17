package invariant.templates.threefeatures;

import java.util.ArrayList;
import java.util.List;

import sav.common.core.formula.Eq;
import sav.common.core.formula.Var;
import sav.strategies.dto.execute.value.ExecValue;
import sav.strategies.dto.execute.value.ExecVar;
import sav.strategies.dto.execute.value.ExecVarType;

public class ThreeNumGcdTemplate extends ThreeFeaturesTemplate {

	public ThreeNumGcdTemplate(List<List<ExecValue>> passExecValuesList, List<List<ExecValue>> failExecValuesList) {
		super(passExecValuesList, failExecValuesList);
	}
	
	private int gcd(int a, int b) {
		while(b != 0) {
	       int t = b;
	       b = a % b;
	       a = t;
		}
		
	    return a;
	}
	
	@Override
	public boolean checkPassValue(List<ExecValue> evl) {
		// list of pass and fail exec value only has three features
		// first feature must be equals to gcd between second and third feature
		int v1 = (int) evl.get(0).getDoubleVal();
		int v2 = (int) evl.get(1).getDoubleVal();
		int v3 = (int) evl.get(2).getDoubleVal();
		return v2 > 0 && v3 > 0 && v1 == gcd(v2, v3);
	}
	
	@Override
	public boolean checkFailValue(List<ExecValue> evl) {
		// list of pass and fail exec value only has three features
		// first feature must not be equals to gcd between second and third feature
		int v1 = (int) evl.get(0).getDoubleVal();
		int v2 = (int) evl.get(1).getDoubleVal();
		int v3 = (int) evl.get(2).getDoubleVal();
		return v2 <= 0 || v3 <= 0 || v1 != gcd(v2, v3);
	}

	@Override
	public boolean check() {
		if (passValues.get(0).get(0).getType() != ExecVarType.INTEGER ||
				passValues.get(0).get(1).getType() != ExecVarType.INTEGER ||
				passValues.get(0).get(2).getType() != ExecVarType.INTEGER) {
			return false;
		} else {
			return check(passValues, failValues);
		}
	}
	
	@Override
	public List<List<Eq<?>>> sampling() {
		List<List<Eq<?>>> samples = new ArrayList<List<Eq<?>>>();
		
		ExecValue ev1 = passValues.get(0).get(0);
		Var v1 = new ExecVar(ev1.getVarId(), ev1.getType());
		
		ExecValue ev2 = passValues.get(0).get(1);
		Var v2 = new ExecVar(ev2.getVarId(), ev2.getType());
		
		ExecValue ev3 = passValues.get(0).get(2);
		Var v3 = new ExecVar(ev3.getVarId(), ev3.getType());
		
		List<Eq<?>> sample1 = new ArrayList<Eq<?>>();
		
		sample1.add(new Eq<Number>(v1, 1));
		sample1.add(new Eq<Number>(v2, 1));
		sample1.add(new Eq<Number>(v3, 1));
		
		List<Eq<?>> sample2 = new ArrayList<Eq<?>>();
		sample2.add(new Eq<Number>(v1, 1));
		sample2.add(new Eq<Number>(v2, 1));
		sample2.add(new Eq<Number>(v3, 2));
		
		List<Eq<?>> sample3 = new ArrayList<Eq<?>>();
		sample3.add(new Eq<Number>(v1, 2));
		sample3.add(new Eq<Number>(v2, 2));
		sample3.add(new Eq<Number>(v3, 4));
		
		samples.add(sample1);
		samples.add(sample2);
		samples.add(sample3);
		
		return samples;
	}
	
	@Override
	public String toString() {
		String id1 = passValues.get(0).get(0).getVarId();
		String id2 = passValues.get(0).get(1).getVarId();
		String id3 = passValues.get(0).get(2).getVarId();
		
		return id1 + " = gcd(" + id2 + ", " + id3 + ")";
	}
	
}
