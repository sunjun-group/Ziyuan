package tzuyu.engine.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Trace implements Serializable {

  private static final long serialVersionUID = -3251529027093400630L;

  public static final Trace epsilon = new EpsilonLString();

  private List<Action> str;

  public Trace() {
    str = new ArrayList<Action>();
  }

  public Trace(Action action) {
    str = new ArrayList<Action>();
    str.add(action);
  }

  public Trace(Trace copy) {
    str = new ArrayList<Action>(copy.str.size());
    str.addAll(copy.str);
  }

  public Trace(List<Action> copy) {
    str = new ArrayList<Action>();
    str.addAll(copy);
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    }

    if (!(o instanceof Trace)) {
      return false;
    }

    Trace lstr = (Trace) o;

    return str.equals(lstr.str);
  }

  @Override
  public int hashCode() {
    return str.hashCode();
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    for (Action action : str) {
      sb.append(action.toString());
    }
    return sb.toString();
  }

  public int size() {
    return str.size();
  }

  public Action valueAt(int index) {
    return str.get(index);
  }

  public boolean isEpsilon() {
    return false;
  }

  public Trace concatenateAtTail(Action action) {
    Trace newStr = new Trace(this);
    newStr.appendAtTail(action);
    return newStr;
  }

  public Trace concatenateAtTail(Trace suffix) {
    Trace newStr = new Trace(this);
    newStr.str.addAll(suffix.str);
    return newStr;
  }

  public Trace concatenateAtFront(Action action) {
    Trace newStr = new Trace(this);
    newStr.appendAtFront(action);
    return newStr;
  }

  public Trace concatenateAtFront(Trace prefix) {
    Trace newStr = new Trace(prefix);
    newStr.str.addAll(this.str);
    return newStr;
  }

  public void appendAtTail(Action action) {
    this.str.add(action);

  }

  public void appendAtFront(Action action) {
    this.str.add(0, action);
  }

  public List<Trace> getPrefix() {
    List<Trace> prefixes = new ArrayList<Trace>();
    for (int index = 1; index <= str.size(); index++) {
      Trace prefix = new Trace(str.subList(0, index));
      prefixes.add(prefix);
    }
    return prefixes;
  }

  public Trace getSubString(int i, int j) {
    if (i < 0 || j > size() || i > j) {
      throw new IndexOutOfBoundsException("fromIndex=" + i + "endIndex=" + j);
    }

    if (i == j) {
      return new EpsilonLString();
    }

    List<Action> subString = str.subList(i, j);

    return new Trace(subString);

  }
}

final class EpsilonLString extends Trace {
  private static final long serialVersionUID = 3099027864884352696L;

  EpsilonLString() {
  }

  @Override
  public String toString() {
    return "epsilon";
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    }

    if (!(o instanceof EpsilonLString)) {
      return false;
    }

    return true;
  }

  @Override
  public int hashCode() {
    return 17;
  }

  @Override
  public boolean isEpsilon() {
    return true;
  }

  @Override
  public Trace concatenateAtTail(Action action) {
    Trace newStr = new Trace(action);
    return newStr;
  }

  @Override
  public Trace concatenateAtTail(Trace suffix) {
    if (suffix.isEpsilon()) {
      return suffix;
    } else {
      Trace newStr = new Trace(suffix);
      return newStr;
    }
  }

  @Override
  public Trace concatenateAtFront(Action action) {
    Trace newStr = new Trace(action);
    return newStr;
  }

  @Override
  public Trace concatenateAtFront(Trace prefix) {
    if (prefix.isEpsilon()) {
      return prefix;
    } else {
      Trace newStr = new Trace(prefix);
      return newStr;
    }
  }
}
