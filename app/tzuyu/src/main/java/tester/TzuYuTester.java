package tester;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import lstar.ReportHandler;
import tzuyu.engine.TzProject;
import tzuyu.engine.iface.HasReport;
import tzuyu.engine.model.InputAndSuccessFlag;
import tzuyu.engine.model.Prestate;
import tzuyu.engine.model.Query;
import tzuyu.engine.model.QueryResult;
import tzuyu.engine.model.QueryTrace;
import tzuyu.engine.model.Sequence;
import tzuyu.engine.model.TVAnswer;
import tzuyu.engine.model.TzuYuAction;
import tzuyu.engine.model.TzuYuAlphabet;
import tzuyu.engine.model.TzuYuException;
import tzuyu.engine.model.VarIndex;
import tzuyu.engine.model.Variable;
import tzuyu.engine.store.IQueryTraceStore;
import tzuyu.engine.store.QueryTraceStoreFactory;
import tzuyu.engine.utils.Permutation;

public class TzuYuTester implements HasReport<TzuYuAlphabet>{
	private ParameterSelector selector;
	private IQueryTraceStore traceStore;
	private TzProject project;

	private HashSet<TzuYuAction> cachedUnkownResult = new HashSet<TzuYuAction>();

	public TzuYuTester() {
		selector = new ParameterSelector();
		traceStore = QueryTraceStoreFactory.createStore();
	}
	
	public void setProject(TzProject project) {
		this.project = project;
		selector.setProject(project);
	}

