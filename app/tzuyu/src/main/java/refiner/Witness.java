package refiner;

import tzuyu.engine.model.Action;
import tzuyu.engine.model.Formula;
import tzuyu.engine.model.Trace;

public class Witness {
  public final Formula divider;

  public final Action action;

  public final Trace counterexample;

  public final boolean success;

  public Witness(Formula witness, Action act) {
    this.divider = witness;
    this.action = act;
    counterexample = null;
    success = true;
  }

  public Witness(Trace counterexample, Action act) {
    this.counterexample = counterexample;
    this.divider = null;
    this.action = act;
    this.success = false;
  }
}
