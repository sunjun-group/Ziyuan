package tzuyu.engine.bool;

import java.util.ArrayList;
import java.util.List;

import tzuyu.engine.model.Formula;
import tzuyu.engine.model.Prestate;


/**
 * Literal is defined as the standard definition of literal in the classic
 * logic, which is either a {@link Atom} or the negation of a {@link Atom}.
 * 
 * @author Spencer Xiao
 * 
 */
public final class Literal implements Formula {
  private final Atom atom;
  private final boolean negation;

  public Literal(Atom atom, boolean isNegation) {
    this.atom = atom;
    this.negation = isNegation;
  }

  @Override
  public List<Var> getReferencedVariables() {
    return atom.getReferencedVariables();
  }

  @Override
  public String toString() {
    return (negation ? "!" : "") + atom.toString();
  }

  @Override
  public boolean evaluate(Object[] objects) {
    if (negation) {
      return !atom.evaluate(objects);
    } else {
      return atom.evaluate(objects);
    }
  }

  @Override
  public boolean equals(Object obj) {
    if (!(obj instanceof Literal)) {
      return false;
    }
    
    Literal lit = (Literal) obj;

    return this.atom.equals(lit) && this.negation == lit.negation;
  }

  @Override
  public int hashCode() {
    int result = 17;

    int c = negation ? 0 : 1;
    result = 37 * result + c;

    result = 37 * result + atom.hashCode();
    return result;
  }

  @Override
  public Formula restrict(List<Atom> vars, List<Integer> vals) {
    Formula expr = this.atom.restrict(vars, vals);
    if (expr instanceof True) {
      if (negation) {
        return Formula.FALSE;
      } else {
        return Formula.TRUE;
      }
    } else if (expr instanceof False) {
      if (negation) {
        return Formula.TRUE;
      } else {
        return Formula.FALSE;
      }
    }
    // This should never happen
    return this;
  }

  @Override
  public List<Atom> getAtomics() {
    List<Atom> atoms = new ArrayList<Atom>();
    atoms.add(atom);
    return atoms;
  }

  @Override
  public Formula simplify() {
    Formula expr = this.atom.simplify();
    if (expr instanceof True) {
      if (negation) {
        return Formula.FALSE;
      } else {
        return Formula.TRUE;
      }
    } else if (expr instanceof False) {
      if (negation) {
        return Formula.TRUE;
      } else {
        return Formula.FALSE;
      }
    }
    return this;
  }

  @Override
  public boolean evaluate(Prestate state) {
    if (negation) {
      return !atom.evaluate(state);
    } else {
      return atom.evaluate(state);
    }
  }
}
