package tzuyu.engine.store;

import java.util.ArrayList;
import java.util.List;

import tzuyu.engine.model.QueryTrace;


public class TraceCollection {

  private List<QueryTrace> alltraces = new ArrayList<QueryTrace>();

  /**
   * Don't allow repeated traces to save memory footprint
   * 
   * @param trace
   */
  public void add(QueryTrace trace) {
    if (alltraces.indexOf(trace) == -1) {
      alltraces.add(trace);
    }
  }

  public List<QueryTrace> toJDKList() {
    return alltraces;
  }

  public int size() {
    return alltraces.size();
  }

}
