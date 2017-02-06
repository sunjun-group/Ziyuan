package tzuyu.engine.store;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tzuyu.engine.model.Prestate;
import tzuyu.engine.model.QueryResult;
import tzuyu.engine.model.QueryTrace;
import tzuyu.engine.model.TzuYuAction;
import tzuyu.engine.model.dfa.DFA;
import tzuyu.engine.model.dfa.Transition;
import tzuyu.engine.model.exception.TzRuntimeException;

/**
 * This class runs a query trace in which the action is based on old versions of
 * the alphabet of the current DFA and classify the instrumented states in the
 * trace to the abstract states in the current DFA.
 * 
 * @author Spencer Xiao
 * 
 */
public class DFARunner {
	private static Logger log = LoggerFactory.getLogger(DFARunner.class);
	private DFA currentDFA;

	private Map<Transition, QueryResult> transitionStates;

	public DFARunner(DFA dfa) {
		currentDFA = dfa;
		transitionStates = new LinkedHashMap<Transition, QueryResult>();
	}

	public Map<Transition, QueryResult> getStatesByTransition() {
		return transitionStates;
	}

	/**
	 * Dispatch the instrumented states in an old query trace to the abstract
	 * states in the DFA of newer version of alphabet.
	 * 
	 * @param trace
	 */
	public void runOldTrace(QueryTrace trace) {

		int currentState = currentDFA.getInitialState();
		int lastActionIndex = trace.lastActionIdx;

		if (trace.isAccepted()) {
			lastActionIndex = lastActionIndex - 1;
		}

		for (int index = -1; index <= lastActionIndex; index++) {
			TzuYuAction statement = trace.query.getStatement(index + 1);
			List<Transition> transitions = currentDFA.getState(currentState).trans;
			boolean matchingTransitionFound = false;
			for (Transition tran : transitions) {
				TzuYuAction tzuyuSymbol = (TzuYuAction) tran.action;
				if (statement.getAction().equals(tzuyuSymbol.getAction())) {

					QueryResult statesPair = null;
					if (transitionStates.containsKey(tran)) {
						statesPair = transitionStates.get(tran);
					} else {
						statesPair = new QueryResult();
						transitionStates.put(tran, statesPair);
					}

					Prestate state = trace.getStates().get(index + 1);
					boolean passing = tzuyuSymbol.getGuard().evaluate(state);
					if (passing) {
						if (index < trace.lastActionIdx) {
							statesPair.positiveSet.add(trace
									.getQueryTraceWithSubStates(index + 1));
						} else {
							statesPair.negativeSet.add(trace
									.getQueryTraceWithSubStates(index + 1));
						}
						// By definition, state can't pass multiple
						// transitions(otherwise,
						// this is not a DFA but a NFA), if we find one which
						// proceeds to
						// a next state and the next action in the trace.
						currentState = tran.target;
						matchingTransitionFound = true;
						break;
					} else {
						continue;
					}
				}
			}

			// This should not happen
			if (!matchingTransitionFound) {
				log.error("No matching transition!!", "\ntrace: ",
						trace, "\nsigma: " + currentDFA.getSigma());
				throw new TzRuntimeException("no matching transtion");
			}
		}
	}
}
