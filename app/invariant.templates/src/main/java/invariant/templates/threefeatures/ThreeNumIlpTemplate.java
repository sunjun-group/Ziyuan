package invariant.templates.threefeatures;

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

// Template ax + by + cz >= d

public class ThreeNumIlpTemplate extends ThreeFeaturesTemplate {

	private double a = 0.0;
	
	private double b = 0.0;
	
	private double c = 0.0;
	
	private double d = 0.0;
	
	public ThreeNumIlpTemplate(List<List<ExecValue>> passExecValuesList, List<List<ExecValue>> failExecValuesList) {
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
	
	public void setD(double dd) {
		d = dd;
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
				if (lia.getMVFOExpr().size() != 3) {
					return false;
				} else if (d != lia.getConstant() ||
						a != lia.getMVFOExpr().get(0).getCoefficient() ||
						b != lia.getMVFOExpr().get(1).getCoefficient() ||
						c != lia.getMVFOExpr().get(2).getCoefficient()) {
					d = lia.getConstant();
					a = lia.getMVFOExpr().get(0).getCoefficient();
					b = lia.getMVFOExpr().get(1).getCoefficient();
					c = lia.getMVFOExpr().get(2).getCoefficient();
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
		
		ExecValue ev3 = passValues.get(0).get(2);
		Var v3 = new ExecVar(ev3.getVarId(), ev3.getType());
		
		List<Eq<?>> sample1 = new ArrayList<Eq<?>>();
		sample1.add(new Eq<Number>(v1, (int) (d / a)));
		sample1.add(new Eq<Number>(v2, (int) 0.0));
		sample1.add(new Eq<Number>(v3, (int) 0.0));
		
		List<Eq<?>> sample2 = new ArrayList<Eq<?>>();
		sample2.add(new Eq<Number>(v1, (int) (d / a - 1.0)));
		sample2.add(new Eq<Number>(v2, (int) 0.0));
		sample2.add(new Eq<Number>(v3, (int) 0.0));
		
		List<Eq<?>> sample3 = new ArrayList<Eq<?>>();
		sample3.add(new Eq<Number>(v1, (int) (d / a + 1.0)));
		sample3.add(new Eq<Number>(v2, (int) 0.0));
		sample3.add(new Eq<Number>(v3, (int) 0.0));
		
		List<Eq<?>> sample4 = new ArrayList<Eq<?>>();
		sample4.add(new Eq<Number>(v1, (int) 0.0));
		sample4.add(new Eq<Number>(v2, (int) (d / b)));
		sample4.add(new Eq<Number>(v3, (int) 0.0));
		
		List<Eq<?>> sample5 = new ArrayList<Eq<?>>();
		sample5.add(new Eq<Number>(v1, (int) 0.0));
		sample5.add(new Eq<Number>(v2, (int) (d / b - 1.0)));
		sample5.add(new Eq<Number>(v3, (int) 0.0));
		
		List<Eq<?>> sample6 = new ArrayList<Eq<?>>();
		sample6.add(new Eq<Number>(v1, (int) 0.0));
		sample6.add(new Eq<Number>(v2, (int) (d / b + 1.0)));
		sample6.add(new Eq<Number>(v3, (int) 0.0));
		
		List<Eq<?>> sample7 = new ArrayList<Eq<?>>();
		sample7.add(new Eq<Number>(v1, (int) 0.0));
		sample7.add(new Eq<Number>(v2, (int) 0.0));
		sample7.add(new Eq<Number>(v3, (int) (d / c)));
		
		List<Eq<?>> sample8 = new ArrayList<Eq<?>>();
		sample8.add(new Eq<Number>(v1, (int) 0.0));
		sample8.add(new Eq<Number>(v2, (int) 0.0));
		sample8.add(new Eq<Number>(v3, (int) (d / c - 1.0)));
		
		List<Eq<?>> sample9 = new ArrayList<Eq<?>>();
		sample9.add(new Eq<Number>(v1, (int) 0.0));
		sample9.add(new Eq<Number>(v2, (int) 0.0));
		sample9.add(new Eq<Number>(v3, (int) (d / c + 1.0)));
		
		samples.add(sample1);
		samples.add(sample2);
		samples.add(sample3);
		samples.add(sample4);
		samples.add(sample5);
		samples.add(sample6);
		samples.add(sample7);
		samples.add(sample8);
		samples.add(sample9);
		
		return samples;
	}
	
	@Override
	public String toString() {
		return a + "*" + passValues.get(0).get(0).getVarId() + " + " +
				b + "*" + passValues.get(0).get(1).getVarId() + " + " +
				c + "*" + passValues.get(0).get(2).getVarId() + " >= " + d;
	}
	
}
