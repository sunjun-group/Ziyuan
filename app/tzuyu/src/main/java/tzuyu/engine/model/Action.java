package tzuyu.engine.model;

/**
 * An action on a transition of a DFA is a method call.
 * @author Spencer Xiao
 *
 */
public abstract class Action {
  public static final Action epsilon = new Epsilon();
  
  public abstract boolean isConstructor();
}

class Epsilon extends Action {
  public Epsilon() {
  }

  @Override
  public String toString() {
    return ("epsilon");
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    }

    return (o instanceof Epsilon);
  }

  @Override
  public int hashCode() {
    return 11;
  }

  @Override
  public boolean isConstructor() {
    return false;
  }
}
