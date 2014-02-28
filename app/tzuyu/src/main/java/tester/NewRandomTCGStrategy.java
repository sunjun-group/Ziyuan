package tester;

import java.util.ArrayList;
import java.util.List;

import tzuyu.engine.TzClass;
import tzuyu.engine.TzConfiguration;
import tzuyu.engine.model.Query;
import tzuyu.engine.model.Sequence;
import tzuyu.engine.model.Statement;
import tzuyu.engine.model.StatementKind;
import tzuyu.engine.model.TVAnswer;
import tzuyu.engine.model.TzuYuAction;
import tzuyu.engine.model.Variable;
import tzuyu.engine.model.exception.TzRuntimeException;
import tzuyu.engine.store.MethodParameterStore;
import tzuyu.engine.utils.Pair;
import tzuyu.engine.utils.Permutation;
import tzuyu.engine.utils.Randomness;

/**
 * 
 * The random generation strategy to generate parameters, the random style is
 * adopted from the Randoop approach except that the receiver of the statement
 * is the one after calling the previous statement.
 * 
 * @author Spencer Xiao
 * 
 */
public class NewRandomTCGStrategy implements ITCGStrategy {

	private IParameterSelector selector;
	private TestCaseStore store;

	private MethodParameterStore parameterStore;
	private TzClass project;
	private TzConfiguration config;

	public NewRandomTCGStrategy() {
		selector = new ParameterSelector(null);
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
		List<TestCase> tcs = new ArrayList<TestCase>();
		
		TestCase curTc = TestCase.epsilon;
		for (int i = 0; i < trace.size(); i++) {
			TzuYuAction stmt = trace.getStatement(i);
			Variable receiver = curTc.getReceiver();
			for (int j = 0; j < config.getTestsPerQuery(); i++) {
				TestCase newTc = generateNewTestcase(curTc, stmt, receiver);
			}
		}
		return tcs;
	}

	private TestCase generateNewTestcase(TestCase current, TzuYuAction statement,
			Variable receiver) {
		List<Variable> params = generateParamsForStatement(receiver, statement);
		params = fromRawVariables(params);
		
		// execute test
		ExecutionResult result = RuntimeExecutor.executeGuard(statement, params);
		
		Sequence newSeq = null;
		Statement stmt = null;
		if (result.isPassing()) {
			List<Object> runtime = result.getRuntime();
			boolean isNormal = RuntimeExecutor.executeStatement(statement,
					runtime);
			if (isNormal) {
				if (stmt.getAction().isConstructor()
						&& receiver != null) {
					TestCaseNode node = new TestCaseNode(
							TVAnswer.Rejecting, stmt, newSeq);
					TestCase newCase = current.extend(node);
					store.addTestCase(newCase);
				} else {
					TestCaseNode node = new TestCaseNode(
							TVAnswer.Accepting, stmt, newSeq);
					TestCase newCase = current.extend(node);
					List<TestCase> goodTraces = null;
					goodTraces.add(newCase);
					store.addTestCase(newCase);
				}
			} else {
				TestCaseNode node = new TestCaseNode(
						TVAnswer.Rejecting, stmt, newSeq);
				TestCase newCase = current.extend(node);
				store.addTestCase(newCase);
			}
		} else {
			TestCaseNode node = new TestCaseNode(TVAnswer.Unknown,
					stmt, newSeq);
			TestCase newCase = current.extend(node);
			store.addTestCase(newCase);
		}
		return current;
	}

	private List<Variable> fromRawVariables(List<Variable> params) {
		List<Variable> newParams = new ArrayList<Variable>(params.size());
		Sequence concatSeq = Sequence.concatenate(extractAllSequences(params));
		int stmtCount = 0;
		for (Variable param : params) {
			newParams.add(new Variable(concatSeq, param.getStmtIdx()
					+ stmtCount, param.getArgIdx()));
			stmtCount += param.owner.size(); 
		}
		return newParams;
	}

	private List<Sequence> extractAllSequences(List<Variable> params) {
		List<Sequence> result = new ArrayList<Sequence>(params.size());
		for (Variable param : params) {
			result.add(param.getOwner());
		}
		return result;
	}

	/**
	 * return List<Variable> !null
	 */
	private List<Variable> generateParamsForStatement(Variable receiver,
			TzuYuAction stmt) {

		// for normal methods we need to distinguish
		// the static method from instance method
		boolean isStatic = stmt.getAction().isStatic();

		List<Class<?>> paramTypes = stmt.getInputTypes();
		List<Variable> parameters = new ArrayList<Variable>(paramTypes.size());
		for (int index = 0; index < paramTypes.size(); index++) {
			Class<?> type = paramTypes.get(index);
			// The first argument of an instance method is the receiver object
			if (index == 0 && !isStatic) {
				// LLT: why receiver is only initialized if null here? 
				if (receiver == null) {
					receiver = selector.selectDefaultReceiver(ensureProject()
							.getTarget());
					parameters.add(receiver);
				} else {
					// Variable receiver = receiver.getReceiver();
					parameters.add(receiver);
				}
			} else {
				for (int i = 0; i < 3; i++) {
					Variable arg = null;
					int source = Randomness.nextRandomInt(3);

					if (source == 0) {
						arg = selector.selectNewVariable(type);
					} else if (source == 1) {
						arg = selector.selectCachedVariable(type);
					} else {
						arg = selector.selectExistingVariable(receiver, type);
					}
					// Variable arg = selector.selectNewParameter(type);
					SequenceRuntime result = RuntimeExecutor
							.executeSequence(arg.owner);
					if (!result.isSuccessful()) {
						continue;
					} else {
						parameters.add(arg);
						parameterStore.add(stmt.getAction(), index, arg);
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
			TzuYuAction statement) {
		List<Variable> variables = new ArrayList<Variable>();
		StatementKind stmt = statement.getAction();

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

	public List<TestCase> findFailedEvidenceForUnknownStatement(TzuYuAction stmt) {
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

	public List<TestCase> getAllGoodTestCases() {
		// Only return good traces
		return store.getAllGoodTestCases();
	}

	private TzClass ensureProject() {
		if (project == null) {
			throw new TzRuntimeException("Tzuyu project not set for tester");
		}
		return project;
	}
}
