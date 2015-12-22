package invariant.templates.onefeature;

import java.util.ArrayList;
import java.util.List;

import libsvm.core.Category;
import libsvm.core.Machine;
import libsvm.core.StringDividerProcessor;
import sav.common.core.formula.Eq;
import sav.common.core.formula.Formula;
import sav.common.core.formula.LIAAtom;
import sav.common.core.formula.Var;
import sav.strategies.dto.execute.value.ExecValue;
import sav.strategies.dto.execute.value.ExecVar;

public class OnePrimIlpTemplate extends OneFeatureTemplate {

	private double a = 0.0;
	
	private double d = 0.0;
	
	private boolean change = false;
	
	public OnePrimIlpTemplate(List<List<ExecValue>> passExecValuesList, List<List<ExecValue>> failExecValuesList) {
		super(passExecValuesList, failExecValuesList);
		System.out.println("One ilp template");
		System.out.println(passExecValuesList);
		System.out.println(failExecValuesList);
	}
	
	@Override
	public boolean check() {
		Machine m = getLearningMachine();
	
		List<String> labels = new ArrayList<String>();
		for (ExecValue ev : passExecValuesList.get(0)) {
			labels.add(ev.getVarId());
		}
		
		m = m.setDataLabels(labels);
		
		for (List<ExecValue> evl : passExecValuesList) {
			int size = evl.size();
			
			double[] v = new double[size];
			for (int i = 0; i < size; i++) {
				v[i] = evl.get(i).getDoubleVal();
			}
			
			m.addDataPoint(Category.POSITIVE, v);
		}
		
		for (List<ExecValue> evl : failExecValuesList) {
			int size = evl.size();
			
			double[] v = new double[size];
			for (int i = 0; i < size; i++) {
				v[i] = evl.get(i).getDoubleVal();
			}
			
			m.addDataPoint(Category.NEGATIVE, v);
		}

		m = m.train();

		if (m.getModel() == null) {
			System.out.println("No model");
			return false;
		} else {
			Formula formula = m.getLearnedLogic(new StringDividerProcessor(), m.getDivider(), true);
			
			if (formula instanceof LIAAtom) {
				LIAAtom lia = (LIAAtom) formula;
				
				if (lia.getMVFOExpr().size() != 1) {
					return false;
				} else if (d != lia.getConstant() ||
						a != lia.getMVFOExpr().get(0).getCoefficient()) {
					d = lia.getConstant();
					a = lia.getMVFOExpr().get(0).getCoefficient();
					change = true;
					return true;
				} else {
					change = false;
					return true;
				}
			} else {
				return false;
			}
		}
	}
	
	@Override
	public boolean isChanged() {
		return change;
	}
 
	@Override
	public List<List<Eq<?>>> sampling() {
		List<List<Eq<?>>> samples = new ArrayList<List<Eq<?>>>();
		
		ExecValue ev = passExecValuesList.get(0).get(0);
		Var v = new ExecVar(ev.getVarId(), ev.getType());
		
		List<Eq<?>> sample1 = new ArrayList<Eq<?>>();
		sample1.add(new Eq<Number>(v, d / a));
		
		List<Eq<?>> sample2 = new ArrayList<Eq<?>>();
		sample2.add(new Eq<Number>(v, (d / a) - 1.0));
		
		List<Eq<?>> sample3 = new ArrayList<Eq<?>>();
		sample3.add(new Eq<Number>(v, (d / a) + 1.0));
		
		samples.add(sample1);
		samples.add(sample2);
		samples.add(sample3);
		
		return samples;
	}
	
	@Override
	public String toString() {
		return a + "*" + passExecValuesList.get(0).get(0).getVarId() + " >= " + d;
	}
	
}
