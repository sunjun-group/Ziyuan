package tzuyu.engine.bool;

import java.util.ArrayList;
import java.util.List;

import tzuyu.engine.iface.BoolVisitor;
import tzuyu.engine.model.Formula;
import tzuyu.engine.model.Prestate;


/**
 * CNF is defined here the same as in the classic logic books, which is a
 * sequence of {@link CNFClause}s.
 * 
 * @author Spencer Xiao
 * 
 */
public class CNF implements Formula {
  private final List<CNFClause> clauses;

  public CNF() {
    this.clauses = new ArrayList<CNFClause>();
  }

  public void addClause(CNFClause clause) {
    int index = this.clauses.indexOf(clause);
    if (index == -1) {
      this.clauses.add(clause);
    }
  }

  public List<Var> getReferencedVariables() {
    List<Var> result = new ArrayList<Var>();
    for (CNFClause clause : clauses) {
      List<Var> vars = clause.getReferencedVariables();
      result.removeAll(vars);
      result.addAll(vars);
    }
    return result;
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    int size = clauses.size();
    for (int index = 0; index < size; index++) {
      CNFClause clause = clauses.get(index);
      sb.append("(");
      sb.append(clause.toString());
      sb.append(")");
      if (index < size - 1) {
        sb.append("&&");
      }
    }
    return sb.toString();
  }

  public boolean evaluate(Object[] objects) {
    boolean result = true;

    for (CNFClause clause : clauses) {
      result &= clause.evaluate(objects);
      if (result == false) {
        return false;
      }
    }
    return true;
  }

  public Formula restrict(List<Atom> vars, List<Integer> vals) {
    CNF result = new CNF();
    for (CNFClause clause : clauses) {
      Formula expr = clause.restrict(vars, vals);
      if (expr instanceof False) {
        return Formula.FALSE;
      } else if (!(expr instanceof True)) {
        result.addClause((CNFClause) expr);
      }
    }

    if (result.clauses.size() == 0) {
      return Formula.TRUE;
    }
    return result;
  }

  public List<Atom> getAtomics() {
    List<Atom> result = new ArrayList<Atom>();
    for (CNFClause clause : clauses) {
      List<Atom> atoms = clause.getAtomics();
      result.removeAll(atoms);
      result.addAll(atoms);
    }
    return result;
  }

  public Formula simplify() {
    CNF result = new CNF();
    for (CNFClause clause : clauses) {
      Formula expr = clause.simplify();
      if (expr instanceof False) {
        return Formula.FALSE;
      } else if (!(expr instanceof True)) {
        result.addClause((CNFClause) expr);
      }
    }

    if (result.clauses.size() == 0) {
      return Formula.TRUE;
    }
    return result;
  }

  public boolean evaluate(Prestate state) {
    boolean result = true;

    for (CNFClause clause : clauses) {
      result &= clause.evaluate(state);
      if (result == false) {
        return false;
      }
    }
    return true;
  }

	@Override
	public void accept(BoolVisitor visitor) {
		visitor.visit(this);
	}
	
	public List<CNFClause> getChildren() {
		return clauses;
	}
}
