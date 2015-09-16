package assertion.template.threefeatures;

import java.util.ArrayList;
import java.util.List;

import icsetlv.common.dto.ExecValue;
import icsetlv.common.dto.ExecVar;
import sav.common.core.formula.Eq;
import sav.common.core.formula.Var;

public class ThreePrimGcdTemplate extends ThreeFeaturesTemplate {

	public ThreePrimGcdTemplate(List<List<ExecValue>> passExecValuesList, List<List<ExecValue>> failExecValuesList) {
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
	public boolean check() {
		// list of pass and fail exec value only has two features
		// first feature must be equals to mod between second and third feature
		for (List<ExecValue> evl : passExecValuesList) {
			int i1 = (int) evl.get(0).getDoubleVal();
			int i2 = (int) evl.get(1).getDoubleVal();
			int i3 = (int) evl.get(2).getDoubleVal();
			
			if (i2 <= 0 || i3 <= 0 || i1 != gcd(i2, i3)) {
				return false;
			}
		}
						
		// first feature must not be equals to mod between second and third feature
		for (List<ExecValue> evl : failExecValuesList) {
			int i1 = (int) evl.get(0).getDoubleVal();
			int i2 = (int) evl.get(1).getDoubleVal();
			int i3 = (int) evl.get(2).getDoubleVal();
					
			if (i2 > 0 && i3 > 0 && i1 == gcd(i2, i3)) {
				return false;
			}
		}
						
		return true;
	}

	@Override
	public List<List<Eq<?>>> sampling() {
		List<List<Eq<?>>> samples = new ArrayList<List<Eq<?>>>();
		
		ExecValue ev1 = passExecValuesList.get(0).get(0);
		Var v1 = new ExecVar(ev1.getVarId(), ev1.getType());
		
		ExecValue ev2 = passExecValuesList.get(0).get(1);
		Var v2 = new ExecVar(ev2.getVarId(), ev2.getType());
		
		ExecValue ev3 = passExecValuesList.get(0).get(2);
		Var v3 = new ExecVar(ev3.getVarId(), ev3.getType());
		
		List<Eq<?>> sample1 = new ArrayList<Eq<?>>();
		sample1.add(new Eq<Number>(v1, 1.0));
		sample1.add(new Eq<Number>(v2, 1.0));
		sample1.add(new Eq<Number>(v3, 1.0));
		
		List<Eq<?>> sample2 = new ArrayList<Eq<?>>();
		sample2.add(new Eq<Number>(v1, 1.0));
		sample2.add(new Eq<Number>(v2, 1.0));
		sample2.add(new Eq<Number>(v3, 2.0));
		
		List<Eq<?>> sample3 = new ArrayList<Eq<?>>();
		sample3.add(new Eq<Number>(v1, 2.0));
		sample3.add(new Eq<Number>(v2, 2.0));
		sample3.add(new Eq<Number>(v3, 4.0));
		
		samples.add(sample1);
		samples.add(sample2);
		samples.add(sample3);
		
		return samples;
	}
	
	@Override
	public String toString() {
		String id1 = passExecValuesList.get(0).get(0).getVarId();
		String id2 = passExecValuesList.get(0).get(1).getVarId();
		String id3 = passExecValuesList.get(0).get(2).getVarId();
		
		return id1 + " = gcd(" + id2 + ", " + id3 + ")";
	}
	
}