	/*  *//**
	 * Test the membership query, the testing strategy for membership test
	 * is that we use the largest normal prefix and then generate random
	 * parameters for the remaining statements. The query result contains the
	 * query traces correspond to the maximum normal prefix of the original
	 * query.
	 * 
	 * @param query
	 * @return
	 */
	/*
	 * private QueryResult memberTest2(Query query) { // Special handling of the
	 * epsilon query if (query.isEpsilon()) { InputAndSuccessFlag input =
	 * selector.selectCtor(Analytics.getTarget());
	 * 
	 * Sequence seq = Sequence.concatenate(input.sequences);
	 * 
	 * Variable initReceiver = new Variable(seq, seq.size() - 1, -1);
	 * seq.setReceiver(initReceiver);
	 * 
	 * if (input.success) { SequenceRuntime runtime =
	 * RuntimeExecutor.executeSequence(seq); if (runtime.isSuccessful()) {
	 * QueryTrace trace = new QueryTrace(query, seq, new ArrayList<Prestate>(),
	 * TVAnswer.Accepting, -1);
	 * 
	 * traceStore.addNormal(query, trace);
	 * 
	 * QueryResult result = new QueryResult(); result.positiveSet.add(trace);
	 * 
	 * return result; } else { // The parameter sequence failed due to some
	 * reason, // here we return a failed trace, since the found parameters //
	 * makes the trace fail. QueryTrace trace = new QueryTrace(query, seq, new
	 * ArrayList<Prestate>(), TVAnswer.Rejecting, -1);
	 * traceStore.addError(query, trace);
	 * 
	 * QueryResult result = new QueryResult(); result.negativeSet.add(trace);
	 * 
	 * return result; } } else { QueryTrace trace = new QueryTrace(query, seq,
	 * new ArrayList<Prestate>(), TVAnswer.Rejecting, -1);
	 * traceStore.addError(query, trace);
	 * 
	 * QueryResult result = new QueryResult(); result.negativeSet.add(trace);
	 * 
	 * return result; } } // Find the longest prefix already executed QueryTrace
	 * trace = findLargestPrefixTrace(query); // There is not good sequence for
	 * this query or the query // is confirmed to have a bad sequence, then we
	 * know the // answer is rejecting. if (trace.isRejected()) {
	 * List<QueryTrace> traces = traceStore.findError(trace.query);
	 * 
	 * QueryResult result = new QueryResult();
	 * result.negativeSet.addAll(traces);
	 * 
	 * return result; }
	 * 
	 * Query prefix = trace.query;
	 * 
	 * // Extend the prefix with the statements and parameters several times //
	 * for multiple tests. Query remainingQuery =
	 * query.getRemainingQuery(prefix);
	 * 
	 * int tracesPerQuery = TzConfiguration.TRACES_PER_QUERY;
	 * 
	 * Query currentQuery = prefix; for (int index = 0; index <
	 * remainingQuery.size(); index++) { QueryTrace currentTrace =
	 * findTrace(currentQuery);
	 * 
	 * Sequence currentSeq = currentTrace.getSequence();
	 * 
	 * Variable receiver = currentSeq.getReceiver();
	 * 
	 * TzuYuAction stmt = remainingQuery.getStatement(index);
	 * 
	 * Query newQuery = currentQuery.extend(stmt);
	 * 
	 * boolean noArgument = stmt.getAction().hasNoArguments(); // ensure the
	 * statement will be executed one boolean hasArgument = true;
	 * 
	 * for (int count = 0; count < tracesPerQuery && hasArgument; count++) { //
	 * Update the hasArgument flag hasArgument = !noArgument; // Select inputs
	 * for the parameters InputAndSuccessFlag inputs =
	 * selector.selectInputs(receiver, stmt); // Prepare input variables for
	 * guard condition evaluation List<Variable> inputVars = new
	 * ArrayList<Variable>(); if (inputs.success) { Sequence sequence =
	 * Sequence.concatenate(inputs.sequences);
	 * 
	 * List<VarIndex> vars = inputs.indices; for (int i = 0; i < vars.size();
	 * i++) { VarIndex idx = vars.get(i); Variable var = new Variable(sequence,
	 * idx.stmtIdx, idx.argIdx); inputVars.add(var); } // Evaluate the guard by
	 * generating runtime values for parameters ExecutionResult result =
	 * RuntimeExecutor .executeGuard(stmt, inputVars);
	 * 
	 * if (!result.isPassing()) {
	 * 
	 * Prestate prestate = Prestate.log(inputVars, result.getRuntime());
	 * List<Prestate> states = new ArrayList<Prestate>(
	 * currentTrace.getStates()); states.add(prestate); Sequence newSeq =
	 * sequence.extend(stmt, inputVars); newSeq.updateReceiver();
	 * 
	 * QueryTrace newTrace = new QueryTrace(newQuery, newSeq, states,
	 * TVAnswer.Unknown, newQuery.size() - 2); traceStore.addUnknown(newQuery,
	 * newTrace); // Try to find old values used and execute it to pass the
	 * guard // condition if (hasArgument) { testAfterAllFailed(currentTrace,
	 * receiver, stmt); }
	 * 
	 * } else { // Instrument to get the pre-state. Prestate prestate =
	 * Prestate.log(inputVars, result.getRuntime()); // Merge the pre-states
	 * List<Prestate> states = new ArrayList<Prestate>(
	 * currentTrace.getStates()); states.add(prestate); // Update the new
	 * sequence Sequence newSeq = sequence.extend(stmt, inputVars);
	 * newSeq.updateReceiver(); // Execute the statement to see whether is OK or
	 * throw exceptions boolean normal = RuntimeExecutor.executeStatement(stmt,
	 * result.getRuntime());
	 * 
	 * if (normal) { QueryTrace newTrace = new QueryTrace(newQuery, newSeq,
	 * states, TVAnswer.Accepting, newQuery.size() - 1);
	 * traceStore.addNormal(newQuery, newTrace); } else { QueryTrace newTrace =
	 * new QueryTrace(newQuery, newSeq, states, TVAnswer.Rejecting,
	 * newQuery.size() - 2); traceStore.addError(newQuery, newTrace); } } } else
	 * { // TODO how about we cannot select inputs for the parameters throw new
	 * TzuYuException("Cannot select inputs for the parameters"); } }
	 * 
	 * List<QueryTrace> normalTraces = traceStore.findNormal(newQuery);
	 * List<QueryTrace> errorTraces = traceStore.findError(newQuery);
	 * List<QueryTrace> unknownTraces = traceStore.findUnknown(newQuery);
	 * 
	 * QueryResult queryResult = new QueryResult();
	 * queryResult.positiveSet.addAll(normalTraces);
	 * queryResult.negativeSet.addAll(errorTraces);
	 * queryResult.unknownSet.addAll(unknownTraces);
	 * 
	 * if (!queryResult.isAccepting()) { // Failed in the intermediate
	 * statements, // We return this successfully executed prefix and the first
	 * failed // statement return queryResult; } else {// All sequences are
	 * normal execution currentQuery = newQuery; continue; } }
	 * 
	 * List<QueryTrace> normalTraces = traceStore.findNormal(query);
	 * List<QueryTrace> errorTraces = traceStore.findError(query);
	 * List<QueryTrace> unknownTraces = traceStore.findUnknown(query);
	 * 
	 * QueryResult result = new QueryResult();
	 * 
	 * result.positiveSet.addAll(normalTraces);
	 * result.negativeSet.addAll(errorTraces);
	 * result.unknownSet.addAll(unknownTraces); return result; }
	 */
	/**
	 * We test the membership query starts from the beginning to the end. But
	 * this cause the problem of non convergence problem. Since for some query
	 * the same action may fail in one trace and it may succeed in another
	 * trace.
	 * 
	 * @param query
	 * @return
	 */
	public QueryResult memberTest(Query query) {
		Query ctorQuery = Query.emptyQuery();
		InputAndSuccessFlag input = selector.selectCtor(ensureProject().getTarget());
		Sequence ctorSeq = Sequence.concatenate(input.sequences);
		// We can select a parameter for the constructor
		if (input.success) {
			Variable initReceiver = new Variable(ctorSeq, ctorSeq.size() - 1,
					-1);
			ctorSeq.setReceiver(initReceiver);

			SequenceRuntime runtime = RuntimeExecutor.executeSequence(ctorSeq);
			// The selected parameter sequence works normally.
			if (runtime.isSuccessful()) {
				QueryTrace trace = new QueryTrace(ctorQuery, ctorSeq,
						new ArrayList<Prestate>(), TVAnswer.Accepting, -1);
				traceStore.addNormal(ctorQuery, trace);
			} else {
				// The select parameter sequence throws exceptions.
				QueryTrace trace = new QueryTrace(ctorQuery, ctorSeq,
						new ArrayList<Prestate>(), TVAnswer.Rejecting, -1);
				traceStore.addError(ctorQuery, trace);
				QueryResult result = new QueryResult();
				result.negativeSet.add(trace);
				return result;
			}
		} else {
			// We cannot find a suitable parameter for the constructor
			QueryTrace trace = new QueryTrace(ctorQuery, ctorSeq,
					new ArrayList<Prestate>(), TVAnswer.Rejecting, -1);
			traceStore.addError(ctorQuery, trace);
			QueryResult result = new QueryResult();
			result.negativeSet.add(trace);
			return result;
		}

		Query currentQuery = ctorQuery;
		int tracesPerQuery = project.getConfiguration().getTestsPerQuery();
		for (int index = 0; index < query.size(); index++) {
			QueryTrace currentTrace = findTrace(currentQuery);
			if (!currentTrace.isAccepted()) {
				List<QueryTrace> errorTraces = traceStore
						.findError(currentQuery);
				QueryResult result = new QueryResult();
				result.negativeSet.addAll(errorTraces);
				return result;
			}

			// Get the current sequence
			Sequence currentSeq = currentTrace.getSequence();

			Variable receiver = currentSeq.getReceiver();

			// Get the statement to be executed and its corresponding alphabet
			TzuYuAction stmt = query.getStatement(index);

			Query newQuery = currentQuery.extend(stmt);
			// no argument flag is true if the statement contains no arguments
			// except the receiver parameter. In this case we don't need to
			// generate more than one trace for the query.
			boolean noArgument = stmt.getAction().hasNoArguments();
			// We need to execute the query at least once so, initialize it to
			// true
			boolean hasArgument = true;

			for (int count = 0; count < tracesPerQuery && hasArgument; count++) {
				// Update the flag
				hasArgument = !noArgument;
				// Select new created inputs for the statement so as to cover
				// more test inputs
				InputAndSuccessFlag inputs = selector.selectNewInputs(receiver,
						stmt);
				// Prepare input variables
				List<Variable> inputVars = new ArrayList<Variable>();
				if (inputs.success) {
					Sequence sequence = Sequence.concatenate(inputs.sequences);

					List<VarIndex> vars = inputs.indices;
					for (int i = 0; i < vars.size(); i++) {
						VarIndex idx = vars.get(i);
						Variable var = new Variable(sequence, idx.stmtIdx,
								idx.argIdx);
						inputVars.add(var);
					}
					// Evaluate the guard by generating runtime values for
					// parameters
					ExecutionResult rslt = RuntimeExecutor.executeGuard(stmt,
							inputVars);

					if (rslt.isPassing()) {
						Prestate prestate = Prestate.log(inputVars,
								rslt.getRuntime(), ensureProject());

						List<Prestate> states = new ArrayList<Prestate>(
								currentTrace.getStates());
						states.add(prestate);

						Sequence newSeq = sequence.extend(stmt, inputVars);
						newSeq.updateReceiver();

						boolean normal = RuntimeExecutor.executeStatement(stmt,
								rslt.getRuntime());

						if (normal) {
							QueryTrace newTrace = new QueryTrace(newQuery,
									newSeq, states, TVAnswer.Accepting,
									newQuery.size() - 1);
							traceStore.addNormal(newQuery, newTrace);
						} else {
							QueryTrace newTrace = new QueryTrace(newQuery,
									newSeq, states, TVAnswer.Rejecting,
									newQuery.size() - 2);
							traceStore.addError(newQuery, newTrace);
						}
					} else {
						// The failed execution may due to the unsuccessfully
						// generation
						// of the input variables, in this case we cannot
						// instrument the
						// Pre-state.
						Prestate prestate = Prestate.log(inputVars,
								rslt.getRuntime(), project);
						List<Prestate> states = new ArrayList<Prestate>(
								currentTrace.getStates());
						states.add(prestate);

						Sequence newSeq = sequence.extend(stmt, inputVars);
						newSeq.updateReceiver();

						QueryTrace newTrace = new QueryTrace(newQuery, newSeq,
								states, TVAnswer.Unknown, newQuery.size() - 2);

						traceStore.addUnknown(newQuery, newTrace);
						// If the guard test failed, we ignore the input and try
						// to
						// generate another parameters and execute it to pass
						// the condition
						if (hasArgument) {
							testAfterAllFailed(currentTrace, receiver, stmt);
						}
					}
				} else {
					// TODO how about we cannot select inputs for the parameters
					throw new TzuYuException(
							"Cannot select inputs for the parameters");
				}
			}
			// We have generated <code>count</code> number of inputs for the
			// statement, then we would check whether the execution result of
			// these traces succeed.
			List<QueryTrace> normalTraces = traceStore.findNormal(newQuery);
			List<QueryTrace> errorTraces = traceStore.findError(newQuery);
			List<QueryTrace> unknownTraces = traceStore.findUnknown(newQuery);

			QueryResult queryResult = new QueryResult();
			// After the accumulation of a few iterations, the traces for one
			// query may become amazing large, for
			queryResult.positiveSet.addAll(normalTraces);
			queryResult.negativeSet.addAll(errorTraces);
			queryResult.unknownSet.addAll(unknownTraces);

			if (!queryResult.isAccepting()) {
				return queryResult;
			} else {
				currentQuery = newQuery;
				continue;
			}
		}
		// If we reach here it means the executions of all the prefix of this
		// query including itself are OK.
		List<QueryTrace> normalTraces = traceStore.findNormal(query);
		List<QueryTrace> errorTraces = traceStore.findError(query);
		List<QueryTrace> unknownTraces = traceStore.findUnknown(query);

		QueryResult result = new QueryResult();

		result.positiveSet.addAll(normalTraces);
		result.negativeSet.addAll(errorTraces);
		result.unknownSet.addAll(unknownTraces);

		return result;

	}

