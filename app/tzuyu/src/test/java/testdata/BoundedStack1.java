package testdata;

/**
 * Bounded Stack implementation, cased adapted from paper "Active Automata
 * learning: From DFAs to Interface Programs and Beyond" by Bernhard Steffen,
 * etc..
 * 
 * @author Spencer Xiao
 * 
 */
public class BoundedStack1 {

  private static final int MaxSize = 3;
  private int size;
  private Integer[] data;

  public BoundedStack1() {
    size = 0;
    data = new Integer[MaxSize];
  }

  public int size() {
  	return size;
  }
  
  public boolean push(Integer element) throws Exception {
    if (size == MaxSize) {
      throw new Exception("Push on full stack.");
    }

    data[size] = element;
    size++;
    return true;
  }

  public Integer pop() throws Exception {
    if (size == 0) {
      throw new Exception("Pop an empty stack.");
    }
    Integer ret = data[size - 1];
    size--;
    return ret;

  }
}
