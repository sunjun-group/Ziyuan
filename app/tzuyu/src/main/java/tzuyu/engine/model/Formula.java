package tzuyu.engine.model;

import java.util.List;

import tzuyu.engine.bool.Var;
import tzuyu.engine.bool.formula.Atom;
import tzuyu.engine.bool.formula.False;
import tzuyu.engine.bool.formula.True;
import tzuyu.engine.iface.ExpressionVisitor;

public interface Formula {

  public static final Formula TRUE = True.getInstance();
  public static final Formula FALSE = False.getInstance();

  public List<Var> getReferencedVariables();

  public boolean evaluate(Object[] objects);

  public boolean evaluate(Prestate state);

  public List<Atom> getAtomics();

  public void accept(ExpressionVisitor visitor);
}
