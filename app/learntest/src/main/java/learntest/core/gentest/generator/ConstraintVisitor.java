package learntest.core.gentest.generator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sf.javailp.Constraint;
import net.sf.javailp.Linear;
import net.sf.javailp.OptType;
import net.sf.javailp.Problem;
import sav.common.core.formula.AndFormula;
import sav.common.core.formula.Atom;
import sav.common.core.formula.Formula;
import sav.common.core.formula.LIAAtom;
import sav.common.core.formula.LIATerm;
import sav.common.core.formula.NotFormula;
import sav.common.core.formula.Operator;
import sav.common.core.formula.utils.ExpressionVisitor;
import sav.strategies.dto.execute.value.ExecVar;
import sav.strategies.dto.execute.value.ExecVarType;

public class ConstraintVisitor extends ExpressionVisitor {
	
	private List<Constraint> constraints;
	private List<List<Constraint>> choices;
	private Set<String> vars;
	private Map<String, ExecVarType> typeMap;
	
	private static final OptType type = OptType.MIN;
	
	private List<Integer> indeices;
	private boolean hasNext;
	
	public ConstraintVisitor() {
		constraints = new ArrayList<Constraint>();
		choices = new ArrayList<List<Constraint>>();
		vars = new HashSet<String>();
		typeMap = new HashMap<String, ExecVarType>();
		indeices = new ArrayList<Integer>();
		hasNext = true;
	}

	@Override
	public void visit(NotFormula not) {
		List<Constraint> choice = new ArrayList<Constraint>();
		List<Atom> atomics = not.getAtomics();
		for (Atom atom : atomics) {
			if (atom instanceof LIAAtom) {
				handleLIAAtom((LIAAtom) atom, true, choice);
			}
		}
		if (choice.size() == 1) {
			constraints.add(choice.get(0));
		} else if (choice.size() > 1) {
			choices.add(choice);
			indeices.add(0);
		}
	}

	@Override
	public void visit(LIAAtom atom) {
		handleLIAAtom(atom, false, constraints);
	}

	@Override
	public void visit(AndFormula and) {
		List<Formula> elements = and.getElements();
		for (Formula element : elements) {
			if (element instanceof AndFormula) {
				visit((AndFormula)element);
			} else if (element instanceof NotFormula) {
				visit((NotFormula)element);
			} else if (element instanceof LIAAtom) {
				visit((LIAAtom)element);
			}
		}
	}
	
	private void handleLIAAtom(LIAAtom atom, boolean not, List<Constraint> res) {
		List<LIATerm> exp = atom.getMVFOExpr();
		Operator operator = atom.getOperator();
		double constant = atom.getConstant();

		Linear constraint = new Linear();
		for (LIATerm term : exp) {
			ExecVar variable = term.getVariable();
			String label = variable.getLabel();
			vars.add(label);
			typeMap.put(label, variable.getType());
			constraint.add(term.getCoefficient(), label);
		}
		
		if (not) {
			operator = Operator.notOf(operator);
		}
		
		if (operator == Operator.GT) {
			res.add(new Constraint(constraint, Operator.GE.getCode(), constant + 1));
		} else if (operator == Operator.LT) {
			res.add(new Constraint(constraint, Operator.LE.getCode(), constant - 1));
		} else if (operator == Operator.NE) {
			res.add(new Constraint(constraint, Operator.GE.getCode(), constant + 1));
			res.add(new Constraint(constraint, Operator.LE.getCode(), constant - 1));
		} else {
			res.add(new Constraint(constraint, operator.getCode(), constant));
		}
	}
	
	public boolean hasNextProblem() {
		if (indeices.isEmpty() && hasNext) {
			hasNext = false;
			return true;
		}
		return hasNext;
	}
	
	public Problem getProblem() {
		Problem problem = new Problem();
		for (Constraint constraint : constraints) {
			problem.add(constraint);
		}
		int index = 0;
		for (int i : indeices) {
			problem.add(choices.get(index).get(i));
			index ++;
		}
		Linear obj = new Linear();
		for (String var : vars) {
			obj.add(1, var);
			setTypeAndBound(problem, var);
		}
		problem.setObjective(obj, type);
		calculateNext();
		return problem;
	}

	private void setTypeAndBound(Problem problem, String var) {
		ExecVarType varType = typeMap.get(var);
		switch (varType) {
		case BOOLEAN:
			problem.setVarType(var, Integer.class);
			problem.setVarLowerBound(var, 0);
			problem.setVarUpperBound(var, 1);
			break;
		case BYTE:
			problem.setVarType(var, Byte.class);
			problem.setVarLowerBound(var, -100);
			problem.setVarUpperBound(var, 100);
			break;
		case CHAR:
			problem.setVarType(var, Character.class);
			problem.setVarLowerBound(var, 100);
			problem.setVarUpperBound(var, 100);
			break;
		case DOUBLE:
			problem.setVarType(var, Double.class);
			problem.setVarLowerBound(var, -2000d);
			problem.setVarUpperBound(var, 2000d);
			break;
		case FLOAT:
			problem.setVarType(var, Float.class);
			problem.setVarLowerBound(var, -1000f);
			problem.setVarUpperBound(var, 1000f);
			break;
		case LONG:
			problem.setVarType(var, Long.class);
			problem.setVarLowerBound(var, -1000l);
			problem.setVarUpperBound(var, 1000l);
			break;
		case SHORT:
			problem.setVarType(var, Short.class);
			problem.setVarLowerBound(var, -100);
			problem.setVarUpperBound(var, 100);
			break;
		default:
			problem.setVarType(var, Integer.class);
			problem.setVarLowerBound(var, -200);
			problem.setVarUpperBound(var, 200);
			break;
		}
	}

	private void calculateNext() {
		int index = 0;
		for (int i : indeices) {
			if (i < choices.get(index).size() - 1) {
				break;
			}
			index ++;
		}
		if (index < choices.size()) {
			for (int i = 0; i < index; i++) {
				indeices.set(i, 0);
			}
			indeices.set(index, indeices.get(index) + 1);
		} else {
			hasNext = false;
		}
	}

	public Set<String> getVars() {
		return vars;
	}

}
