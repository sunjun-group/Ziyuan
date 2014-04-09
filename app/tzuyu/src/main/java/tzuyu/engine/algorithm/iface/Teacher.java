package tzuyu.engine.algorithm.iface;

import lstar.LStarException;
import lstar.QueryException;

import org.apache.log4j.Logger;

import tzuyu.engine.iface.HasReport;
import tzuyu.engine.model.Trace;
import tzuyu.engine.model.dfa.Alphabet;
import tzuyu.engine.model.dfa.DFA;
import tzuyu.engine.model.exception.TzException;

/**
 * This is an abstract teacher, the actual LStar algorithm can extend this class
 * by overriding the two query functions.
 * 
 * @author Spencer Xiao
 * 
 */
public interface Teacher<A extends Alphabet> extends HasReport<A> {
	static final Logger logger = Logger.getRootLogger();
	/**
	 * If the @param str is accepted by the unknown DFA then return true; If the
	 * 
	 * @param str
	 *            is rejected by the unknown DFA then return false; Otherwise (
	 *            e.g., the system is not a DFA ), throw a
	 *            {@link QueryException}.
	 * 
	 * @param str
	 *            the string to be decided
	 */
	public boolean membershipQuery(Trace str) throws LStarException,
			InterruptedException, TzException;

	/**
	 * Whether the @param dfa is equal to the unknown DFA. If they are equal
	 * return a LString with length 0; If they are not equal, return a
	 * counterexample LString; Otherwise(e.g., the system is not a DFA) throw a
	 * {@link QueryException}.
	 * 
	 * @param dfa
	 * @return the counterexample string if the two are not equal or an empty
	 *         (not null) LString if the tow equal.
	 */
	public Trace candidateQuery(DFA dfa) throws LStarException,
			InterruptedException, TzException;

	public void setInitAlphabet(A sig);

}
