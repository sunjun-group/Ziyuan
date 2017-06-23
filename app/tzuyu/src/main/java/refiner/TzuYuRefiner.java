package refiner;

import java.util.List;
import java.util.Map;

import lstar.IReportHandler;
import lstar.IReportHandler.OutputType;
import tzuyu.engine.algorithm.iface.Refiner;
import tzuyu.engine.iface.ITzManager;
import tzuyu.engine.model.Action;
import tzuyu.engine.model.ClassInfo;
import tzuyu.engine.model.Formula;
import tzuyu.engine.model.QueryResult;
import tzuyu.engine.model.QueryTrace;
import tzuyu.engine.model.Trace;
import tzuyu.engine.model.TzuYuAction;
import tzuyu.engine.model.TzuYuAlphabet;
import tzuyu.engine.model.dfa.DFA;
import tzuyu.engine.model.dfa.Transition;
import tzuyu.engine.store.DFARunner;
import tzuyu.engine.utils.Pair;

/**
 * 
 * @author Spencer Xiao
 *
 */
public class TzuYuRefiner implements Refiner<TzuYuAlphabet> {

	private SVMWrapper classifier;
	private Map<Class<?>, ClassInfo> classInfoMap;
	private ITzManager<TzuYuAlphabet> manager;

	public TzuYuRefiner(ITzManager<TzuYuAlphabet> manager) {
		this.classifier = new SVMWrapper(manager);
		this.manager = manager;
	}

	public void init(TzuYuAlphabet sigma) {
		this.classInfoMap = sigma.getProject().getTypeMap();
		this.classifier.setClassInDepth(sigma.getProject().getConfiguration().getClassMaxDepth());
	}

	public int getRefinementCount() {
		return classifier.getSVMCallCount();
	}

	public int getTimeConsumed() {
		return classifier.getTimeConsumed();
	}

	public Formula refineMembership(QueryResult cex) throws InterruptedException {
		manager.checkProgress();
		if (cex == null) {
			throw new IllegalArgumentException(
					"the counter example cannot be null");
		}

		if (cex.positiveSet.size() <= 0 || cex.negativeSet.size() <= 0) {
			throw new IllegalArgumentException(
					"the size of positive and negtative"
							+ " counter examples must be greater than zero");
		}

		// Invoke the LibSVM to refine the alphabet.
		Formula divider = classifier.memberDivide(cex.positiveSet,
				cex.negativeSet, classInfoMap);

		return divider;
	}

	/**
	 * Refine the query results based on the DFA in order to find inconsistent
	 * transitions and generate the divider which can distinguish the
	 * inconsistent transition. In dealing with the counterexamples traces, we
	 * prefer alphabet refinement to control refinement, since control
	 * refinement by L* may waste efforts to construct a DFA which would be
	 * destroyed later in case we find a divider which leads to a refined
	 * alphabet, since TzuYu will restart the whole learning process when the
	 * alphabet is refined.
	 * 
	 * @param dfa
	 *            the DFA based on which the traces were generated
	 * @param traces
	 *            the query results for traces to refine DFA.
	 * 
	 * @return the witness which contains the divider for the inconsistent
	 *         transition or the counterexample trace
	 */
	public Witness refineCandidate(DFA dfa, List<QueryTrace> traces) throws InterruptedException {
		manager.checkProgress();
		Map<Transition, QueryResult> resultMap = dispatchTraces(dfa, traces);
		// Find the first inconsistent transition
		Pair<Transition, QueryResult> result = findInconsistentTransition(dfa,
				resultMap);

		// No counterexample, return a null divider
		if (result == null) {
			return new Witness(Formula.TRUE, Action.epsilon);
		}

		QueryResult queryResult = result.second();

		TzuYuAction tzuyuSymbol = (TzuYuAction)result.first().action;

		if (queryResult.positiveSet.size() == 0) {
			StringConverter converter = new StringConverter(dfa);
			QueryTrace ctexample = queryResult.negativeSet.get(0);
			Trace counterexampleLString = converter
					.convertToNewString(ctexample);
			// The counterexample string returned by convertToNewString only
			// contains the prefix string that reach the inconsistent state,
			// So as a counterexample to return to L*, we need to append the
			// inconsistent action at the end.
			counterexampleLString.appendAtTail(tzuyuSymbol);
			return new Witness(counterexampleLString, tzuyuSymbol);
		} else if (queryResult.negativeSet.size() == 0) {
			StringConverter converter = new StringConverter(dfa);
			QueryTrace ctexample = queryResult.positiveSet.get(0);
			Trace counterexampleLString = converter
					.convertToNewString(ctexample);
			// The counterexample string returned by convertToNewString only
			// contains the prefix string that reach the inconsistent state,
			// So as a counterexample to return to L*, we need to append the
			// inconsistent action at the end.
			counterexampleLString.appendAtTail(tzuyuSymbol);
			return new Witness(counterexampleLString, tzuyuSymbol);
		} else {
			// Invoke LibSVM to refine the alphabet.
			Formula divider = classifier.candidateDivide(tzuyuSymbol,
					queryResult.positiveSet, queryResult.negativeSet,
					classInfoMap);

			return new Witness(divider, tzuyuSymbol);
		}

	}

