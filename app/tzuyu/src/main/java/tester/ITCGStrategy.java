package tester;

import java.util.List;

import tzuyu.engine.TzClass;
import tzuyu.engine.model.Query;
import tzuyu.engine.model.Sequence;
import tzuyu.engine.model.TzuYuAction;
import tzuyu.engine.utils.Pair;

/**
 * 
 * The interface for test case generation strategies. There are different
 * strategies to generate parameters for a trace and return different number of
 * test cases. We can use randoop approach to generate parameters or use the
 * symbolic execution and many other methods.
 * 
 * @author Spencer Xiao
 * 
 */
public interface ITCGStrategy {

	/**
	 * Generate a list of executable test cases for the given trace. All three
	 * types of test cases may be generated i.e., the good traces, bad traces
	 * and unknown traces. For the traces that contains more than instance call
	 * to constructor, we treat it as an error traces.
	 */
	public List<TestCase> generate(Query trace);

	public List<TestCase> findFailedEvidenceForUnknownStatement(TzuYuAction stmt);

	public List<TestCase> getAllGeneratedTestCases();

	public void setProject(TzClass project);

	public Pair<List<Sequence>, List<Sequence>> getAllTestSequences(boolean pass,
			boolean fail);

	/**
	 * @param option (null, true, false) : 
	 * (all, passOnly, failOnly)
	 */
	public int countTcs(Boolean option);
}
