package tzuyu.engine.bool;

import java.util.ArrayList;
import java.util.List;

import tzuyu.engine.iface.BoolVisitor;
import tzuyu.engine.model.Formula;
import tzuyu.engine.model.Prestate;

/**
 * DNF stands for Disjunctive Normal Form, which is a disjunction of a list of
 * {@link DNFTerm}s.
 * 
 * @author Spencer Xiao
 * 
 */
public final class DNF implements Formula {
	private final List<DNFTerm> terms;

	public DNF() {
		this.terms = new ArrayList<DNFTerm>();
	}

	public void addTerm(DNFTerm term) {
		int index = this.terms.indexOf(term);
		if (index == -1) {
			this.terms.add(term);
		}
	}

	public List<Var> getReferencedVariables() {
		List<Var> result = new ArrayList<Var>();
		for (DNFTerm term : terms) {
			List<Var> vars = term.getReferencedVariables();
			result.removeAll(vars);
			result.addAll(vars);
		}
		return result;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		int size = terms.size();
		for (int index = 0; index < size; index++) {
			DNFTerm term = terms.get(index);
			sb.append("(");
			sb.append(term.toString());
			sb.append(")");
			if (index < size - 1) {
				sb.append("||");
			}
		}
		return sb.toString();
	}

	public boolean evaluate(Object[] objects) {
		boolean result = false;

		for (DNFTerm term : terms) {
			result |= term.evaluate(objects);
			if (result == true) {
				return true;
			}
		}
		return false;
	}

	public Formula restrict(List<Atom> vars, List<Integer> vals) {
		DNF result = new DNF();
		for (DNFTerm term : terms) {
			Formula expr = term.restrict(vars, vals);
			if (expr instanceof True) {
				return Formula.TRUE;
			} else if (!(expr instanceof False)) {
				result.addTerm((DNFTerm) expr);
			}
		}

		if (result.terms.size() == 0) {
			return Formula.FALSE;
		}
		return result;
	}

	public List<Atom> getAtomics() {
		List<Atom> result = new ArrayList<Atom>();
		for (DNFTerm term : terms) {
			List<Atom> atoms = term.getAtomics();
			result.removeAll(atoms);
			result.addAll(atoms);
		}
		return result;
	}

	public Formula simplify() {

		DNF result = new DNF();
		for (DNFTerm term : terms) {
			Formula expr = term.simplify();
			if (expr instanceof True) {
				return Formula.TRUE;
			} else if (!(expr instanceof False)) {
				result.addTerm((DNFTerm) expr);
			}
		}

		if (result.terms.size() == 0) {
			return Formula.FALSE;
		}
		return result;
	}

	public boolean evaluate(Prestate state) {
		boolean result = false;

		for (DNFTerm term : terms) {
			result |= term.evaluate(state);
			if (result == true) {
				return true;
			}
		}
		return false;
	}

	@Override
	public void accept(BoolVisitor visitor) {
		visitor.visit(this);
	}
	
	public List<DNFTerm> getChildren() {
		return terms;
	}
}
