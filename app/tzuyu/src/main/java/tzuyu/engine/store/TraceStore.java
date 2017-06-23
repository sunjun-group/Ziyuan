package tzuyu.engine.store;

import tzuyu.engine.model.QueryResult;
import tzuyu.engine.model.QueryTrace;
import tzuyu.engine.model.dfa.DFA;

public interface TraceStore {
  public QueryResult findCexForNegativeTrace(DFA dfa, QueryTrace ntr);

  public QueryResult findCexForPositiveTrace(DFA dfa, QueryTrace ptr);

  public void clear();
}