	/**
	 * Find the a longest sequence and its corresponding query which corresponds
	 * to the longest prefix of the given query. The trace could be rejecting or
	 * accepting, but should not be refine.
	 * 
	 * @param query
	 *            the query for which to find the prefix and sequence.
	 * @return a prefix trace of the query and the its corresponding sequence.
	 */

	/*
	 * private QueryTrace findLargestPrefixTrace(Query query) { Query
	 * currentQuery = query; Query prefix = currentQuery.getImmediatePrefix();
	 * while (prefix.size() >= 0) { QueryTrace trace =
	 * traceStore.findNormalRandomly(prefix); if (trace != null) { return trace;
	 * } // Try to find the bad sequence for the prefix trace =
	 * traceStore.findErrorRandomly(prefix); if (trace != null) { // the prefix
	 * corresponds to a bad sequence, // a null value representing there is no
	 * good sequence // for the longest prefix return trace; } currentQuery =
	 * prefix; prefix = currentQuery.getImmediatePrefix(); }
	 * 
	 * throw new TzuYuException("prefix query cannot be in conflicting states");
	 * }
	 */

	/**
	 * Find corresponding query trace for the query. The query trace could be a
	 * good trace or an error trace if there is not good trace for the query.
	 * 
	 * @param query
	 * @return
	 */
	private QueryTrace findTrace(Query query) {
		QueryTrace trace = traceStore.findNormalRandomly(query);
		if (trace != null) {
			return trace;
		}

		trace = traceStore.findErrorRandomly(query);
		if (trace != null) {
			return trace;
		}

		throw new TzuYuException("At this postion, a trace for the "
				+ "query cannot be in conflicting states");
	}

