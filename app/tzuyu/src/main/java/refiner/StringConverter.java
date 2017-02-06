package refiner;

import java.util.List;

import sav.common.core.utils.Assert;
import tzuyu.engine.model.Prestate;
import tzuyu.engine.model.QueryTrace;
import tzuyu.engine.model.Trace;
import tzuyu.engine.model.TzuYuAction;
import tzuyu.engine.model.dfa.DFA;
import tzuyu.engine.model.dfa.Transition;


public class StringConverter {

  private DFA currentDFA;

  public StringConverter(DFA dfa) {
    currentDFA = dfa;
  }

  /**
   * Convert an old query trace to a string of a newer version of DFA
   * 
   * @param trace
   * @return
   */
  public Trace convertToNewString(QueryTrace trace) {
    Trace string = new Trace();
    int currentState = currentDFA.getInitialState();
    int lastActionIndex = trace.lastActionIdx;

    //if (trace.isAccepted()) {
      //lastActionIndex = lastActionIndex - 1;
    //}

    for (int index = 0; index <= lastActionIndex; index++) {
      TzuYuAction statement = trace.query.getStatement(index);
      List<Transition> transitions = currentDFA.getState(currentState).trans;
      boolean matchingTransitionFound = false;
      for (Transition tran : transitions) {

        TzuYuAction tzuyuSymbol = (TzuYuAction) tran.action;
        if (statement.getAction().equals(tzuyuSymbol.getAction())) {
          Prestate state = trace.getStates().get(index);
          boolean passing = tzuyuSymbol.getGuard().evaluate(state);
          if (passing) {
            // append the corresponding action in the new DFA to the result
            // string
            string.appendAtTail(tzuyuSymbol);
            // Since state can't pass multiple transitions(otherwise, this
            // is not a DFA but a NFA), if we find one, proceed to the next
            // state and the next action in the trace.
            currentState = tran.target;
            matchingTransitionFound = true;
            break;
          } else {
            continue;
          }
        }
      }
      Assert.assertTrue(!matchingTransitionFound, "no matching transtion");
    }
    return string;
  }

}
