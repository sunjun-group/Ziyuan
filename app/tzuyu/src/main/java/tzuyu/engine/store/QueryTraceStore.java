package tzuyu.engine.store;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import tzuyu.engine.model.Action;
import tzuyu.engine.model.Prestate;
import tzuyu.engine.model.Query;
import tzuyu.engine.model.QueryResult;
import tzuyu.engine.model.QueryTrace;
import tzuyu.engine.model.Trace;
import tzuyu.engine.model.TzuYuAction;
import tzuyu.engine.model.dfa.DFA;
import tzuyu.engine.model.dfa.RunningResult;
import tzuyu.engine.model.dfa.Transition;
import tzuyu.engine.utils.Pair;
import tzuyu.engine.utils.Randomness;


/**
 * This version of query trace store is different from the first version in
 * that, when finding the counterpart data for a given counterexample trace, we
 * first dispatch all the traces in this store to the abstract states in the
 * DFA, then run the counterexample trace on the DFA to get the transition on
 * which inconsistency occurred, then return all the pre-states for this
 * transition.
 * 
 * @author Spencer Xiao
 * 
 */
public class QueryTraceStore implements IQueryTraceStore {

  private MappedTraces<Query> normalTraces;

  private MappedTraces<Query> errorTraces;

  private MappedTraces<Query> unknownTraces;

  // private static QueryTraceStoreV2 instance = new QueryTraceStoreV2();

  public QueryTraceStore() {
    normalTraces = new MappedTraces<Query>();
    errorTraces = new MappedTraces<Query>();
    unknownTraces = new MappedTraces<Query>();
  }

  // public static QueryTraceStoreV2 getInstance() {
  // return instance;
  // }

  public void addNormal(Query key, QueryTrace trace) {
    normalTraces.addTrace(key, trace);
  }

  public void addError(Query key, QueryTrace trace) {
    errorTraces.addTrace(key, trace);
  }

  public void addUnknown(Query key, QueryTrace trace) {
    unknownTraces.addTrace(key, trace);
  }

  public List<QueryTrace> findNormal(Query key) {
    TraceCollection collection = normalTraces.find(key);
    return collection.toJDKList();
  }

  public List<QueryTrace> findError(Query key) {
    TraceCollection collection = errorTraces.find(key);
    return collection.toJDKList();
  }

