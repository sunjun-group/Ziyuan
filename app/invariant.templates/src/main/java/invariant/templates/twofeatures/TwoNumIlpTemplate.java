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
import sav.strategies.dto.execute.value.ExecVarType;

// Template ax + by >= c

public class TwoNumIlpTemplate extends TwoFeaturesTemplate {

	private double a = 0.0;
	
	private double b = 0.0;
	
	private double c = 0.0;
	
	public TwoNumIlpTemplate(List<List<ExecValue>> passExecValuesList, List<List<ExecValue>> failExecValuesList) {
		super(passExecValuesList, failExecValuesList);
	}
	
	public void setA(double aa) {
		a = aa;
	}
	
	public void setB(double bb) {
		b = bb;
	}
	
	public void setC(double cc) {
		c = cc;
	}
	
	@Override
	public boolean check() {
		Machine m = getSimpleMachine();
	
		List<String> labels = new ArrayList<String>();
		for (ExecValue ev : passValues.get(0)) {
			labels.add(ev.getVarId());
		}
		
		m = m.setDataLabels(labels);
		
		for (List<ExecValue> evl : passValues) {
			int size = evl.size();
			
			double[] v = new double[size];
			for (int i = 0; i < size; i++) {
				v[i] = evl.get(i).getDoubleVal();
			}
			
			m.addDataPoint(Category.POSITIVE, v);
		}
		
		for (List<ExecValue> evl : failValues) {
			int size = evl.size();
			
			double[] v = new double[size];
			for (int i = 0; i < size; i++) {
				v[i] = evl.get(i).getDoubleVal();
			}
			
			m.addDataPoint(Category.NEGATIVE, v);
		}

		m = m.train();

		if (m.getModel() == null) {
			return false;
		} else {
			Formula formula = m.getLearnedLogic(new StringDividerProcessor(), m.getDivider(), true);
			// System.out.println(formula);
			
			if (formula instanceof LIAAtom) {
				LIAAtom lia = (LIAAtom) formula;
				if (lia.getMVFOExpr().size() != 2) {
					return false;
				} else if (c != lia.getConstant() ||
						a != lia.getMVFOExpr().get(0).getCoefficient() ||
						b != lia.getMVFOExpr().get(1).getCoefficient()) {
					c = lia.getConstant();
					a = lia.getMVFOExpr().get(0).getCoefficient();
					b = lia.getMVFOExpr().get(1).getCoefficient();
					return true;
				} else {
					return true;
				}
			} else {
				return false;
			}
		}
	}
 
	@Override
	public List<List<Eq<?>>> sampling() {
		List<List<Eq<?>>> samples = new ArrayList<List<Eq<?>>>();
		
		ExecValue ev1 = passValues.get(0).get(0);
		Var v1 = new ExecVar(ev1.getVarId(), ev1.getType());
		
		ExecValue ev2 = passValues.get(0).get(1);
		Var v2 = new ExecVar(ev2.getVarId(), ev2.getType());
		
		List<Eq<?>> sample1 = new ArrayList<Eq<?>>();
		if (ev1.getType() == ExecVarType.INTEGER) {
			sample1.add(new Eq<Number>(v1, (int) (c / a)));
			sample1.add(new Eq<Number>(v2, 0));
		} else if (ev1.getType() == ExecVarType.LONG) {
			sample1.add(new Eq<Number>(v1, (long) (c / a)));
			sample1.add(new Eq<Number>(v2, 0L));
		} else {
			sample1.add(new Eq<Number>(v1, (c / a)));
			sample1.add(new Eq<Number>(v2, 0.0));
		}
		
		List<Eq<?>> sample2 = new ArrayList<Eq<?>>();
		if (ev1.getType() == ExecVarType.INTEGER) {
			sample2.add(new Eq<Number>(v1, (int) (c / a) - 1));
			sample2.add(new Eq<Number>(v2, 0));
		} else if (ev1.getType() == ExecVarType.LONG) {
			sample2.add(new Eq<Number>(v1, (long) (c / a) - 1));
			sample2.add(new Eq<Number>(v2, 0L));
		} else {
			sample2.add(new Eq<Number>(v1, (c / a) - 1));
			sample2.add(new Eq<Number>(v2, 0.0));
		}
		
		List<Eq<?>> sample3 = new ArrayList<Eq<?>>();
		if (ev1.getType() == ExecVarType.INTEGER) {
			sample3.add(new Eq<Number>(v1, (int) (c / a) + 1));
			sample3.add(new Eq<Number>(v2, 0));
		} else if (ev1.getType() == ExecVarType.LONG) {
			sample3.add(new Eq<Number>(v1, (long) (c / a) + 1));
			sample3.add(new Eq<Number>(v2, 0L));
		} else {
			sample3.add(new Eq<Number>(v1, (c / a) + 1));
			sample3.add(new Eq<Number>(v2, 0.0));
		}
		
		List<Eq<?>> sample4 = new ArrayList<Eq<?>>();
		if (ev1.getType() == ExecVarType.INTEGER) {
			sample4.add(new Eq<Number>(v1, 0));
			sample4.add(new Eq<Number>(v2, (int) (c / b)));
		} else if (ev1.getType() == ExecVarType.LONG) {
			sample4.add(new Eq<Number>(v1, 0L));
			sample4.add(new Eq<Number>(v2, (long) (c / b)));
		} else {
			sample4.add(new Eq<Number>(v1, 0.0));
			sample4.add(new Eq<Number>(v2, (c / b)));
		}
		
		List<Eq<?>> sample5 = new ArrayList<Eq<?>>();
		if (ev1.getType() == ExecVarType.INTEGER) {
			sample5.add(new Eq<Number>(v1, 0));
			sample5.add(new Eq<Number>(v2, (int) (c / b) - 1));
		} else if (ev1.getType() == ExecVarType.LONG) {
			sample5.add(new Eq<Number>(v1, 0L));
			sample5.add(new Eq<Number>(v2, (long) (c / b) - 1));
		} else {
			sample5.add(new Eq<Number>(v1, 0.0));
			sample5.add(new Eq<Number>(v2, (c / b) - 1));
		}
		
		List<Eq<?>> sample6 = new ArrayList<Eq<?>>();
		if (ev1.getType() == ExecVarType.INTEGER) {
			sample6.add(new Eq<Number>(v1, 0));
			sample6.add(new Eq<Number>(v2, (int) (c / b) + 1));
		} else if (ev1.getType() == ExecVarType.LONG) {
			sample6.add(new Eq<Number>(v1, 0L));
			sample6.add(new Eq<Number>(v2, (long) (c / b) + 1));
		} else {
			sample6.add(new Eq<Number>(v1, 0.0));
			sample6.add(new Eq<Number>(v2, (c / b) + 1));
		}
		
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
		return a + "*" + passValues.get(0).get(0).getVarId() + " + " +
				b + "*" + passValues.get(0).get(1).getVarId() + " >= " + c;
	}
	
}
