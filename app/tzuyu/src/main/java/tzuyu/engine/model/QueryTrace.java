package tzuyu.engine.model;

import java.util.Collections;
import java.util.List;

/**
 * A query trace is a particular execution trace of the sequence with specific
 * parameters for a query. The sequence is an extended serial of methods with a
 * particular set of parameters assigned to methods defined in the query.
 * 
 * @author Spencer Xiao
 * 
 */
public class QueryTrace {

	private final TVAnswer answer;
	public final Query query;
	private Sequence sequence;
	private List<Prestate> states;
	public final int lastActionIdx;

	public QueryTrace(Query q, Sequence seq, List<Prestate> ps,
			TVAnswer answer, int idx) {
		this.query = q;
		this.sequence = seq;
		this.states = ps;
		this.lastActionIdx = idx;
		this.answer = answer;
	}

	public boolean isRejected() {
		return answer == TVAnswer.Rejecting;
	}

	public boolean isAccepted() {
		return answer == TVAnswer.Accepting;
	}

	public boolean isUnknown() {
		return answer == TVAnswer.Unknown;
	}

	public final Prestate getLastState() {
		return states.get(states.size() - 1);
	}

	public List<Prestate> getStates() {
		return Collections.unmodifiableList(states);
	}

	public final Sequence getSequence() {
		return sequence;
	}

	/**
	 * Get the variable sequence that generates the parameter for the
	 * <code>varIdex</code> in the <code>stmtIdex</code> in the query.
	 * 
	 * @param queryIndex
	 *            the index of the action in the query, it should lies in
	 *            <code>[-1, query.size() - 1]</code>
	 * @param varIndex
	 * @return
	 */
	public Variable getVariableForStatement(int queryIndex, int varIndex) {
		int reverseQueryIndex = query.size() - 1 - queryIndex;
//		Tzuyu.getLog().debug(this, queryIndex, varIndex);
		return sequence.getVariableSequence(reverseQueryIndex, varIndex);
	}

	@Override
	public String toString() {
		return answer.toString() + ":" + query.toString() + ":" + states.size()
				+ ":" + lastActionIdx;
	}

	@Override
	public boolean equals(Object o) {
		if (o == this) {
			return true;
		}

		if (o instanceof QueryTrace) {
			QueryTrace obj = (QueryTrace) o;
			// Here we don't compare the sequence to save time.
			if (obj.answer == answer && obj.query.equals(query)
					&& obj.lastActionIdx == lastActionIdx) {
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

	@Override
	public int hashCode() {
		int hash = 31 * answer.hashCode() + 23 * query.hashCode()
				+ lastActionIdx;
		return hash;
	}

	public TzuYuAction getNextAction() {
		if (lastActionIdx + 1 == query.size()) {
			return null;
		} else {
			return query.getStatement(lastActionIdx + 1);
		}
	}

	/**
	 * Get the new query trace which contains only the prefix states of length
	 * <code>len</code>, the new query trace has the same query, sequences and
	 * answer with this, but have a different lastActionIdx.
	 * 
	 * @param len
	 * @return
	 */
	public QueryTrace getQueryTraceWithSubStates(int len) {
		List<Prestate> subStateList = states.subList(0, len + 1);
		return new QueryTrace(query, sequence, subStateList, answer, len - 1);
	}
	
	public Query getQuery() {
		return query;
	}
}
