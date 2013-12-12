package tester;

import java.util.List;

import tzuyu.engine.model.Sequence;
import tzuyu.engine.model.Variable;


/**
 * A sequence runtime of a sequence is a serial of runtime objects which are the
 * parameter values to the methods defined in the sequence.
 * 
 * @author Spencer Xiao
 * 
 */
public class SequenceRuntime {
  /**
   * The original sequence which generates this runtime.
   */
  private Sequence sequence;
  /**
   * The set of out reference variables which were passed to methods as input
   * parameters but may also were modified by the method, and their values could
   * be used as other methods' input parameters.
   */
  private List<List<Object>> outputValues;

  /**
   * The set of return values of the method calls in the sequence, for void
   * method calls we add an default place holder at the respective position.
   */
  private List<Object> retValues;

  private boolean executionResult;

  public SequenceRuntime(boolean result, Sequence seq, List<Object> ret,
      List<List<Object>> out) {
    this.executionResult = result;
    this.sequence = seq;
    this.retValues = ret;
    this.outputValues = out;
  }

  public Object getValue(Variable inputVar) {
    if (!inputVar.owner.equals(sequence)) {
      throw new IllegalArgumentException("The sequence that generates the "
          + "variable has not been executed");
    }

    int stmtIndex = inputVar.getDeclIndex();
    int paramIndex = inputVar.getVarIndex();

    if (paramIndex == -1) {
      return retValues.get(stmtIndex);
    } else {
      return outputValues.get(stmtIndex).get(paramIndex);
    }
  }

  public Sequence getOriginalSequence() {
    return this.sequence;
  }

  public boolean isSuccessful() {
    return executionResult == true;
  }
}