	/**
	 * Test a single query with several membership queries generated by L*'s
	 * equivalence query. The testing strategy is that we don't reuse any
	 * existing prefix such that we can discover more test cases. The generated
	 * query result contains only the maximum successfully executed prefix until
	 * a failure occurred.
	 * 
	 * @param query
	 * @return A query trace corresponds to the maximum successfully executed
	 *         prefix of the query.
	 */
	public QueryResult candidateTest(Query query) {
		// First construct a receiver sequence
		Query ctorQuery = Query.emptyQuery();
		InputAndSuccessFlag ctor = selector.selectCtor(ensureProject().getTarget());

		Sequence seq = Sequence.concatenate(ctor.sequences);

		Variable initReceiver = new Variable(seq, seq.size() - 1, -1);
		seq.setReceiver(initReceiver);

		// Handle the result of input selection
		if (!ctor.success) {
			QueryTrace trace = new QueryTrace(ctorQuery, seq,
					new ArrayList<Prestate>(), TVAnswer.Rejecting, -1);
			traceStore.addError(ctorQuery, trace);

			QueryResult result = new QueryResult();
			result.negativeSet.add(trace);

			return result;
		} else {
			SequenceRuntime runtime = RuntimeExecutor.executeSequence(seq);
			if (!runtime.isSuccessful()) {
				QueryTrace trace = new QueryTrace(ctorQuery, seq,
						new ArrayList<Prestate>(), TVAnswer.Rejecting, -1);
				traceStore.addError(ctorQuery, trace);

				QueryResult result = new QueryResult();
				result.negativeSet.add(trace);

				return result;
			} else {
				QueryTrace trace = new QueryTrace(ctorQuery, seq,
						new ArrayList<Prestate>(), TVAnswer.Accepting, -1);

				traceStore.addNormal(ctorQuery, trace);
			}
		}

		Query currentQuery = ctorQuery;

		int tracesPerQuery = project.getConfiguration().getTestsPerQuery();

		// For candidate query we start from the beginning to the end,
		// not reuse the largest prefix as does in memberTest
		for (int index = 0; index < query.size(); index++) {
			QueryTrace currentTrace = findTrace(currentQuery);

			if (!currentTrace.isAccepted()) {
				List<QueryTrace> errorTraces = traceStore
						.findError(currentTrace.query);

				QueryResult result = new QueryResult();
				result.negativeSet.addAll(errorTraces);

				return result;
			}

			// Get the current sequence
			Sequence currentSeq = currentTrace.getSequence();

			Variable receiver = currentSeq.getReceiver();

			// Get the statement to be executed and its corresponding alphabet
			TzuYuAction stmt = query.getStatement(index);

			Query newQuery = currentQuery.extend(stmt);
			// no argument flag is true if the statement contains no arguments
			// except the receiver parameter. In this case we don't need to
			// generate more than one trace for the query.
			boolean noArgument = stmt.getAction().hasNoArguments();
			// We need to execute the query at least once so, initialize it to
			// true
			boolean hasArgument = true;

			for (int count = 0; count < tracesPerQuery && hasArgument; count++) {
				// Update the flag
				hasArgument = !noArgument;
				// Select new created inputs for the statement so as to cover
				// more test inputs
				InputAndSuccessFlag inputs = selector.selectNewInputs(receiver,
						stmt);
				// Prepare input variables
				List<Variable> inputVars = new ArrayList<Variable>();
				if (inputs.success) {
					Sequence sequence = Sequence.concatenate(inputs.sequences);

					List<VarIndex> vars = inputs.indices;
					for (int i = 0; i < vars.size(); i++) {
						VarIndex idx = vars.get(i);
						Variable var = new Variable(sequence, idx.stmtIdx,
								idx.argIdx);
						inputVars.add(var);
					}
					// Evaluate the guard by generating runtime values for
					// parameters
					ExecutionResult rslt = RuntimeExecutor.executeGuard(stmt,
							inputVars);

					if (rslt.isPassing()) {
						Prestate prestate = Prestate.log(inputVars,
								rslt.getRuntime(), ensureProject());

						List<Prestate> states = new ArrayList<Prestate>(
								currentTrace.getStates());
						states.add(prestate);

						Sequence newSeq = sequence.extend(stmt, inputVars);
						newSeq.updateReceiver();

						boolean normal = RuntimeExecutor.executeStatement(stmt,
								rslt.getRuntime());

						if (normal) {
							QueryTrace newTrace = new QueryTrace(newQuery,
									newSeq, states, TVAnswer.Accepting,
									newQuery.size() - 1);
							traceStore.addNormal(newQuery, newTrace);
						} else {
							QueryTrace newTrace = new QueryTrace(newQuery,
									newSeq, states, TVAnswer.Rejecting,
									newQuery.size() - 2);
							traceStore.addError(newQuery, newTrace);
						}
					} else {
						// The failed execution may due to the unsuccessfully
						// generation
						// of the input variables, in this case we cannot
						// instrument the
						// Prestate.
						Prestate prestate = Prestate.log(inputVars,
								rslt.getRuntime(), project);
						List<Prestate> states = new ArrayList<Prestate>(
								currentTrace.getStates());
						states.add(prestate);

						Sequence newSeq = sequence.extend(stmt, inputVars);
						newSeq.updateReceiver();

						QueryTrace newTrace = new QueryTrace(newQuery, newSeq,
								states, TVAnswer.Unknown, newQuery.size() - 2);

						traceStore.addUnknown(newQuery, newTrace);
						// If the guard test failed, we ignore the input and try
						// to
						// generate another parameters and execute it to pass
						// the condition
						if (hasArgument) {
							testAfterAllFailed(currentTrace, receiver, stmt);
						}
					}
				} else {
					// TODO how about we cannot select inputs for the parameters
					throw new TzuYuException(
							"Cannot select inputs for the parameters");
				}
			}
			// We have generated <code>count</code> number of inputs for the
			// statement, then we would check whether the execution result of
			// these traces succeed.
			List<QueryTrace> normalTraces = traceStore.findNormal(newQuery);
			List<QueryTrace> errorTraces = traceStore.findError(newQuery);
			List<QueryTrace> unknownTraces = traceStore.findUnknown(newQuery);

			QueryResult queryResult = new QueryResult();
			// After the accumulation of a few iterations, the traces for one
			// query may become amazing large, for
			queryResult.positiveSet.addAll(normalTraces);
			queryResult.negativeSet.addAll(errorTraces);
			queryResult.unknownSet.addAll(unknownTraces);

			if (!queryResult.isAccepting()) {
				return queryResult;
			} else {
				currentQuery = newQuery;
				continue;
			}
		}
		// If we reach here it means the executions of all the prefix of this
		// query including itself are OK.
		List<QueryTrace> normalTraces = traceStore.findNormal(query);
		List<QueryTrace> errorTraces = traceStore.findError(query);
		List<QueryTrace> unknownTraces = traceStore.findUnknown(query);

		QueryResult result = new QueryResult();

		result.positiveSet.addAll(normalTraces);
		result.negativeSet.addAll(errorTraces);
		result.unknownSet.addAll(unknownTraces);

		return result;
	}

