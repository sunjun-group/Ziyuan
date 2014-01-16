package tzuyu.engine.utils;

public class Pair<A, B> {
	public A a;
	public B b;

	public static <A, B> Pair<A, B> of(A a, B b) {
		return new Pair<A, B>(a, b);
	}

	public Pair(A a, B b) {
		this.a = a;
		this.b = b;
	}

	public A first() {
		return a;
	}

	public B second() {
		return b;
	}
}
