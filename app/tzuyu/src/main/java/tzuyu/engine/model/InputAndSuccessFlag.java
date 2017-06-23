package tzuyu.engine.model;

import java.util.List;

/**
 * Return type for ParameterSelector's selectParameter method, which is
 * responsible for selecting a set of component sequences to be concatenated
 * into a longer sequence.
 * 
 * @author Spencer Xiao
 * 
 */
public class InputAndSuccessFlag {
  /**
   * True is the method selectParameter was able to find component sequences for
   * all the input types required by the given statement.
   */
  public final boolean success;

  /**
   * A list of sequences contains the sequence which generates the input
   * variable at the corresponding position.
   */
  public List<Sequence> sequences;

  /**
   * The indices for a variable, where the first one stands for the statement
   * index and the second represents the parameter index
   */
  public List<VarIndex> indices;

  public InputAndSuccessFlag(boolean successFlag, List<Sequence> seqs,
      List<VarIndex> varIndices) {
    this.success = successFlag;
    this.sequences = seqs;
    this.indices = varIndices;
  }
}
