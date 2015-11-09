package invariant.templates.onefeature;

import java.util.ArrayList;
import java.util.List;

import japa.parser.ast.expr.BinaryExpr;
import japa.parser.ast.expr.DoubleLiteralExpr;
import japa.parser.ast.expr.Expression;
import japa.parser.ast.expr.IntegerLiteralExpr;
import japa.parser.ast.expr.NameExpr;
import japa.parser.ast.stmt.AssertStmt;
import libsvm.core.Category;
import libsvm.core.Machine;
import libsvm.core.StringDividerProcessor;
import sav.common.core.formula.Eq;
import sav.common.core.formula.Formula;
import sav.common.core.formula.LIAAtom;
import sav.common.core.formula.Var;
import sav.strategies.dto.execute.value.ExecValue;
import sav.strategies.dto.execute.value.ExecVar;

// Template ax >= b

public class OnePrimIlpTemplate extends OneFeatureTemplate {

	private double a = 0.0;
	
	private double b = 0.0;
	
	public OnePrimIlpTemplate(List<List<ExecValue>> passExecValuesList, List<List<ExecValue>> failExecValuesList) {
		super(passExecValuesList, failExecValuesList);
	}
	
	public void setA(double aa) {
		a = aa;
	}
	
	public void setB(double bb) {
		b = bb;
	}
	
	@Override
	public boolean isSatisfiedAllPassValues() {
		return true;
	}
	
	@Override
	public boolean check() {
		Machine m = getSimpleMachine();
	
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
			return false;
		} else {
			Formula formula = m.getLearnedLogic(new StringDividerProcessor(), m.getDivider(), true);
			
			if (formula instanceof LIAAtom) {
				LIAAtom lia = (LIAAtom) formula;
				
				if (lia.getMVFOExpr().size() != 1) {
					return false;
				} else if (b != lia.getConstant() ||
						a != lia.getMVFOExpr().get(0).getCoefficient()) {
					b = lia.getConstant();
					a = lia.getMVFOExpr().get(0).getCoefficient();
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
		
		ExecValue ev = passExecValuesList.get(0).get(0);
		Var v = new ExecVar(ev.getVarId(), ev.getType());
		
		List<Eq<?>> sample1 = new ArrayList<Eq<?>>();
		sample1.add(new Eq<Number>(v, (int) (b / a)));
		
		List<Eq<?>> sample2 = new ArrayList<Eq<?>>();
		sample2.add(new Eq<Number>(v, (int) (b / a - 1.0)));
		
		List<Eq<?>> sample3 = new ArrayList<Eq<?>>();
		sample3.add(new Eq<Number>(v, (int) (b / a + 1.0)));
		
		samples.add(sample1);
		samples.add(sample2);
		samples.add(sample3);
		
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
		Expression e2 = new NameExpr(passExecValuesList.get(0).get(0).getVarId());
		
		Expression e = new BinaryExpr(e1, e2, BinaryExpr.Operator.times);
		return e;
	}
	
	private Expression getRhs() {
		return new DoubleLiteralExpr(String.valueOf(b));
	}
	
	@Override
	public String toString() {
		return a + "*" + passExecValuesList.get(0).get(0).getVarId() + " >= " + b;
	}
	
}
