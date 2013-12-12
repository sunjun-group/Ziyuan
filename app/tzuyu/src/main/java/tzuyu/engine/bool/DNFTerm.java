package tzuyu.engine.bool;

import java.util.ArrayList;
import java.util.List;

import tzuyu.engine.model.Formula;
import tzuyu.engine.model.Prestate;


/**
 * Term is a conjunction of a sequence of {@link Literal}s.
 * 
 * @author Spencer Xiao
 * 
 */
public class DNFTerm implements Formula {
  public final List<Literal> literals;

  public DNFTerm() {
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
        sb.append("&&");
      }
    }
    return sb.toString();
  }

  @Override
  public boolean evaluate(Object[] objects) {
    boolean result = true;

    for (Literal literal : literals) {
      result &= literal.evaluate(objects);
      if (result == false) {
        return false;
      }
    }
    return true;
  }

  @Override
  public boolean equals(Object obj) {
    if (!(obj instanceof DNFTerm)) {
      return false;
    }

    DNFTerm term = (DNFTerm) obj;
    if (term.literals.size() != literals.size()) {
      return false;
    }

    return literals.equals(term.literals);
  }

  @Override
  public Formula restrict(List<Atom> vars, List<Integer> vals) {
    DNFTerm result = new DNFTerm();
    for (Literal lit : literals) {
      Formula expr = lit.restrict(vars, vals);
      if (expr instanceof False) {
        return Formula.FALSE;
      } else if (!(expr instanceof True)) {
        result.addLiteral(lit);
      }
    }

    if (result.literals.size() == 0) {
      return Formula.TRUE;
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
    DNFTerm result = new DNFTerm();
    for (Literal lit : literals) {
      Formula expr = lit.simplify();
      if (expr instanceof False) {
        return Formula.FALSE;
      } else if (!(expr instanceof True)) {
        result.addLiteral(lit);
      }
    }

    if (result.literals.size() == 0) {
      return Formula.TRUE;
    }

    return result;
  }

  @Override
  public boolean evaluate(Prestate state) {
    boolean result = true;

    for (Literal literal : literals) {
      result &= literal.evaluate(state);
      if (result == false) {
        return false;
      }
    }
    return true;
  }
}
