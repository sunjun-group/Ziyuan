package tzuyu.engine.utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Generate either a right(e.g., [0,0,0]->[0,0,1]) or left(e.g., [0,0,0]->
 * [1,0,0]) permutation from a give integer lists, each of the element specifies
 * the number of values in that domain.
 * 
 * @author Spencer Xiao
 * 
 */
public class Permutation {

  private final List<Integer> maxSizes;
  private final boolean order;

  private List<Integer> current;

  /**
   * Initialize the generator to the domain sizes and the order of generating
   * the next permutation.
   * 
   * @param sizes
   *          the max size of each domain
   * @param right
   *          true generate from the right, false generate from left.
   */
  public Permutation(List<Integer> sizes, boolean right) {
    // Check the input such that only every elements in the
    // sizes are positive
    if (sizes == null || sizes.size() == 0) {
      throw new IllegalArgumentException("the sizes must not be "
          + "null and with at least one element");
    }

    for (int i = 0; i < sizes.size(); i++) {
      if (sizes.get(i) < 1) {
        throw new IllegalArgumentException("each element of "
            + "the sizes must be at least 1");
      }
    }
    maxSizes = sizes;
    order = right;
  }

  /**
   * Whether there are more permutations to generate;
   * 
   * @return
   */
  public boolean hasNext() {
    if (current == null) {
      return true;
    }

    for (int index = 0; index < maxSizes.size(); index++) {
      if (current.get(index) < maxSizes.get(index) - 1) {
        return true;
      }
    }

    return false;
  }

  /**
   * Get the next permutation if it has one.
   */
  public List<Integer> next() {
    if (current == null) {
      current = new ArrayList<Integer>();
      for (int i = 0; i < maxSizes.size(); i++) {
        current.add(0);
      }
      return current;
    }
    // Here we use the decimal add 1 algorithms to generate the
    // next permutation.
    if (order) {
      // the carrier number.
      int c = 1;
      // for each domain, we use the integer add algorithm to
      // generate the next value.
      for (int index = maxSizes.size() - 1; index >= 0; index--) {
        int i = (c + current.get(index)) % maxSizes.get(index);
        c = (c + current.get(index)) / maxSizes.get(index);

        current.set(index, i);
      }
    } else {
      int c = 1;
      for (int index = 0; index < maxSizes.size(); index++) {
        int i = (c + current.get(index)) % maxSizes.get(index);
        c = (c + current.get(index)) / maxSizes.get(index);

        current.set(index, i);
      }
    }
    return current;
  }

  /**
   * Reset the current permutation to the initial one(e.g.,[0,0,0]).
   */
  public void reset() {
    current = null;
  }

}
