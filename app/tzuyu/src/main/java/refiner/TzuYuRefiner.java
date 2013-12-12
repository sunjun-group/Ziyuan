package refiner;

import java.util.Map;

import tzuyu.engine.model.Action;
import tzuyu.engine.model.Formula;
import tzuyu.engine.model.QueryResult;
import tzuyu.engine.model.QueryTrace;
import tzuyu.engine.model.Trace;
import tzuyu.engine.model.TzuYuAction;
import tzuyu.engine.model.dfa.DFA;
import tzuyu.engine.model.dfa.Transition;
import tzuyu.engine.store.IQueryTraceStore;
import tzuyu.engine.store.QueryTraceStoreFactory;
import tzuyu.engine.utils.Pair;




/**
 * This version of refiner is based on the version based trace store and use all
 * the traces generated so far to find the inconsistent transition.
 * 
 * @author Spencer Xiao
 * 
 */
public class TzuYuRefiner {
  private SVMWrapper classifier;
  private IQueryTraceStore traceStore;

  public TzuYuRefiner() {
    classifier = new SVMWrapper();
    traceStore = QueryTraceStoreFactory.createStore();
  }

  /**
   * Generate a boolean formula divider which separates positive data set from
   * the negative data set. The divider are generated from SVM classifier.
   * 
   * @param cex
   *          the query result returned by membership query to a teacher
   * @return the divider or null if we cannot find one.
   */
  public Formula membershipRefinement(QueryResult cex) {

    if (cex == null) {
      throw new IllegalArgumentException("the counter example cannot be null");
    }

    if (cex.positiveSet.size() <= 0 || cex.negativeSet.size() <= 0) {
      throw new IllegalArgumentException("the size of positive and negtative"
          + " counter examples must be greater than zero");
    }

    // Invoke the LibSVM to refine the alphabet.
    Formula divider = classifier.memberDivide(cex.positiveSet, cex.negativeSet);
    // If a divider is found we need to clear the traces
    // for the next learning pass.
    if (divider != null) {
      this.traceStore.clear();
    }

    return divider;
  }

  /**
   * Generate a divider for an inconsistent transition on the DFA. It search all
   * the traces generated so far and try to find an inconsistent transition and
   * the two sets of data states on the source state of the DFA. Then it use SVM
   * to generate the divider. If only one of the two inconsistent data sets
   * exists, we just return a counterexample query for the inconsistent
   * transition. and
   * 
   * @param dfa
   *          the DFA on which inconsistent transition happens
   * @return the transition and its associated data states or the transition and
   *         the counterexample query.
   */
  public Witness candidateRefinement(DFA dfa) {

    Map<Transition, QueryResult> resultMap = traceStore
        .findAllInconsitentTrans(dfa);
    // Find one transition to refine

    Pair<Transition, QueryResult> result = findInconsistentTransition(dfa,
        resultMap);

    // No counterexample, return a null divider
    if (result == null) {
      return new Witness(Formula.TRUE, Action.epsilon);
    }

    QueryResult queryResult = result.second();

    Action symbol = result.first().action;
    TzuYuAction tzuyuSymbol = (TzuYuAction) symbol;

    if (queryResult.positiveSet.size() == 0) {
      StringConverter converter = new StringConverter(dfa);
      QueryTrace ctexample = queryResult.negativeSet.get(0);
      Trace counterexampleLString = converter.convertToNewString(ctexample);
      return new Witness(counterexampleLString, tzuyuSymbol);
    } else if (queryResult.negativeSet.size() == 0) {
      StringConverter converter = new StringConverter(dfa);
      QueryTrace ctexample = queryResult.positiveSet.get(0);
      Trace counterexampleLString = converter.convertToNewString(ctexample);
      return new Witness(counterexampleLString, tzuyuSymbol);
    } else {
      // Invoke LibSVM to refine the alphabet.
      Formula divider = classifier.candidateDivide(tzuyuSymbol,
          queryResult.positiveSet, queryResult.negativeSet);
      // If a divider is found, clear the traces to prepare for
      // next round of learning
      if (divider != null) {
        traceStore.clear();
      }

      return new Witness(divider, tzuyuSymbol);
    }
  }

  /**
   * Find an inconsistent transition on the DFA and the inconsistent data states
   * on the source state of the transition. There is an inconsistency when one
   * of the following conditions happens:
   * <ul>
   *  <li>there are both positive traces and negative traces on the source 
   *  state of a transition. 
   *  <li>negative traces for a transition whose target state is an accepting 
   *  state. 
   *  <li>positive traces for a transition whose target state is a rejecting 
   *  state.
   * </ul>
   * We prefer the case (the first case) where both positive and negative traces 
   * exist to the last two cases since alphabet refinement makes TzuYu converge 
   * faster. That is to say we prefer TzuYu alphabet refinement to L* control 
   * refinement.
   * @param dfa
   *          on which the inconsistent transition happens
   * @param resultMap
   *          the transition and its inconsistent states
   * @return
   */
  private Pair<Transition, QueryResult> findInconsistentTransition(
      DFA dfa, Map<Transition, QueryResult> resultMap) {
    //We first traverse the map to find the case that both positive and 
    //negative traces exist for a transition in order to do TzuYu alphabet 
    //refinement to L* control refinement.
    for (Transition transition : resultMap.keySet()) {
      QueryResult queryResult = resultMap.get(transition);
      int positiveSize = queryResult.positiveSet.size();
      int negativeSize = queryResult.negativeSet.size();
      if (positiveSize != 0 && negativeSize != 0) {
        return new Pair<Transition, QueryResult>(transition, queryResult);
      } 
    }
    
    //If we cannot find the both positive and negative traces for a transition 
    //We try to find a case where there is counterexample traces to let L* do 
    //the control refinement.
    for (Transition transition : resultMap.keySet()) {
      QueryResult queryResult = resultMap.get(transition);
      int positiveSize = queryResult.positiveSet.size();
      int negativeSize = queryResult.negativeSet.size();
      if (positiveSize != 0) {
        if (!dfa.isAccepting(transition.target)) {
          // positive counterexample found
          return new Pair<Transition, QueryResult>(transition, queryResult);
        }
      } else if (negativeSize != 0) {
        if (dfa.isAccepting(transition.target)) {
          // negative counterexample found
          return new Pair<Transition, QueryResult>(transition, queryResult);
        }
      } else {
        continue;
      }
    }
    //There is no inconsistent transition, just return null.
    return null;

  }
}
