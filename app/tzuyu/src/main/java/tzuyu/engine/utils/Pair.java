package tzuyu.engine.utils;

public class Pair<A, B> {
  private A first;
  private B second;

  public static <A, B>Pair<A, B> of (A a, B b) {
	  return new Pair<A, B>(a, b);
  }
  
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
