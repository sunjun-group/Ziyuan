package tzuyu.engine.store;

import java.util.List;
import java.util.Map;

import tzuyu.engine.model.Query;
import tzuyu.engine.model.QueryResult;
import tzuyu.engine.model.QueryTrace;
import tzuyu.engine.model.TzuYuAction;
import tzuyu.engine.model.dfa.DFA;
import tzuyu.engine.model.dfa.Transition;


public interface IQueryTraceStore {

  public void addNormal(Query key, QueryTrace trace);

  public void addError(Query key, QueryTrace trace);

  public void addUnknown(Query key, QueryTrace trace);

  public List<QueryTrace> findError(Query key);

  public List<QueryTrace> findNormal(Query key);

  public List<QueryTrace> findUnknown(Query key);

  public QueryTrace findNormalRandomly(Query key);

  public QueryTrace findErrorRandomly(Query key);

  public Map<Transition, QueryResult> findAllInconsitentTrans(DFA dfa);

  public boolean findFailedEvidenceForUnknownTransition(TzuYuAction action);

  public void clear();
}
