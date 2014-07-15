package testdata.boundedStack;


/**
 * Bounded Stack implementation, cased adapted from paper "Active Automata
 * learning: From DFAs to Interface Programs and Beyond" by Bernhard Steffen,
 * etc..
 * 
 * @author Spencer Xiao
 * 
 */
public class BoundedStack {

	private static final int MaxSize = 3;
	private int size;
	private Integer[] data;

	public BoundedStack() {
		size = 0;
		data = new Integer[MaxSize];
	}

	public int size() {
		return size;
	}

	public boolean push(java.lang.Integer element) {
		System.out.println(size);
		// Tzuyu Auto-generated assertion
		assert size <= 2.0;
		if (size == MaxSize) {
			throw new RuntimeException("Push on full stack.");
		}

		data[size] = element;
		size++;
		return true;
	}

	public Integer pop() throws Exception {
		System.out.println(size);
		// Tzuyu Auto-generated assertion
		assert size >= 1.0;
		if (size == 0) {
			throw new Exception("Pop an empty stack.");
		}
		Integer ret = data[size - 1];
		size--;
		return ret;

	}
}
