package tzuyu.engine.model;

/**
 * Represents the two indices(statement index and argument index) of a variable.
 * 
 * @author Spencer Xiao
 * 
 */
public class VarIndex {

  /**
   * The index of the statement which generates the variable.
   */
  public int stmtIdx;
  /**
   * The index of the parameter which the variable refers to. -1 represents the
   * return value of the statement; non-negative values represent the normal
   * indices of the parameters. Based on the assumption that the first parameter
   * of instance method is the receiver and all other parameters are
   * concatenated to the end of the receiver.
   */
  public final int argIdx;

  public VarIndex(int stmtIndex, int argIndex) {
    this.stmtIdx = stmtIndex;
    this.argIdx = argIndex;
  }

  @Override
  public String toString() {
    return "statement index:" + stmtIdx + ", argument index:" + argIdx;
  }

}
