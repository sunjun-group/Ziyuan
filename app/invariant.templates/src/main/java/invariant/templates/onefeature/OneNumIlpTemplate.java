package invariant.templates.onefeature;

import java.util.ArrayList;
import java.util.List;

import japa.parser.ast.expr.BinaryExpr;
import japa.parser.ast.expr.DoubleLiteralExpr;
import japa.parser.ast.expr.Expression;
import japa.parser.ast.expr.NameExpr;
import japa.parser.ast.stmt.AssertStmt;
import libsvm.core.Category;
import libsvm.core.Machine;
import libsvm.core.StringDividerProcessor;
import sav.common.core.formula.Eq;
import sav.common.core.formula.Formula;
import sav.common.core.formula.LIAAtom;
import sav.common.core.formula.Var;
import sav.settings.SAVExecutionTimeOutException;
import sav.strategies.dto.execute.value.ExecValue;
import sav.strategies.dto.execute.value.ExecVar;
import sav.strategies.dto.execute.value.ExecVarType;

// Template ax >= b

public class OneNumIlpTemplate extends OneFeatureTemplate {

	private double a = 0.0;
	
	private double b = 0.0;
	
	public OneNumIlpTemplate(List<List<ExecValue>> passExecValuesList, List<List<ExecValue>> failExecValuesList) {
		super(passExecValuesList, failExecValuesList);
	}
	
	public void setA(double aa) {
		a = aa;
	}
	
	public void setB(double bb) {
		b = bb;
	}
	
	@Override
	public boolean check() throws SAVExecutionTimeOutException {
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
				
				if (lia.getMVFOExpr().size() != 1) {
					return false;
				} else
//					if (b != lia.getConstant() ||
//						a != lia.getMVFOExpr().get(0).getCoefficient()) 
				{
					b = lia.getConstant();
					a = lia.getMVFOExpr().get(0).getCoefficient();
					
					if (Double.isNaN(a) || Double.isNaN(b)) return false;
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
		
		ExecValue ev = passValues.get(0).get(0);
		
		String id = ev.getVarId();
		ExecVarType t = ev.getType();
		
		Var v = new ExecVar(id, t);
		
		if (t == ExecVarType.INTEGER || t == ExecVarType.LONG ||
				t == ExecVarType.BYTE || t == ExecVarType.SHORT) {
			samples.add(sampling(v, t, b / a));
			samples.add(sampling(v, t, b / a - 1.0));
			samples.add(sampling(v, t, b / a + 1.0));
		} else if (t == ExecVarType.FLOAT || t == ExecVarType.DOUBLE) {
			samples.add(sampling(v, t, b / a));
			samples.add(sampling(v, t, b / a - offset));
			samples.add(sampling(v, t, b / a + offset));
		}
		
		return samples;
	}
	
	@Override
	public AssertStmt convertToAssertStmt() {
		Expression rhs = getRhs();
		Expression lhs = getLhs();
		
		Expression e = new BinaryExpr(lhs, rhs, BinaryExpr.Operator.greaterEquals);
		return new AssertStmt(e);
	}
	
	private Expression getLhs() {
		Expression e1 = new DoubleLiteralExpr(String.valueOf(a));
		Expression e2 = new NameExpr(passValues.get(0).get(0).getVarId());
		
		Expression e = new BinaryExpr(e1, e2, BinaryExpr.Operator.times);
		return e;
	}
	
	private Expression getRhs() {
		return new DoubleLiteralExpr(String.valueOf(b));
	}
	
	@Override
	public String toString() {
		if (a > 0) return passValues.get(0).get(0).getVarId() + " >= " + round(b / a);
		else return passValues.get(0).get(0).getVarId() + " <= " + round(b / a);
	}
	
}
