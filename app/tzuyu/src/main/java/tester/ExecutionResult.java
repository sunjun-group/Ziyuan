package tester;

import java.util.List;

public final class ExecutionResult {
  private boolean passing;
  private List<Object> values;

  public ExecutionResult(boolean rst, List<Object> inputVals) {
    this.passing = rst;
    this.values = inputVals;
  }

  public boolean isPassing() {
    return passing;
  }

  public List<Object> getRuntime() {
    return values;
  }

  @Override
  public String toString() {
    return Boolean.toString(passing) + ":" + values.size();
  }
}
