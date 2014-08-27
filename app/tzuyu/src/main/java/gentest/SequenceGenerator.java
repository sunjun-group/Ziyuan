/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package gentest;

import java.util.ArrayList;
import java.util.List;

import tester.ExecutionResult;
import tester.IParameterSelector;
import tester.ITCGStrategy;
import tester.ParameterSelector;
import tester.SequenceRuntime;
import tester.StatementExecutor;
import tester.TestCase;
import tester.TestCaseNode;
import tester.TestCaseStore;
import tzuyu.engine.TzClass;
import tzuyu.engine.TzConfiguration;
import tzuyu.engine.iface.ITzManager;
import tzuyu.engine.model.GuardedStatement;
import tzuyu.engine.model.Query;
import tzuyu.engine.model.RelativeNegativeIndex;
import tzuyu.engine.model.Sequence;
import tzuyu.engine.model.Statement;
import tzuyu.engine.model.StatementKind;
import tzuyu.engine.model.TVAnswer;
import tzuyu.engine.model.VarIndex;
import tzuyu.engine.model.Variable;
import tzuyu.engine.store.MethodParameterStore;
import tzuyu.engine.utils.Pair;
import tzuyu.engine.utils.Permutation;
import tzuyu.engine.utils.Randomness;

/**
 * @author LLT
 * 
 */
public class SequenceGenerator implements ITCGStrategy {
	private IParameterSelector selector;
	private TestCaseStore store;

	private MethodParameterStore parameterStore;
	private TzClass project;
	private TzConfiguration config;

	public SequenceGenerator(ITzManager<?> prjFactory) {
		selector = new ParameterSelector(prjFactory);
		store = new TestCaseStore();
		parameterStore = new MethodParameterStore();
	}

	public void setProject(TzClass project) {
		this.project = project;
		this.config = project.getConfiguration();
		selector.setProject(project);
	}

	/*
	 * This method generates parameter for a trace in which there is no
	 * constructor call, thus for each trace we add an implicit constructor
	 * call.
	 * 
	 * For each trace we start from the first statement to the last one without
	 * using the success prefix in order to generate more parameters for each
	 * statement.
	 * 
	 * @see tester.ITCGStrategy#generate(core.Query)
	 */
	public List<TestCase> generate(Query trace) {
		// Get the main object
		TestCase current = TestCase.epsilon;
		Variable receiver = null;
		int size = trace.size();
		for (int index = 0; index < size; index++) {
			GuardedStatement stmt = trace.getStatement(index);
			receiver = current.getReceiver();
			List<TestCase> goodTraces = new ArrayList<TestCase>();

			int currentSize = index + 1;
			Query currentQuery = trace.getSubQuery(currentSize);
			boolean noArgument = stmt.getMethod().hasNoArguments();
			// ensure the statement will be executed once
			boolean hasArg = true;
			// test sub queries.
			for (int count = 0; count < config.getTestsPerQuery() && hasArg; count++) {
				hasArg = !noArgument;
				List<Variable> rawParams = generateParamsForStatement(receiver,
						stmt);

				// ///////////////////////////////////////////////////////////////////
				// Start to process the returned raw parameters by updating the
				// sequences and the statement index
				List<Variable> parameters = new ArrayList<Variable>(
						rawParams.size());
				int stmtCount = 0;
				List<Sequence> sequences = new ArrayList<Sequence>();
				List<VarIndex> indices = new ArrayList<VarIndex>();
				
				for (Variable raw : rawParams) {
					indices.add(VarIndex.plus(raw.getVarIndex(), stmtCount));
					sequences.add(raw.owner);
					stmtCount += raw.owner.size();
				}

				Sequence newSeq = Sequence.concatenate(sequences);
				int seqSize = newSeq.size();

				for (VarIndex varIdx : indices) {
					parameters.add(new Variable(newSeq, varIdx));
				}
				// End of processing of raw parameters
				// ////////////////////////////////////////////////////////////////////

				// Then execute the parameters to decide whether the parameter
				// sequence
				// is valid. We would prefer the parameter sequences that could
				// pass the
				// guard condition test.
				ExecutionResult result = StatementExecutor.executeGuard(stmt,
						parameters);

				List<RelativeNegativeIndex> inputs = new ArrayList<RelativeNegativeIndex>();

				for (Variable var : parameters) {
					RelativeNegativeIndex relIdx = new RelativeNegativeIndex(
							-(seqSize - var.getStmtIdx()), var.getArgIdx());
					inputs.add(relIdx);
				}

				Statement statement = new Statement(stmt, inputs);
				TestCase newCase;
				if (result.isPassing()) {
					List<Object> runtime = result.getRuntime();
					boolean isNormal = StatementExecutor.executeStatement(stmt,
							runtime);
					if (isNormal) {
						if (stmt.getMethod().isConstructor()
								&& receiver != null) {
							TestCaseNode node = new TestCaseNode(
									TVAnswer.Rejecting, statement, newSeq);
							newCase = current.extend(node);
						} else {
							TestCaseNode node = new TestCaseNode(
									TVAnswer.Accepting, statement, newSeq);
							newCase = current.extend(node);
							goodTraces.add(newCase);
						}
					} else {
						TestCaseNode node = new TestCaseNode(
								TVAnswer.Rejecting, statement, newSeq);
						newCase = current.extend(node);
					}
				} else {
					TestCaseNode node = new TestCaseNode(TVAnswer.Unknown,
							statement, newSeq);
					newCase = current.extend(node);
				}
				store.addTestCase(newCase);
			}

			if (goodTraces.size() > 0) {
				// there is good traces to continue
				current = Randomness.randomMember(goodTraces);
			} else {
				// all traces are bad traces, we return
				List<TestCase> normalCases = store
						.selectNormalTraces(currentQuery);
				List<TestCase> errorCases = store
						.selectErrorTraces(currentQuery);
				List<TestCase> unknownCases = store
						.selectUnkownTraces(currentQuery);
				List<TestCase> result = new ArrayList<TestCase>();
				result.addAll(normalCases);
				result.addAll(errorCases);
				result.addAll(unknownCases);

				return result;
			}
		}

		List<TestCase> normalCases = store.selectNormalTraces(trace);
		List<TestCase> errorCases = store.selectErrorTraces(trace);
		List<TestCase> unknownCases = store.selectUnkownTraces(trace);
		List<TestCase> result = new ArrayList<TestCase>();
		result.addAll(normalCases);
		result.addAll(errorCases);
		result.addAll(unknownCases);

		return result;
	}

