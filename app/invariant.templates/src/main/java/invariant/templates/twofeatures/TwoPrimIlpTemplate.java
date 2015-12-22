package invariant.templates.twofeatures;

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

public class TwoPrimIlpTemplate extends TwoFeaturesTemplate {

	private double a = 0.0;
	
	private double b = 0.0;
	
	private double d = 0.0;
	
	private boolean change = false;
	// ax + by + d >= 0
	
	public TwoPrimIlpTemplate(List<List<ExecValue>> passExecValuesList, List<List<ExecValue>> failExecValuesList) {
		super(passExecValuesList, failExecValuesList);
		System.out.println("Two ilp template");
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
			// System.out.println(formula);
			
			if (formula instanceof LIAAtom) {
				LIAAtom lia = (LIAAtom) formula;
				if (lia.getMVFOExpr().size() != 2) {
					return false;
				} else if (d != lia.getConstant() ||
						a != lia.getMVFOExpr().get(0).getCoefficient() ||
						b != lia.getMVFOExpr().get(1).getCoefficient()) {
					d = lia.getConstant();
					a = lia.getMVFOExpr().get(0).getCoefficient();
					b = lia.getMVFOExpr().get(1).getCoefficient();
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
		
		ExecValue ev1 = passExecValuesList.get(0).get(0);
		Var v1 = new ExecVar(ev1.getVarId(), ev1.getType());
		
		ExecValue ev2 = passExecValuesList.get(0).get(1);
		Var v2 = new ExecVar(ev2.getVarId(), ev2.getType());
		
		List<Eq<?>> sample1 = new ArrayList<Eq<?>>();
		sample1.add(new Eq<Number>(v1, d / a));
		sample1.add(new Eq<Number>(v2, 0.0));
		
		List<Eq<?>> sample2 = new ArrayList<Eq<?>>();
		sample2.add(new Eq<Number>(v1, (d / a) - 1.0));
		sample2.add(new Eq<Number>(v2, 0.0));
		
		List<Eq<?>> sample3 = new ArrayList<Eq<?>>();
		sample3.add(new Eq<Number>(v1, (d / a) + 1.0));
		sample3.add(new Eq<Number>(v2, 0.0));
		
		List<Eq<?>> sample4 = new ArrayList<Eq<?>>();
		sample4.add(new Eq<Number>(v1, 0.0));
		sample4.add(new Eq<Number>(v2, d / b));
		
		List<Eq<?>> sample5 = new ArrayList<Eq<?>>();
		sample5.add(new Eq<Number>(v1, 0.0));
		sample5.add(new Eq<Number>(v2, (d / b) - 1.0));
		
		List<Eq<?>> sample6 = new ArrayList<Eq<?>>();
		sample6.add(new Eq<Number>(v1, 0.0));
		sample6.add(new Eq<Number>(v2, (d / b) + 1.0));
		
		samples.add(sample1);
		samples.add(sample2);
		samples.add(sample3);
		samples.add(sample4);
		samples.add(sample5);
		samples.add(sample6);
		
		return samples;
	}
	
	@Override
	public String toString() {
		return a + "*" + passExecValuesList.get(0).get(0).getVarId() + " + " +
				b + "*" + passExecValuesList.get(0).get(1).getVarId() + " >= " + d;
	}
	
}
