package tzuyu.engine.model;

import java.io.Serializable;

/**
 * 
 * Used to represents inputs to a statement. Implementation notes: Recall that 
 * a sequence is a sequence of statements where the inputs to a statement are
 * values created by an earlier statement. Instead of using a Variable to
 * represent such inputs, we use a RelativeNegativeIndex, which is just a
 * wrapper for an integer. The integer represents a negative offset from the
 * statement index in which this RelativeNegative lives, and the offset points
 * to the statement that created the values that is used as an input. In other
 * words, a RelativeNegativeIndex says
 * "I represent the value created by the N-th statement above me".
 * 
 * @author Spencer Xiao
 * 
 */
public class RelativeNegativeIndex implements Serializable {

  private static final long serialVersionUID = 6677077531443066995L;

  public final int stmtIdx;
  public final int argIdx;

  public RelativeNegativeIndex(int stmtIndex, int varIndex) {
    if (stmtIndex >= 0) {
      throw new IllegalArgumentException("invalid index "
          + "(expecting non-positive):" + stmtIndex);
    }
    this.stmtIdx = stmtIndex;
    this.argIdx = varIndex;
  }

  @Override
  public String toString() {
    return Integer.toString(stmtIdx);
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    }

    if (!(o instanceof RelativeNegativeIndex)) {
      return false;
    }
    return this.stmtIdx == ((RelativeNegativeIndex) o).stmtIdx
        && this.argIdx == ((RelativeNegativeIndex) o).argIdx;
  }

  @Override
  public int hashCode() {
    return this.stmtIdx * 31 + argIdx;
  }

}
