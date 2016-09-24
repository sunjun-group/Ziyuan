package invariant.templates.twofeatures;

import java.util.ArrayList;
import java.util.Arrays;
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
			ExecVarType evt = passValues.get(0).get(0).getType();
			Formula formula = null;
			
			if (evt == ExecVarType.INTEGER || evt == ExecVarType.LONG ||
					evt == ExecVarType.BYTE || evt == ExecVarType.SHORT)
				formula = m.getLearnedLogic(new StringDividerProcessor(), m.getDivider(), true);
			else
//				formula = m.getLearnedLogic(new StringDividerProcessor(), m.getDivider(), true, roundNum);
				formula = m.getLearnedLogic(new StringDividerProcessor(), m.getDivider(), false);
			
			if (formula instanceof LIAAtom) {
				LIAAtom lia = (LIAAtom) formula;
				if (lia.getMVFOExpr().size() != 2) {
					return false;
				} else
//					if (c != lia.getConstant() ||
//						a != lia.getMVFOExpr().get(0).getCoefficient() ||
//						b != lia.getMVFOExpr().get(1).getCoefficient())
				{
					c = lia.getConstant();
					a = lia.getMVFOExpr().get(0).getCoefficient();
					b = lia.getMVFOExpr().get(1).getCoefficient();
					
					if (Double.isNaN(a) || Double.isNaN(b) || Double.isNaN(c)) return false;
					return true;
				}
//				else {
//					return true;
//				}
			} else {
				return false;
			}
		}
	}
	
	@Override
	public List<List<Eq<?>>> sampling() {
		List<List<Eq<?>>> samples = new ArrayList<List<Eq<?>>>();
		
		ExecValue ev1 = passValues.get(0).get(0);
		ExecValue ev2 = passValues.get(0).get(1);
		
		String id1 = ev1.getVarId();
		String id2 = ev2.getVarId();
		
		ExecVarType t1 = ev1.getType();
		ExecVarType t2 = ev2.getType();
		
		Var v1 = new ExecVar(id1, t1);
		Var v2 = new ExecVar(id2, t2);
		
		List<Var> vs = Arrays.asList(v1, v2);
		List<ExecVarType> ts = Arrays.asList(t1, t2);
		
		double px = passValues.get(0).get(0).getDoubleVal();
		double py = passValues.get(0).get(1).getDoubleVal();
		
		double npx = (c - b * py) / a;
		double npy = (c - a * px) / b;
		
		double fx = failValues.get(0).get(0).getDoubleVal();
		double fy = failValues.get(0).get(0).getDoubleVal();
		
		double nfx = (c - b * fy) / a;
		double nfy = (c - a * fx) / b;
		
		if (t1 == ExecVarType.INTEGER || t1 == ExecVarType.LONG ||
				t1 == ExecVarType.BYTE || t1 == ExecVarType.SHORT) {
			samples.add(sampling(vs, ts, Arrays.asList(px, npy)));
			samples.add(sampling(vs, ts, Arrays.asList(px - 1.0, npy)));
			samples.add(sampling(vs, ts, Arrays.asList(px + 1.0, npy)));
		} else if (t1 == ExecVarType.FLOAT || t1 == ExecVarType.DOUBLE) {
			samples.add(sampling(vs, ts, Arrays.asList(px, npy)));
			samples.add(sampling(vs, ts, Arrays.asList(px - offset, npy)));
			samples.add(sampling(vs, ts, Arrays.asList(px + offset, npy)));
		}
		
		if (t2 == ExecVarType.INTEGER || t2 == ExecVarType.LONG ||
				t2 == ExecVarType.BYTE || t2 == ExecVarType.SHORT) {
			samples.add(sampling(vs, ts, Arrays.asList(npx, py)));
			samples.add(sampling(vs, ts, Arrays.asList(npx, py - 1.0)));
			samples.add(sampling(vs, ts, Arrays.asList(npx, py + 1.0)));
		} else if (t2 == ExecVarType.FLOAT || t2 == ExecVarType.DOUBLE) {
			samples.add(sampling(vs, ts, Arrays.asList(npx, py)));
			samples.add(sampling(vs, ts, Arrays.asList(npx, py - offset)));
			samples.add(sampling(vs, ts, Arrays.asList(npx, py + offset)));
		}
		
		if (t1 == ExecVarType.INTEGER || t1 == ExecVarType.LONG ||
				t1 == ExecVarType.BYTE || t1 == ExecVarType.SHORT) {
			samples.add(sampling(vs, ts, Arrays.asList(fx, nfy)));
			samples.add(sampling(vs, ts, Arrays.asList(fx - 1.0, nfy)));
			samples.add(sampling(vs, ts, Arrays.asList(fx + 1.0, nfy)));
		} else if (t1 == ExecVarType.FLOAT || t1 == ExecVarType.DOUBLE) {
			samples.add(sampling(vs, ts, Arrays.asList(fx, nfy)));
			samples.add(sampling(vs, ts, Arrays.asList(fx - offset, nfy)));
			samples.add(sampling(vs, ts, Arrays.asList(fx + offset, nfy)));
		}
		
		if (t2 == ExecVarType.INTEGER || t2 == ExecVarType.LONG ||
				t2 == ExecVarType.BYTE || t2 == ExecVarType.SHORT) {
			samples.add(sampling(vs, ts, Arrays.asList(nfx, fy)));
			samples.add(sampling(vs, ts, Arrays.asList(nfx, fy - 1.0)));
			samples.add(sampling(vs, ts, Arrays.asList(nfx, fy + 1.0)));
		} else if (t2 == ExecVarType.FLOAT || t2 == ExecVarType.DOUBLE) {
			samples.add(sampling(vs, ts, Arrays.asList(nfx, fy)));
			samples.add(sampling(vs, ts, Arrays.asList(nfx, fy - offset)));
			samples.add(sampling(vs, ts, Arrays.asList(nfx, fy + offset)));
		}
		
//		if (t1 == ExecVarType.INTEGER || t1 == ExecVarType.LONG) {
//			samples.add(sampling(vs, ts, Arrays.asList(c / a, 0.0)));
//			samples.add(sampling(vs, ts, Arrays.asList(c / a - 1.0, 0.0)));
//			samples.add(sampling(vs, ts, Arrays.asList(c / a + 1.0, 0.0)));
//		} else if (t1 == ExecVarType.FLOAT || t1 == ExecVarType.DOUBLE) {
//			samples.add(sampling(vs, ts, Arrays.asList(c / a, 0.0)));
//			samples.add(sampling(vs, ts, Arrays.asList(c / a - offset, 0.0)));
//			samples.add(sampling(vs, ts, Arrays.asList(c / a + offset, 0.0)));
//		}
//		
//		if (t2 == ExecVarType.INTEGER || t2 == ExecVarType.LONG) {
//			samples.add(sampling(vs, ts, Arrays.asList(0.0, c / b)));
//			samples.add(sampling(vs, ts, Arrays.asList(0.0, c / b - 1.0)));
//			samples.add(sampling(vs, ts, Arrays.asList(0.0, c / b + 1.0)));
//		} else if (t2 == ExecVarType.FLOAT || t2 == ExecVarType.DOUBLE) {
//			samples.add(sampling(vs, ts, Arrays.asList(0.0, c / b)));
//			samples.add(sampling(vs, ts, Arrays.asList(0.0, c / b - offset)));
//			samples.add(sampling(vs, ts, Arrays.asList(0.0, c / b + offset)));
//		}
		
		return samples;
	}
	
	@Override
	public String toString() {
		return a + "*" + passValues.get(0).get(0).getVarId() + " + " +
				b + "*" + passValues.get(0).get(1).getVarId() + " >= " + c;
	}
	
}
