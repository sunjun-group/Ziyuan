package tzuyu.engine.model.dfa;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import tzuyu.engine.iface.IPrintStream;
import tzuyu.engine.model.Action;
import tzuyu.engine.model.Trace;
import tzuyu.engine.utils.Randomness;
import tzuyu.engine.utils.dfa.DfaGraph;

public class DFA {
	public Alphabet<?> sigma;
	private int initStateID;
	// private IntList acceptingStates;
	private List<Integer> acceptingStates;
	private List<State> states;

	public DFA() {
		sigma = null;
		initStateID = 0;
		// acceptingStates = new IntList();
		acceptingStates = new ArrayList<Integer>();
		states = new ArrayList<State>();
	}

	/**
	 * add the state into the states of the DFA. if the state to be added
	 * already exists ( its id equals to the id of some state), just ignore it
	 * 
	 * @param state
	 * @return the index to which the state was added
	 */
	public int addState(State state) {
		for (int i = 0; i < states.size(); i++) {
			if (states.get(i).id.equals(state.id)) {
				return i;
			}
		}

		states.add(state);
		return states.size() - 1;
	}

	public int getStateID(String state) {
		for (int i = 0; i < states.size(); i++) {
			if (states.get(i).id.equals(state)) {
				return i;
			}
		}

		return -1;
	}

	public int getStateSize() {
		return states.size();
	}

	public State getState(int index) {
		return states.get(index);
	}

	public void setInitialState(int init) {
		initStateID = init;
	}

	public int getInitialState() {
		return this.initStateID;
	}

	public List<Integer> getAcceptingStates() {
		return Collections.unmodifiableList(acceptingStates);
	}

	public void addAcceptingState(int accepting) {
		boolean exists = acceptingStates.contains(accepting);

		if (!exists) {
			acceptingStates.add(accepting);
		}
	}

	public void print(IPrintStream out) {
		out.println("state#:" + states.size());
		out.println("initial state:<" + states.get(initStateID).id + ">");
		out.print("accepting state:{");
		for (int k = 0; k < acceptingStates.size(); k++) {
			out.print(states.get(acceptingStates.get(k)).id);
			if (acceptingStates.size() - k >= 2) {
				out.print(",");
			}
		}

		out.println("}");

		for (State state : states) {
			for (Transition tran : state.trans) {
				out.println("(" + state.id + "--"
						+ tran.action.getString() + "-->"
						+ states.get(tran.target).id + ")");
			}
		}
	}

	/**
	 * Generate size number of string randomly.
	 * 
	 * @param size
	 * @return
	 */
	public TracesPair randomWalk(int traceSize, int traceLength) {
		List<Trace> acceptingResult = new LinkedList<Trace>();
		HashSet<Trace> acceptingHashSet = new HashSet<Trace>(traceSize / 2);

		List<Trace> refusingResult = new LinkedList<Trace>();
		HashSet<Trace> refusingHashSet = new HashSet<Trace>(traceSize / 2);

		for (int i = 0; i < traceSize; i++) {
			Trace str = new Trace();
			int currentStateId = initStateID;
			boolean acceptingStateHit = true;
			for (int length = 0; length < traceLength; length++) {
				State currentState = states.get(currentStateId);
				int outDegree = currentState.trans.size();
				Random rander = new Random();
				int selectedTransitionID = rander.nextInt(outDegree);
				Transition transition = currentState.trans
						.get(selectedTransitionID);
				currentStateId = transition.target;
				// Here we just want to append the action to the tail of str.
				// For performance reason DO NOT CALL concatAtTail();
				str.appendAtTail(transition.action);
			}

			acceptingStateHit = acceptingStates.contains(currentStateId);
			if (acceptingStateHit && !acceptingHashSet.contains(str)) {
				acceptingResult.add(str);
				acceptingHashSet.add(str);
			} else if (!acceptingStateHit && !refusingResult.contains(str)) {
				refusingResult.add(str);
				refusingHashSet.add(str);
			}
		}

		return new TracesPair(acceptingResult, refusingResult);
	}

	/**
	 * This version of random walk generates traces in which only the first
	 * action is constructor and all the remaining actions are methods.
	 * 
	 * This version of random walks works for the second version of the query
	 * handler in which all constructors of the target class are treated as
	 * normal methods.
	 * 
	 * @param traceSize
	 * @param maxTraceLength
	 * @return
	 */
	public TracesPair randomWalk2(int traceSize, int maxTraceLength) {
		List<Trace> acceptingResult = new LinkedList<Trace>();
		HashSet<Trace> acceptingHashSet = new HashSet<Trace>(traceSize / 2);

		List<Trace> refusingResult = new LinkedList<Trace>();
		HashSet<Trace> refusingHashSet = new HashSet<Trace>(traceSize / 2);

		for (int i = 0; i < traceSize; i++) {
			Trace str = new Trace();

			int currentStateId = initStateID;

			boolean acceptingStateHit = true;

			for (int length = 0; length < maxTraceLength; length++) {
				// For the first action, we only need to execute one
				// constructor.
				// For the remaining actions, we are not allowed to execute
				// constructors
				Transition transition = selectTransition(currentStateId,
						length == 0);
				currentStateId = transition.target;
				// Here we just want to append the action to the tail of str.
				// For performance reason DO NOT CALL concatAtTail();
				str.appendAtTail(transition.action);
			}

			acceptingStateHit = acceptingStates.contains(currentStateId);
			if (acceptingStateHit && !acceptingHashSet.contains(str)) {
				acceptingResult.add(str);
				acceptingHashSet.add(str);
			} else if (!acceptingStateHit && !refusingResult.contains(str)) {
				refusingResult.add(str);
				refusingHashSet.add(str);
			}
		}

		return new TracesPair(acceptingResult, refusingResult);
	}

