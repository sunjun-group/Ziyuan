package tzuyu.engine.bool;

import java.util.ArrayList;
import java.util.List;

import tzuyu.engine.model.Formula;
import tzuyu.engine.model.Prestate;


/**
 * Clause defined here is the same as the standard definition in the classic
 * logic, which is disjunction of a list of {@link Literal}s.
 * 
 * @author Spencer Xiao
 * 
 */
public class CNFClause implements Formula {
  private final List<Literal> literals;

  public CNFClause() {
    this.literals = new ArrayList<Literal>();
  }

  public void addLiteral(Literal literal) {
    int index = this.literals.indexOf(literal);
    if (index == -1) {
      this.literals.add(literal);
    }
  }

  @Override
  public List<Var> getReferencedVariables() {
    List<Var> result = new ArrayList<Var>();
    for (Literal lit : literals) {
      List<Var> vars = lit.getReferencedVariables();
      result.removeAll(vars);
      result.addAll(vars);
    }
    return result;
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    int size = literals.size();
    for (int index = 0; index < size; index++) {
      Literal literal = literals.get(index);
      sb.append(literal.toString());
      if (index < size - 1) {
        sb.append("||");
      }
    }
    return sb.toString();
  }

  @Override
  public boolean evaluate(Object[] objects) {
    boolean result = false;

    for (Literal literal : literals) {
      result |= literal.evaluate(objects);
      if (result == true) {
        return true;
      }
    }
    return false;
  }

  @Override
  public boolean equals(Object obj) {
    if (!(obj instanceof CNFClause)) {
      return false;
    }

    CNFClause clause = (CNFClause) obj;

    if (this.literals.size() != clause.literals.size()) {
      return false;
    }

    return this.literals.equals(clause);
  }

  @Override
  public Formula restrict(List<Atom> vars, List<Integer> vals) {
    CNFClause result = new CNFClause();
    for (Literal literal : literals) {
      Formula expr = literal.restrict(vars, vals);
      if (expr instanceof True) {
        return Formula.TRUE;
      } else if (!(expr instanceof False)) {
        result.addLiteral(literal);
      }
    }
    if (result.literals.size() == 0) {
      return Formula.FALSE;
    }
    return result;
  }

  @Override
  public List<Atom> getAtomics() {
    List<Atom> result = new ArrayList<Atom>();
    for (Literal lit : literals) {
      List<Atom> atoms = lit.getAtomics();
      result.removeAll(atoms);
      result.addAll(atoms);
    }
    return result;
  }

  @Override
  public Formula simplify() {
    CNFClause result = new CNFClause();
    for (Literal literal : literals) {
      Formula expr = literal.simplify();
      if (expr instanceof True) {
        return Formula.TRUE;
      } else if (!(expr instanceof False)) {
        result.addLiteral(literal);
      }
    }
    if (result.literals.size() == 0) {
      return Formula.FALSE;
    }
    return result;
  }

  @Override
  public boolean evaluate(Prestate state) {
    boolean result = false;

    for (Literal literal : literals) {
      result |= literal.evaluate(state);
      if (result == true) {
        return true;
      }
    }
    return false;
  }
}
