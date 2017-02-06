package tzuyu.engine.model;

import java.util.ArrayList;
import java.util.List;

public final class QueryResult {
  /**
   * This positive set is a set of all successful executions in the case of
   * membership query and it corresponds to the positive counterexample in the
   * case of candidate query.
   */
  public List<QueryTrace> positiveSet;
  /**
   * This negative set is a set of all failed executions in the case of
   * membership query and it corresponds to the negative counterexample in the
   * case of candidate query.
   */
  public List<QueryTrace> negativeSet;

  public List<QueryTrace> unknownSet;

  public QueryResult(List<QueryTrace> positive, List<QueryTrace> negative) {
    assert (positive != null && negative != null);

    positiveSet = positive;
    negativeSet = negative;
    unknownSet = new ArrayList<QueryTrace>();
  }

  public QueryResult(List<QueryTrace> unknown) {
    positiveSet = new ArrayList<QueryTrace>();
    negativeSet = new ArrayList<QueryTrace>();
    unknownSet = unknown;
  }

  public QueryResult() {
    positiveSet = new ArrayList<QueryTrace>();
    negativeSet = new ArrayList<QueryTrace>();
    unknownSet = new ArrayList<QueryTrace>();
  }

  public boolean isAccepting() {
    int nSize = negativeSet.size();
    int pSize = positiveSet.size();
    
    return (pSize != 0 && nSize == 0);
  }

  @Override
  public String toString() {
    return "positive set size is: " + positiveSet.size()
        + "; negative set is: " + negativeSet.size();
  }

}