	/**
	 * If the randomly generated parameters cannot be
	 * 
	 * @param currentTrace
	 * @param receiver
	 * @param stmt
	 */
	public void testAfterAllFailed(QueryTrace currentTrace, Variable receiver,
			TzuYuAction stmt) {

		Query newQuery = currentTrace.query.extend(stmt);
		List<Integer> inputSizes = selector.getInputSizes(stmt);

		Permutation permutation = new Permutation(inputSizes, true);
		while (permutation.hasNext()) {
			List<Integer> argIndex = permutation.next();

			InputAndSuccessFlag inputs = selector.getInputsAfterFailed(
					receiver, stmt, argIndex);
			// Prepare input variables
			List<Variable> inputVars = new ArrayList<Variable>();
			if (inputs.success) {
				Sequence sequence = Sequence.concatenate(inputs.sequences);

				List<VarIndex> vars = inputs.indices;
				for (int i = 0; i < vars.size(); i++) {
					VarIndex idx = vars.get(i);
					Variable var = new Variable(sequence, idx.stmtIdx,
							idx.argIdx);
					inputVars.add(var);
				}
				// Evaluate the guard by generating runtime values for
				// parameters
				ExecutionResult rslt = RuntimeExecutor.executeGuard(stmt,
						inputVars);

				if (rslt.isPassing()) {
					Prestate prestate = Prestate.log(inputVars,
							rslt.getRuntime(), ensureProject());

					List<Prestate> states = new ArrayList<Prestate>(
							currentTrace.getStates());
					states.add(prestate);

					Sequence newSeq = sequence.extend(stmt, inputVars);
					newSeq.updateReceiver();

					boolean normal = RuntimeExecutor.executeStatement(stmt,
							rslt.getRuntime());

					if (normal) {
						QueryTrace newTrace = new QueryTrace(newQuery, newSeq,
								states, TVAnswer.Accepting, newQuery.size() - 1);

						traceStore.addNormal(newQuery, newTrace);
						// Find one and then exit, don't need to do other
						// testings
						return;
					} else {
						QueryTrace newTrace = new QueryTrace(newQuery, newSeq,
								states, TVAnswer.Rejecting, newQuery.size() - 2);

						traceStore.addError(newQuery, newTrace);
						// Find one and then exit, don't need to do other
						// testings
						return;
					}
				} else {
					Prestate prestate = Prestate.log(inputVars,
							rslt.getRuntime(), project);
					List<Prestate> states = new ArrayList<Prestate>(
							currentTrace.getStates());
					states.add(prestate);
					Sequence newSeq = sequence.extend(stmt, inputVars);
					newSeq.updateReceiver();

					QueryTrace newTrace = new QueryTrace(newQuery, newSeq,
							states, TVAnswer.Unknown, newQuery.size() - 2);
					traceStore.addUnknown(newQuery, newTrace);
					continue;
				}
			} else {
				return;
			}
		}
	}