	public Map<Transition, QueryResult> dispatchTraces(DFA dfa,
			List<QueryTrace> traces) {

		DFARunner runner = new DFARunner(dfa);

		for (QueryTrace trace : traces) {
			if (trace.query.isEpsilon() && (!trace.isAccepted())) {
				continue;
			}
			runner.runOldTrace(trace);
		}

		return runner.getStatesByTransition();
	}

	/**
	 * Find an inconsistent transition on the DFA and the inconsistent data
	 * states on the source state of the transition. There is an inconsistency
	 * when one of the following conditions happens:
	 * <ul>
	 * <li>there are both positive traces and negative traces on the source
	 * state of a transition.
	 * <li>negative traces for a transition whose target state is an accepting
	 * state.
	 * <li>positive traces for a transition whose target state is a rejecting
	 * state.
	 * </ul>
	 * We prefer the case (the first case) where both positive and negative
	 * traces exist to the last two cases since alphabet refinement makes TzuYu
	 * converge faster. That is to say we prefer TzuYu alphabet refinement to L*
	 * control refinement.
	 * 
	 * @param dfa
	 *            on which the inconsistent transition happens
	 * @param resultMap
	 *            the transition and its inconsistent states
	 * @return
	 */
	private Pair<Transition, QueryResult> findInconsistentTransition(DFA dfa,
			Map<Transition, QueryResult> resultMap) {
		// We first traverse the map to find the case that both positive and
		// negative traces exist for a transition in order to do TzuYu alphabet
		// refinement to L* control refinement.
		for (Transition transition : resultMap.keySet()) {
			QueryResult queryResult = resultMap.get(transition);
			int positiveSize = queryResult.positiveSet.size();
			int negativeSize = queryResult.negativeSet.size();
			if (positiveSize != 0 && negativeSize != 0) {
				return new Pair<Transition, QueryResult>(transition,
						queryResult);
			}
		}

		// If we cannot find the both positive and negative traces for a
		// transition
		// We try to find a case where there is counterexample traces to let L*
		// do
		// the control refinement.
		for (Transition transition : resultMap.keySet()) {
			QueryResult queryResult = resultMap.get(transition);
			int positiveSize = queryResult.positiveSet.size();
			int negativeSize = queryResult.negativeSet.size();
			if (positiveSize != 0) {
				if (!dfa.isAccepting(transition.target)) {
					// positive counterexample found
					return new Pair<Transition, QueryResult>(transition,
							queryResult);
				}
			} else if (negativeSize != 0) {
				if (dfa.isAccepting(transition.target)) {
					// negative counterexample found
					return new Pair<Transition, QueryResult>(transition,
							queryResult);
				}
			} else {
				continue;
			}
		}
		// There is no inconsistent transition, just return null.
		return null;

	}

	public void report(IReportHandler<TzuYuAlphabet> reporter) {
		reporter.getOutStream(OutputType.TZ_OUTPUT)
				.writeln("Total NO. of SVM Calls: " + getRefinementCount())
				.writeln("Total Time consumed by SVM: " + getTimeConsumed() + "ms");
	}

}
