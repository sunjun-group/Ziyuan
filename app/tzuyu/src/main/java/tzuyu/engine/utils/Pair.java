package tzuyu.engine.utils;

public class Pair<A, B> {
  private A first;
  private B second;

  public Pair(A a, B b) {
    first = a;
    second = b;
  }

  public A first() {
    return first;
  }

  public B second() {
    return second;
  }
}