	/**
	 * Confirm whether the transition with no parameters passing its guard
	 * condition in the unknown trace has some failed test traces which has
	 * parameters passing the guard but failed after executing the action.
	 * 
	 * @param queryTrace
	 *            it must be an unknown trace.
	 * @return
	 */
	public boolean confirmWishfulThinking(QueryTrace queryTrace) {
		// Find the transition on which the guard testing failed.
		int lastActionIndex = queryTrace.lastActionIdx;

		if (queryTrace.isAccepted()) {
			lastActionIndex = lastActionIndex - 1;
		}

		TzuYuAction stmt = queryTrace.getNextAction();
		if (cachedUnkownResult.contains(stmt)) {
			return false;
		} else {
			// Find in the error traces in the history store to confirm whether
			// the same transition has parameters but failed after its action.
			boolean found = traceStore
					.findFailedEvidenceForUnknownTransition(stmt);
			if (found) {
				cachedUnkownResult.add(stmt);
			}

			// If we cannot find such a transition then we return true, which
			// means
			// the transition is OK according to our wishful thinking; If such a
			// transition exists in the error traces, we must return false,
			// which
			// means the wishful thinking does not work.
			return !found;
		}
	}

	private TzProject ensureProject() {
		if (project == null) {
			throw new TzuYuException("Tzuyu project not set for tester");
		}
		return project;
	}

	@Override
	public void report(ReportHandler<TzuYuAlphabet> reporter) {
		// TODO Auto-generated method stub
	}
}
