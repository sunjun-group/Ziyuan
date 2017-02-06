package refiner;

import java.util.ArrayList;
import java.util.List;

import tester.RuntimeExecutor;
import tzuyu.engine.model.Prestate;
import tzuyu.engine.model.QueryTrace;
import tzuyu.engine.model.Sequence;
import tzuyu.engine.model.TzuYuAction;
import tzuyu.engine.model.VarIndex;
import tzuyu.engine.model.Variable;
import tzuyu.engine.utils.Pair;
import tzuyu.engine.utils.Randomness;

/**
 * The preprocessor is responsible for the pre-processing of the inconsistent
 * data states before giving them as the input to the SVM. The processor does
 * two kinds of pre-processing:
 * <ul>
 * <li>Check whether the input values really influence the correctly of the
 * traces in order to eliminate the generation of invalid dividers on such input
 * variables.
 * <li>Feature Scaling. Normalize the numerical values for categorical variables
 * with other numerical variables with potentially large values compared with
 * the numerical values of categorical variables.
 * <li>To balance the positive and negative sample. If one of the two samples
 * have much less number of elements than the other, we need to duplicate the
 * samples with smaller size such that the input samples are more balanced.
 * </ul>
 * 
 * @author Spencer Xiao
 * 
 */
public class SVMPreprocessor {

	/**
	 * Balance the training sets by scaling up the smaller set with data
	 * randomly selected from the smaller set.
	 * 
	 * @param positive
	 * @param negative
	 * @return
	 */
	public static Pair<List<Prestate>, List<Prestate>> balanceTraningSet(
			List<Prestate> positive, List<Prestate> negative) {

		List<Prestate> posResult = new ArrayList<Prestate>();
		// Add the original data
		posResult.addAll(positive);
		List<Prestate> negResult = new ArrayList<Prestate>();
		// Add the original
		negResult.addAll(negative);

		// identify which is bigger
		int scaleSize = positive.size() - negative.size();
		boolean isPositiveBigger = scaleSize >= 0;
		int size = Math.abs(scaleSize);
		// scale the smaller one by randomly duplicating data from itself
		for (int index = 0; index < size; index++) {
			if (isPositiveBigger) {
				negResult.add(Randomness.randomMember(negative));
			} else {
				posResult.add(Randomness.randomMember(positive));
			}
		}

		return new Pair<List<Prestate>, List<Prestate>>(posResult, negResult);
	}

	/**
	 * Check the parameters are relevant to the execution result (exceptions or
	 * normal) or not by returning a list of boolean indicating the i-th
	 * argument is relevant to the result if it is true; irrelevant to the
	 * result if it is false.
	 * 
	 * @param stmt
	 * @param positive
	 * @param negative
	 * @return
	 */
	public static List<Boolean> checkParametersRelevance(TzuYuAction stmt,
			List<QueryTrace> positive, List<QueryTrace> negative) {
		List<Boolean> result = new ArrayList<Boolean>();

		List<Class<?>> paramTypes = stmt.getInputTypes();
		// For instance method which has a receiver,
		// the receiver parameter is valid.
		int receiverIndex = 0;
		if (!stmt.getAction().isStatic()) {
			receiverIndex = 1;
			result.add(true);
		}

		int argSize = paramTypes.size();
		if (argSize == 0) {
			return result;
		}

		/*
		 * For each arg, check if the different value of it in positive/negative
		 * traces affects on the executed result
		 * if yes, mean relevant.
		 * The checking based on trying.
		 * 
		 */
		//iterate remain indexes (mean all other parameter excluding receivers)
		for (int argIndex = receiverIndex; argIndex < argSize; argIndex++) {
			// Testing positive traces with relevant parameters from negative
			// traces
			boolean irrelevant = true;
			irrelevant = checkRelevantForTraces(stmt, negative,
					positive, argSize, argIndex, true);

			if (!irrelevant) {
				result.add(true);
				continue;
			}
			// irrelevant == true
			// Testing negative traces with relevant parameters from positive
			// traces
			irrelevant = checkRelevantForTraces(stmt, positive, negative,
					argSize, argIndex, false);

			result.add(!irrelevant);
		}

		return result;
	}

	private static boolean checkRelevantForTraces(TzuYuAction stmt,
			List<QueryTrace> opTraces, List<QueryTrace> mainTraces,
			int argSize, int argIndex, boolean expectedRtResult) {
		boolean irrelevant = true;
		for (QueryTrace opTrace : opTraces) {
			List<Variable> opTracesVars = getTraceVariables(argSize, opTrace);

			for (QueryTrace mainTrace : mainTraces) {
				int posStmtIndex = mainTrace.lastActionIdx;

				// Prepare the input arguments
				List<Sequence> sequences = new ArrayList<Sequence>(argSize);
				List<VarIndex> indices = new ArrayList<VarIndex>(argSize);
				int totStmts = 0;

				for (int index = 0; index < argSize; index++) {
					Variable input = null;
					// use the positive trace input
					if (index == argIndex) {
						input = opTracesVars.get(index);
					} else {
						input = mainTrace.getVariableForStatement(
								posStmtIndex + 1, index);
					}

					indices.add(new VarIndex(input.getStmtIdx() + totStmts,
							input.getArgIdx()));
					sequences.add(input.owner);
					totStmts += input.owner.size();
				}

				Sequence newSeq = Sequence.concatenate(sequences);

				List<Variable> newInputVars = new ArrayList<Variable>(
						argSize);
				for (int index = 0; index < argSize; index++) {
					VarIndex vIdx = indices.get(index);
					newInputVars.add(new Variable(newSeq,
							vIdx.getStmtIdx(), vIdx.getArgIdx()));
				}
				// Execute the statement with new relevant inputs from
				// positive trace
				// and other inputs from negative trace. So if the execution
				// result is
				// Ok(normal) then which means input does influence the
				// execution.
				boolean normal = RuntimeExecutor
						.execute(stmt, newInputVars);

				if (normal == expectedRtResult) {
					// If the new trace trace conforms with positive trace
					// even though
					// it uses relevant inputs from the negative trace, this
					// means the
					// found divider is valid on the input variables.
					continue;
				} else {
					// After changing the input parameters the execution
					// still conforms
					// to the result of negative trace, so the found divider
					// is invalid
					// on the input variables.
					irrelevant = false;
					break;
				}
			}

			if (!irrelevant) {
				break;
			}
		}
		return irrelevant;
	}

	private static List<Variable> getTraceVariables(int argSize,
			QueryTrace opTrace) {
		// The negStmtIndex should be in [-1, query.size() - 2]
		int negStmtIndex = opTrace.lastActionIdx;
		// Find all the referenced non-receiver input arguments

		List<Variable> tracesVars = new ArrayList<Variable>(argSize);
		for (int index = 0; index < argSize; index++) {
			// Get the variable for the next statement
			Variable newVar = opTrace.getVariableForStatement(
					negStmtIndex + 1, index);

			tracesVars.add(newVar);
		}
		return tracesVars;
	}
}
