package tzuyu.engine.store;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import tzuyu.engine.model.Query;
import tzuyu.engine.model.QueryResult;
import tzuyu.engine.model.QueryTrace;
import tzuyu.engine.model.TzuYuAction;
import tzuyu.engine.model.dfa.DFA;
import tzuyu.engine.model.dfa.Transition;
import tzuyu.engine.utils.Pair;
import tzuyu.engine.utils.Randomness;


/**
 * This is a version based query trace store, each version of the trace store is
 * an instance of the {@link QueryTraceStore}. After each candidate query on
 * current version, we push the current version to the old versions. The
 * insertion of traces resulting from membership queries and candidate queries
 * is made on the current version. Find counterpart data for a counterexample
 * trace is done on both old versions and current versions.
 * 
 * @author Spencer Xiao
 * 
 */
public class VersionBasedQueryTraceStore implements IQueryTraceStore {

  private List<QueryTraceStore> oldVersions;

  private QueryTraceStore currentVersion;

  private static VersionBasedQueryTraceStore instance = 
      new VersionBasedQueryTraceStore();

  private VersionBasedQueryTraceStore() {
    oldVersions = new LinkedList<QueryTraceStore>();
    currentVersion = new QueryTraceStore();
  }

  static VersionBasedQueryTraceStore getInstance() {
    return instance;
  }

  public void addNormal(Query key, QueryTrace trace) {
    currentVersion.addNormal(key, trace);
  }

  public void addError(Query key, QueryTrace trace) {
    currentVersion.addError(key, trace);
  }

  public void addUnknown(Query key, QueryTrace trace) {
    currentVersion.addUnknown(key, trace);
  }

  public List<QueryTrace> findNormal(Query key) {
    return currentVersion.findNormal(key);
  }

  public List<QueryTrace> findError(Query key) {
    return currentVersion.findError(key);
  }

  public List<QueryTrace> findUnknown(Query key) {
    return currentVersion.findUnknown(key);
  }

  public QueryTrace findNormalRandomly(Query key) {
    List<QueryTrace> traces = findNormal(key);
    if (traces.size() == 0) {
      return null;
    } else {
      return Randomness.randomMember(traces);
    }
  }

  public QueryTrace findErrorRandomly(Query key) {
    List<QueryTrace> traces = findError(key);
    if (traces.size() == 0) {
      return null;
    } else {
      return Randomness.randomMember(traces);
    }
  }

  public int getErrorSize(Query key) {
    return findError(key).size();
  }

  public int getNormalSize(Query key) {
    return findNormal(key).size();
  }

  public int getFailedSize(Query key) {
    return findUnknown(key).size();
  }

  /**
   * Find all the counterpart data for the given counterexample trace from the
   * current trace store and all the old versions of trace stores.
   * 
   * @param dfa
   * @param trace
   * @return
   */
  public Pair<Transition, QueryResult> findCexForNegativeTrace(
      DFA dfa, QueryTrace trace) {
    Pair<Transition, QueryResult> result = currentVersion
        .findCexForNegativeTrace(dfa, trace);
    // We should better use the latest history result
    for (QueryTraceStore version : oldVersions) {
      Pair<Transition, QueryResult> resultPair = version
          .findCexForNegativeTrace(dfa, trace);
      // merge with the result from current version
      result.second().positiveSet.addAll(resultPair.second().positiveSet);
      result.second().negativeSet.addAll(resultPair.second().negativeSet);
    }
    return result;
  }

  /**
   * Find all the counterpart data fro the given counterexample trace from the
   * current trace store and all the old versions of trace stores.
   * 
   * @param dfa
   * @param trace
   * @return
   */
  public Pair<Transition, QueryResult> findCexForPositiveTrace(
      DFA dfa, QueryTrace trace) {
    Pair<Transition, QueryResult> result = currentVersion
        .findCexForPositiveTrace(dfa, trace);

    for (QueryTraceStore version : oldVersions) {
      Pair<Transition, QueryResult> resultPair = version
          .findCexForPositiveTrace(dfa, trace);
      // merge with the result from current version
      result.second().positiveSet.addAll(resultPair.second().positiveSet);
      result.second().negativeSet.addAll(resultPair.second().negativeSet);
    }
    return result;
  }

  /**
   * Find all the inconsistent transitions of traces in both old versions of
   * trace store and current trace store on the current dfa.
   * 
   * @param dfa
   * @param cexs
   * @return
   */
  public Map<Transition, QueryResult> findInconsistentTransitions(
      DFA dfa, QueryResult cexs) {
    Map<Transition, QueryResult> result = currentVersion
        .findInconsistentTransitions(dfa, cexs);

    for (QueryTraceStore version : oldVersions) {
      Map<Transition, QueryResult> resultMap = version
          .findInconsistentTransitions(dfa, cexs);
      // merge with the result from current version
      for (Transition transition : resultMap.keySet()) {
        QueryResult queryResult = resultMap.get(transition);
        QueryResult mainResult = result.get(transition);
        // The transition may not exist in the current version result
        if (mainResult == null) {
          mainResult = queryResult;
          result.put(transition, mainResult);
        } else {
          mainResult.positiveSet.addAll(queryResult.positiveSet);
          mainResult.negativeSet.addAll(queryResult.negativeSet);
        }
      }
    }
    return result;
  }

  public Map<Transition, QueryResult> findAllInconsitentTrans(DFA dfa) {
    Map<Transition, QueryResult> result = currentVersion.dispatchTraces(dfa);

    for (QueryTraceStore oldVersion : oldVersions) {
      Map<Transition, QueryResult> resultMap = oldVersion.dispatchTraces(dfa);

      for (Transition transition : resultMap.keySet()) {
        QueryResult queryResult = resultMap.get(transition);
        QueryResult mainResult = result.get(transition);
        // The transition may not exist in the current version result
        if (mainResult == null) {
          mainResult = queryResult;
          result.put(transition, mainResult);
        } else {
          mainResult.positiveSet.addAll(queryResult.positiveSet);
          mainResult.negativeSet.addAll(queryResult.negativeSet);
        }
      }
    }

    return result;
  }

  /**
   * The method does not clear any data, instead, it push the current version of
   * query trace store into the old versions list, then create an empty query
   * trace store for the current version.
   */
  public void clear() {
    // Add the latest version to the front, such that when are searching from
    // history, we use the latest history first.
    oldVersions.add(0, currentVersion);
    currentVersion = new QueryTraceStore();
  }

  public boolean findFailedEvidenceForUnknownTransition(TzuYuAction stmt) {

    if (oldVersions.size() >= 1) {
      // we only need to find in the previous version
      QueryTraceStore previousVersion = oldVersions.get(0);
      boolean result = previousVersion.findFailedTransition(stmt);
      if (result == true) {
        return true;
      }
    } else {
      boolean result = currentVersion.findFailedTransition(stmt);
      if (result == true) {
        return true;
      }
    }

    return false;
  }
}
