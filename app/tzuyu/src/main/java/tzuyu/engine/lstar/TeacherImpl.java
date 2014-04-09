/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package tzuyu.engine.lstar;

import java.util.ArrayList;
import java.util.List;

import lstar.IReportHandler;
import lstar.LStarException;
import lstar.LStarException.Type;
import refiner.Witness;
import tzuyu.engine.algorithm.iface.Refiner;
import tzuyu.engine.algorithm.iface.Teacher;
import tzuyu.engine.algorithm.iface.Tester;
import tzuyu.engine.bool.True;
import tzuyu.engine.iface.IPrintStream;
import tzuyu.engine.iface.ITzManager;
import tzuyu.engine.model.Formula;
import tzuyu.engine.model.Query;
import tzuyu.engine.model.QueryResult;
import tzuyu.engine.model.QueryTrace;
import tzuyu.engine.model.Trace;
import tzuyu.engine.model.TzuYuAction;
import tzuyu.engine.model.TzuYuAlphabet;
import tzuyu.engine.model.dfa.DFA;
import tzuyu.engine.model.dfa.TracesPair;
import tzuyu.engine.model.exception.ReportException;
import tzuyu.engine.model.exception.TzException;
import tzuyu.engine.model.exception.TzExceptionType;

/**
 * @author LLT extracted from QueryHandlerV2, and only keep the part that
 *         implements the Lstar teacher.
 */
public class TeacherImpl implements Teacher<TzuYuAlphabet> {
	protected int membershipCount = 0;
	protected int candidateCount = 1;
	protected int maxMemberSize = 0;

	protected Tester tester;
	protected Refiner<TzuYuAlphabet> refiner;
	// sigma should not changed inside teacher and always point to the same
	// entity as in learner. 
	protected TzuYuAlphabet sigma; 
	private IPrintStream outStream;

	public TeacherImpl(ITzManager<TzuYuAlphabet> tzFactory) {
		tester = tzFactory.getTester();
		refiner = tzFactory.getRefiner();
		outStream = tzFactory.getOutStream();
	}

	public void setInitAlphabet(TzuYuAlphabet sigma) {
		this.sigma = sigma;
		refiner.init(sigma);
		tester.setProject(sigma.getProject());
	}

	public boolean membershipQuery(Trace str) throws LStarException,
			InterruptedException, TzException {
		assert sigma != null : "Sigma in teacherImplV2 is not set!!";
		membershipCount++;
		// Update maximum membership query size
		if (str.size() > maxMemberSize) {
			maxMemberSize = str.size();
		}

		// Step 2: relay this query as membership query to the Oracle.
		Query query = new Query(str);

		QueryResult result = tester.test(query);

		if (query.isEpsilon()) {
			return true;
		}

		// Step 3: process the result from oracle membership query.
		if (result.positiveSet.size() == 0 && result.negativeSet.size() == 0) {
			// return the old value. Here we need to search the database
			// to find the counterpart. If there is no counterpart in the
			// database, we need to return true or false?

			List<QueryTrace> traces = result.unknownSet;

			QueryTrace queryTrace = traces.get(0);

			TzuYuAction stmt = queryTrace.getNextAction();
			boolean wishful = tester.confirmWishfulThinking(stmt);

			// Return true for unknown test trace as the wishful thinking, but
			// we
			// need to know which guard condition failed in case latter we find
			// a passing trace for this guard, then we should return true for
			// that
			// membership query.
			return wishful;
		} else if (result.positiveSet.size() == 0) {
			return false;
		} else if (result.negativeSet.size() == 0) {
			return true;
		} else {
			Formula divider = refiner.refineMembership(result);
			if (divider == null || divider.equals(Formula.FALSE)) {
				throw new TzException(TzExceptionType.CANNOT_FIND_DEVIDER);
			}
			QueryTrace trace = result.negativeSet.get(0);

			TzuYuAction action = trace.query
					.getStatement(trace.lastActionIdx + 1);
			TzuYuAlphabet newSigma = sigma.refine(divider, action);

			if (newSigma.equals(sigma)) {
				return false;
			} else {
				/* should stop learner in learner itself. */
				// At this point, we need to notify the learner
				// to refine the alphabet and restart to learn.
				throw new LStarException(Type.RestartLearning, newSigma);
			}
		}
	}

	public Trace candidateQuery(DFA dfa) throws LStarException,
			InterruptedException, TzException {
		logger.info("------------Candidate Query Iteration " + candidateCount++
				+ "------------------");
		dfa.print(outStream);
		logger.info("dfa state size: " + dfa.getStateSize());
		logger.info("alphabet size: " + (dfa.sigma.getSize() - 1));
		List<QueryTrace> traces = new ArrayList<QueryTrace>();

		// Step 1: Find all the generated test cases and execute them in order
		// to
		// get a better judgment of DFA based on more information (the old test
		// cases).
		QueryResult oldResult = tester.executeAllOldTestCases();

		// Step 2: Use automata testing strategies to generate the positive and
		// negative traces.
		TracesPair pair = dfa.randomWalk(maxMemberSize * 20, maxMemberSize + 3);

		List<Trace> acceptingTraces = pair.acceptingTraces;
		List<Trace> refusingTraces = pair.refusingTraces;
		logger.info("positive traces size:" + acceptingTraces.size());
		logger.info("negative traces size:" + refusingTraces.size());

		for (Trace trace : acceptingTraces) {
			Query query = new Query(trace);
			QueryResult result = tester.test(query);
			traces.addAll(result.positiveSet);
			traces.addAll(result.negativeSet);
		}

		for (Trace trace : refusingTraces) {
			Query query = new Query(trace);
			QueryResult result = tester.test(query);
			traces.addAll(result.positiveSet);
			traces.addAll(result.negativeSet);
		}

		// Step 3: merge old traces with the newly generated one by appending
		// the
		// old traces after the newly generated traces.
		traces.addAll(oldResult.positiveSet);
		traces.addAll(oldResult.negativeSet);

		Witness evid = refiner.refineCandidate(dfa, traces);

		if (!evid.success) {
			// If we can't find the counterpart data for the specified query,
			// we just return the inconsistent trace's query string as a
			// counterexample to L* and let L* do the control refinement
			logger.info("return counterexample.");
			return evid.counterexample;
		} else if (evid.divider == null) {
			// There is inconsistency, while TzuYu cannot find a divider,
			// we terminate TzuYu.
			throw new TzException(TzExceptionType.CANNOT_FIND_DEVIDER);
		} else if (evid.divider instanceof True) {
			return Trace.epsilon;
		}

		TzuYuAlphabet newSigma = sigma.refine(evid.divider, evid.action);
		logger.info("alphabet refined");
		if (newSigma.equals(sigma)) {
			// We find an alphabet which was found before, is this possible?
			return Trace.epsilon;
		} else {
			// At this point we need to notify the learner
			// to refine the alphabet and restart to learn.
			throw new LStarException(Type.RestartLearning, newSigma);
		}
	}

	public void report(IReportHandler<TzuYuAlphabet> reporter) throws ReportException {
		// report it output
		outStream.writeln("Total NO. of membership queries:" + membershipCount)
				.writeln("Total NO. of candidate queries:" + candidateCount);
		// report its component output
		refiner.report(reporter);
		tester.report(reporter);
	}
}