	/**
	 * Select randomly an outgoing transition in which the action is either a
	 * constructor or a normal method.
	 * 
	 * @param stateID
	 * @param useCtorTransition
	 * @return
	 */
	private Transition selectTransition(int stateID, boolean useCtorTransition) {
		State currentState = states.get(stateID);
		List<Transition> ctorTrans = new ArrayList<Transition>();
		List<Transition> methodTrans = new ArrayList<Transition>();
		for (Transition tran : currentState.trans) {
			if (tran.action.isConstructor()) {
				ctorTrans.add(tran);
			} else {
				methodTrans.add(tran);
			}
		}

		if (useCtorTransition && ctorTrans.size() > 0) {
			return Randomness.randomMember(ctorTrans);
		} else {
			return Randomness.randomMember(methodTrans);
		}
	}

	/**
	 * Run the input string on the DFA until the first error state is reached.
	 * 
	 * @param str
	 *            the input string
	 * @param len
	 *            maximum transition to run on the input string
	 * @return The first transition in the <code>len</code> prefix of the input
	 *         string that leads to an error state and the number of transitions
	 *         have been traversed (including the last transition). If the
	 *         <code>len</code> prefix of the input string is accepting, then
	 *         the last transition is the last transition in the prefix and the
	 *         number of traversed transitions is <code>len</code>. The return
	 *         value lies in <code>[0, len]</code>.
	 */
	public RunningResult runString(Trace str, int len) {
		int current = this.initStateID;

		int index = 0;

		Transition lastMatchedTran = null;
		for (; index < str.size(); index++) {
			Action currentAction = str.valueAt(index);
			List<Transition> transitions = states.get(current).trans;
			for (Transition transition : transitions) {
				if (transition.action.equals(currentAction)) {
					lastMatchedTran = transition;
					if (acceptingStates.contains(transition.target)) {
						if (index == len - 1) {
							return new RunningResult(transition, index);
						}
						current = transition.target;
						break;
					} else {
						return new RunningResult(transition, index);
					}
				}
			}
		}
		return new RunningResult(lastMatchedTran, index);
	}

	public DfaGraph toDfaGraph() {
		DfaGraph graph = new DfaGraph();
		graph.setInit(states.get(initStateID).id);
		// nodes
		for (int i = 0; i < states.size(); i++) {
			State state = states.get(i);
			if (acceptingStates.contains(i)) {
				graph.addNode(state.id, true);
			} else {
				graph.addNode(state.id, false);
			}
		}
		// edges
		for (State state : states) {
			for (Transition tran : state.trans) {
				graph.addEdge(state.id, states.get(tran.target).id,
						tran.action.toString());
			}
		}
		graph.commit();
		return graph;
	}

	public String createDotRepresentation() {

		StringBuffer result = new StringBuffer();
		result.append("digraph automaton {\n\tcenter=true;\n\tsize=\"9,11\";\n");

		// Draw the initial state transition
		result.append("\t" + "init" + " -> " + states.get(initStateID).id);
		result.append(" [label=\"init<>\"];\n");

		for (State state : states) {
			for (Transition tran : state.trans) {
				result.append("\t" + state.id + " -> "
						+ states.get(tran.target).id);
				result.append(" [label=\"");
				result.append(tran.action.toString());
				result.append("\"];\n");
			}
		}

		// Draw the initial state
		result.append("\t" + "init [shape=point];\n");
		for (int i = 0; i < states.size(); i++) {
			State state = states.get(i);
			if (acceptingStates.contains(i)) {
				result.append("\t" + state.id + " [label=\"" + state.id + "\""
						+ ", shape=doublecircle];\n");
			} else {
				result.append("\t" + state.id + " [label=\"" + state.id
						+ "\", shape=circle, style=filled, color=red];\n");
			}
		}

		result.append("}\n");
		return result.toString();
	}

	public boolean isAccepting(int state) {
		return acceptingStates.contains(state);
	}
	
	public List<State> getAllStates() {
		return states;
	}
	
	public Alphabet<?> getSigma() {
		return sigma;
	}
}