	private List<Variable> generateParamsForStatement(Variable receiver,
			GuardedStatement stmt) {

		// for normal methods we need to distinguish
		// the static method from instance method
		boolean isStatic = stmt.getMethod().isStatic();

		List<Class<?>> paramTypes = stmt.getInputTypes();
		List<Variable> parameters = new ArrayList<Variable>(paramTypes.size());
		for (int index = 0; index < paramTypes.size(); index++) {
			Class<?> type = paramTypes.get(index);
			// The first argument of an instance method is the receiver object
			if (index == 0 && !isStatic) {
				// initialize the receiver for a non-static method.
				if (receiver == null) {
					receiver = selector.selectDefaultReceiver(project
							.getTarget());
				} 
				parameters.add(receiver);
			} else {
				// try 3 times.
				for (int i = 0; i < 3; i++) {
					Variable arg = selector.selectVariable(receiver, type);
					// Variable arg = selector.selectNewParameter(type);
					SequenceRuntime result = StatementExecutor
							.executeSequence(arg.owner);
					if (!result.isSuccessful()) {
						continue;
					} else {
						parameters.add(arg);
						parameterStore.add(stmt.getMethod(), index, arg);
						break;
					}
				}
			}
		}
		// Test whether the parameter generation successes
		if (parameters.size() != paramTypes.size()) {
			parameters = getInputsAfterFailed(receiver, stmt);
		}
		return parameters;
	}

	/**
	 * Get a set of least used parameters for the statement which may not cause
	 * all the statement execution fail.
	 * 
	 * @param receiver
	 * @param statement
	 * @param argIdx
	 *            the list of argument index to choose.
	 * @return
	 */
	private List<Variable> getInputsAfterFailed(Variable receiver,
			GuardedStatement statement) {
		List<Variable> variables = new ArrayList<Variable>();
		StatementKind stmt = statement.getMethod();

		List<Class<?>> inputTypes = statement.getInputTypes();

		List<Integer> inputSizes = getInputSize(stmt);
		Permutation permutation = new Permutation(inputSizes, true);
		while (permutation.hasNext()) {

			List<Integer> argIdx = permutation.next();

			boolean isStatic = stmt.isStatic();

			for (int index = 0; index < inputTypes.size(); index++) {

				boolean isReceiver = (index == 0 && !isStatic);

				if (isReceiver) {
					variables.add(receiver);
				} else {
					Variable var = parameterStore.getLRU(stmt, index,
							argIdx.get(index));
					variables.add(var);
				}
			}
		}

		return variables;

	}

	/**
	 * Get a list of integers, each corresponds to the number of different
	 * arguments that have been generated for the parameter of the statement. If
	 * the statement is an instance method, the first element is always 1; If
	 * the statement is a class method (a.k.a. static method) the first element
	 * is the size of the first non-receiver parameter values.
	 * 
	 * @param stmt
	 * @return
	 */
	private List<Integer> getInputSize(StatementKind stmt) {
		List<Class<?>> inputTypes = stmt.getInputTypes();
		List<Integer> inputSizes = new ArrayList<Integer>();

		boolean isStatic = stmt.isStatic();

		for (int index = 0; index < inputTypes.size(); index++) {
			boolean isReceiver = (!isStatic && index == 0);
			if (isReceiver) {
				inputSizes.add(1);
			} else {
				int argSize = parameterStore.getParameterSize(stmt, index);
				inputSizes.add(argSize);
			}
		}
		return inputSizes;
	}

	public List<TestCase> findFailedEvidenceForUnknownStatement(GuardedStatement stmt) {
		return store.findFailedEvidence(stmt);
	}

	public List<TestCase> getAllGeneratedTestCases() {
		Pair<List<TestCase>, List<TestCase>> representatives = store
				.getRepresentativeForAllTestCases();

		List<TestCase> resultCases = new ArrayList<TestCase>();
		resultCases.addAll(representatives.first());
		resultCases.addAll(representatives.second());
		return resultCases;
	}

	@Override
	public Pair<List<TestCase>, List<TestCase>> getAllTestcases(boolean pass, boolean fail) {
		return store.getTestcases(pass, fail);
	}
	
}
