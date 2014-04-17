package tzuyu.engine.model;

import java.util.List;

import tzuyu.engine.bool.Atom;
import tzuyu.engine.bool.False;
import tzuyu.engine.bool.True;
import tzuyu.engine.bool.Var;
import tzuyu.engine.iface.BoolVisitor;



public interface Formula {

  public static final Formula TRUE = True.getInstance();
  public static final Formula FALSE = False.getInstance();

  public List<Var> getReferencedVariables();

  public boolean evaluate(Object[] objects);

  public boolean evaluate(Prestate state);

  public Formula restrict(List<Atom> vars, List<Integer> vals);

  public List<Atom> getAtomics();

  public Formula simplify();

  public void accept(BoolVisitor visitor);
}