  public List<QueryTrace> findUnknown(Query key) {
    TraceCollection collection = unknownTraces.find(key);
    return collection.toJDKList();
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

  public int getUnknownSize(Query key) {
    return findUnknown(key).size();
  }

  /**
   * Find the negative data set on which the transition failed but proceeded
   * correctly on the input positive counterexample. Positive counterexample was
   * supposed to fail at some transition in the postulated automata, but succeed
   * in its real execution.
   * 
   * @param dfa
   *          the postulated dfa by the learner
   * @param ptr
   *          the positive counterexample returned by the tester
   * @return test result contains the corresponding negative data for the input
   *         positive counterexample
   */
  private QueryResult findPeersForPostiveCex(DFA dfa, Transition tran) {
    QueryResult queryResult = new QueryResult();
    // Then find the test case which threw errors in this transition
    Set<Query> keys = this.errorTraces.getKeys();
    for (Query key : keys) {
      List<QueryTrace> traces = this.findError(key);
      // Use failed test cases
      for (QueryTrace ntr : traces) {
        // Don't add self to both positive examples and negative examples.
        // If the test case is assertion violation, then the it cannot be
        // treated as positive nor negative.
        if (ntr.isUnknown()) {
          continue;
        }
        // Find the first erroneous action
        int nMaxIndex = ntr.lastActionIdx;
        Trace keyStr = key.getLString();
        // int action = keyStr.str.getValue(nMaxIndex + 1);
        Action action = keyStr.valueAt(nMaxIndex + 1);
        // Check that action which thrown exception is the same
        // as the first erroneous action that the dfa thoughts
        if (tran.action.equals(action)) {
          RunningResult nrr = dfa.runString(keyStr, nMaxIndex + 1);
          int nLen = nrr.runLen;
          Transition testTran = nrr.tran;
          if (testTran.equals(tran)) {
            QueryTrace nntr = ntr.getQueryTraceWithSubStates(nLen);
            if (nrr.runLen < nMaxIndex + 1) {
              queryResult.positiveSet.add(nntr);
            } else {
              queryResult.negativeSet.add(nntr);
            }
          }
        }
      }
    }

    return queryResult;
  }

  /**
   * Find the positive data set on which the transition proceeded correctly but
   * failed on the input negative counterexample. Negative counterexample was
   * supposed to traverse the automata and end at an accepting state, but there
   * was a failed transition in its real execution.
   * 
   * @param dfa
   *          the postulated dfa by the learner
   * @param tc
   *          the negative counterexample returned by the tester
   * @return test result contains the positive data which correspond to the
   *         input negative data.
   */
  private QueryResult findPeersForNegativeCex(DFA dfa, Transition tran) {

    QueryResult queryResult = new QueryResult();
    // Then find the test case in which the same transition was OK.
    Set<Query> nkeys = this.errorTraces.getKeys();
    Set<Query> pkeys = this.normalTraces.getKeys();

    List<Query> keys = new ArrayList<Query>(pkeys);
    keys.removeAll(nkeys);
    keys.addAll(nkeys);

    for (Query key : keys) {
      List<QueryTrace> pTraces = this.findNormal(key);
      List<QueryTrace> nTraces = this.findError(key);
      // Use all test cases
      List<QueryTrace> allCases = new ArrayList<QueryTrace>(pTraces);
      allCases.addAll(nTraces);
      for (QueryTrace ptr : allCases) {
        int pMaxIndex = ptr.lastActionIdx;
        Trace keyStr = key.getLString();
        // Try out all the prefixes to search for the peer transitions
        for (int index = -1; index <= pMaxIndex; index++) {
          // int action = keyStr.str.getValue(index + 1);
          Action action = keyStr.valueAt(index + 1);
          // Check that action is the same as the negative counterexample
          // before confirming that they conform to the same transition
          if (tran.action.equals(action)) {
            RunningResult prr = dfa.runString(keyStr, index + 1);
            int pLen = prr.runLen;
            Transition testTran = prr.tran;
            // Here we use transition to check equality, which means
            // we are find counterexamples for the same source with
            // the same action.
            if (testTran.equals(tran)) {
              QueryTrace nptr = ptr.getQueryTraceWithSubStates(pLen);
              // According to the DFA, there is an error action before the
              // index, but it runs successfully, then it should be a positive
              // counterexample.
              if (pLen == index + 1 && index < pMaxIndex) {
                queryResult.positiveSet.add(nptr);
              } else if (index == pMaxIndex) {// pLen == index + 1
                queryResult.negativeSet.add(nptr);
              }
            }
          }
        }
      }
    }

    return queryResult;
  }

  public void clear() {
    /**
     * For trace store version 2, we don't clear the stored traces.
     */
  }

  /**
   * Find all the inconsistent transitions on all abstract states in the DFA for
   * the given set of counterexamples.
   * 
   * @param dfa
   * @param cexs
   */
  public Map<Transition, QueryResult> findInconsistentTransitions(
      DFA dfa, QueryResult cexs) {

    Map<Transition, QueryResult> cachedResult = 
        new LinkedHashMap<Transition, QueryResult>();

    for (QueryTrace trace : cexs.negativeSet) {
      Trace str = trace.query.getLString();
      int lastIndex = trace.lastActionIdx;
      RunningResult rr = dfa.runString(str, lastIndex + 1);
      Transition transition = rr.tran;

      QueryResult queryResult = null;

      if (cachedResult.containsKey(transition)) {
        queryResult = cachedResult.get(transition);
      } else {
        queryResult = findPeersForNegativeCex(dfa, transition);
        cachedResult.put(transition, queryResult);
      }
      int len = rr.runLen;
      // len lies in [0, lastIndex + 1]
      QueryTrace subTrace = trace.getQueryTraceWithSubStates(len);
      queryResult.negativeSet.add(subTrace);
    }

    for (QueryTrace trace : cexs.positiveSet) {
      Trace str = trace.query.getLString();
      // The string should be rejected by the dfa.
      int lastIndex = trace.lastActionIdx;
      RunningResult rr = dfa.runString(str, lastIndex + 1);
      Transition transition = rr.tran;
      int len = rr.runLen;

      QueryResult queryResult = new QueryResult();

      if (cachedResult.containsKey(transition)) {
        queryResult = cachedResult.get(transition);
      } else {
        queryResult = findPeersForPostiveCex(dfa, transition);
        cachedResult.put(transition, queryResult);
      }

      QueryTrace subTrace = trace.getQueryTraceWithSubStates(len);
      queryResult.positiveSet.add(subTrace);
    }

    return cachedResult;
  }

  public Map<Transition, QueryResult> dispatchTraces(DFA dfa) {
    Set<Query> nKeys = errorTraces.getKeys();
    Set<Query> pKeys = normalTraces.getKeys();

    DFARunner runner = new DFARunner(dfa);

    List<Query> keys = new ArrayList<Query>(nKeys);
    keys.removeAll(pKeys);
    keys.addAll(pKeys);

    for (Query key : keys) {
      List<QueryTrace> pTraces = this.findNormal(key);
      List<QueryTrace> nTraces = this.findError(key);

      List<QueryTrace> traces = new ArrayList<QueryTrace>(pTraces);
      traces.addAll(nTraces);
      for (QueryTrace trace : traces) {
        if (trace.query.isEpsilon() && (!trace.isAccepted())) {
          continue;
        }
        runner.runOldTrace(trace);
      }
    }
    return runner.getStatesByTransition();
  }

  /**
   * Find the positive data set on which the transition proceeded correctly but
   * failed on the input negative counterexample. Negative counterexample was
   * supposed to traverse the automata and end at an accepting state, but there
   * was a failed transition in its real execution.
   * 
   * @param dfa
   *          the postulated dfa by the learner
   * @param tc
   *          the negative counterexample returned by the tester
   * @return test result contains the positive data which correspond to the
   *         input negative data.
   */
  public Pair<Transition, QueryResult> findCexForNegativeTrace(
      DFA dfa, QueryTrace trace) {

    Map<Transition, QueryResult> statesPerTransition = dispatchTraces(dfa);

    Trace str = trace.query.getLString();
    int lastIndex = trace.lastActionIdx;
    RunningResult rr = dfa.runString(str, lastIndex + 1);
    Transition transition = rr.tran;

    QueryResult statesPair = statesPerTransition.get(transition);

    return new Pair<Transition, QueryResult>(transition, statesPair);
  }

  /**
   * Find the negative data set on which the transition failed but proceeded
   * correctly on the input positive counterexample. Positive counterexample was
   * supposed to fail at some transition in the postulated automata, but succeed
   * in its real execution.
   * 
   * @param dfa
   *          the postulated dfa by the learner
   * @param ptr
   *          the positive counterexample returned by the tester
   * @return test result contains the corresponding negative data for the input
   *         positive counterexample
   */
  public Pair<Transition, QueryResult> findCexForPositiveTrace(
      DFA dfa, QueryTrace trace) {
    Map<Transition, QueryResult> statesPerTransition = dispatchTraces(dfa);

    Trace str = trace.query.getLString();
    int lastIndex = trace.lastActionIdx;
    RunningResult rr = dfa.runString(str, lastIndex + 1);
    Transition transition = rr.tran;

    QueryResult statesPair = statesPerTransition.get(transition);

    return new Pair<Transition, QueryResult>(transition, statesPair);
  }

  public boolean findFailedTransition(TzuYuAction stmt) {
    Set<Query> keys = errorTraces.getKeys();
    for (Query query : keys) {
      List<QueryTrace> traces = findError(query);
      for (QueryTrace trace : traces) {
        if (trace.query.isEpsilon() && trace.isRejected()) {
          continue;
        }

        TzuYuAction action = trace.getNextAction();
        if (action.getAction().equals(stmt.getAction())) {
          Prestate state = trace.getLastState();
          // If the guard of the statement is satisfied, 
          // then we find an evidence
          if (stmt.getGuard().evaluate(state)) {
            return true;
          }
        }
      }
    }
    return false;
  }

  @Override
  public Map<Transition, QueryResult> findAllInconsitentTrans(DFA dfa) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public boolean findFailedEvidenceForUnknownTransition(TzuYuAction action) {
    // TODO Auto-generated method stub
    return false;
  }

}
