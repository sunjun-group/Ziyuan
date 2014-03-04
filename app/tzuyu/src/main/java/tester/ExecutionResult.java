package tester;

import java.util.List;

public final class ExecutionResult {
  private boolean passing;
  private List<Object> values;

  public ExecutionResult(boolean result, List<Object> inputVals) {
    this.passing = result;
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
