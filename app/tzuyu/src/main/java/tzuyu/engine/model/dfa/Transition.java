package tzuyu.engine.model.dfa;

import tzuyu.engine.model.Action;

public class Transition {
  // The index to the action in the Alphabet
  public Action action;
  // The source node of the transition
  public int source;
  // The target node of the transition
  public int target;

  @Override
  public boolean equals(Object obj) {
    if (obj == this)
      return true;

    if (!(obj instanceof Transition)) {
      return false;
    }

    Transition t = (Transition) obj;

    return source == t.source 
        && target == t.target
        &&action.equals(t.action) ;
  }

  @Override
  public int hashCode() {
    return action.hashCode() * 31 + source * 19 + target;
  }

  @Override
  public String toString() {
    return "" + source + "--" + action + "-->" + target;
  }
}
