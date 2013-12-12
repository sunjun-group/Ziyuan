package tzuyu.engine.bool;

import java.util.ArrayList;
import java.util.List;

import tzuyu.engine.model.Formula;


/**
 * The abstract atomic expression used in this boolean library. The subclasses
 * of this class can represents different atomic expressions in its related
 * theories(e.g., the Linear Integer Arithmetic LIA for short) and must override
 * the unimplemented abstract methods.
 * 
 * @author Spencer Xiao
 * 
 */
public abstract class Atom implements Formula {

  @Override
  public Formula restrict(List<Atom> vars, List<Integer> vals) {
    for (int index = 0; index < vars.size(); index++) {
      Atom atom = vars.get(index);
      if (this.equals(atom)) {
        if (vals.get(index) == 0) {
          return Formula.FALSE;
        } else {
          return Formula.TRUE;
        }
      }
    }

    return this;
  }

  @Override
  public List<Atom> getAtomics() {
    List<Atom> atoms = new ArrayList<Atom>();
    atoms.add(this);
    return atoms;
  }

  @Override
  public Formula simplify() {
    return this;
  }
}
