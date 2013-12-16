/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package tzuyu.engine.lstar;

import java.util.List;

import lstar.LStarException;
import lstar.LStarException.Type;
import lstar.Teacher;
import refiner.Witness;
import tester.TzuYuTester;
import tzuyu.engine.TzProject;
import tzuyu.engine.bool.True;
import tzuyu.engine.iface.TzReportHandler;
import tzuyu.engine.iface.algorithm.Refiner;
import tzuyu.engine.model.Formula;
import tzuyu.engine.model.Query;
import tzuyu.engine.model.QueryResult;
import tzuyu.engine.model.QueryTrace;
import tzuyu.engine.model.Trace;
import tzuyu.engine.model.TzuYuAction;
import tzuyu.engine.model.TzuYuAlphabet;
import tzuyu.engine.model.TzuYuException;
import tzuyu.engine.model.dfa.DFA;
import tzuyu.engine.model.dfa.TracesPair;

/**
 * @author LLT
 * 
 */
public class TeacherImpl implements Teacher {
	private int maxMemberSize = 0;
	private int iterationCount = 1;
	// TODO-LLT : To refactor later.
	// TzuYuTester and TzuYuTesterV2 are quiet different??
	private TzuYuTester tester;
	private Refiner refiner;
	private TzuYuAlphabet sigma;

	public void setInitAlphabet(TzuYuAlphabet sigma) {
		this.sigma = sigma;
	}

	public void setRefiner(Refiner refiner) {
		this.refiner = refiner;
	}

	public void setTester(TzuYuTester tester) {
		this.tester = tester;
	}

	@Override
	public boolean membershipQuery(Trace str) throws LStarException {
		// Update maximum membership query size
		if (str.size() > maxMemberSize) {
			maxMemberSize = str.size();
		}

		// Step 2: relay this query as membership query to the Oracle.
		Query query = new Query(str);

		QueryResult result = tester.memberTest(query);

		if (query.isEpsilon()) {
			return true;
		}

		// Step 3: process the result from oracle membership query.
		if (result.positiveSet.size() == 0 && result.negativeSet.size() == 0) {
			// return the old value. Here we need to search the database
			// to find the counterpart. If there is no counterpart in the
			// database, we need to return true or false?

			List<QueryTrace> traces = result.unknownSet;

			boolean wishful = tester.confirmWishfulThinking(traces.get(0));

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
			if (divider == null) {
				throw new TzuYuException("Cannot find "
						+ "divider for the inconsistent transitons");
			}

			QueryTrace trace = result.negativeSet.get(0);

			TzuYuAction action = trace.query
					.getStatement(trace.lastActionIdx + 1);
			TzuYuAlphabet newSigma = sigma.incrementalRefine(divider, action);

			if (newSigma.equals(sigma)) {
				return false;
			} else {
				sigma = newSigma;
				// needRestart = true;
				// At this point, we need to notify the learner
				// to refine the alphabet and restart to learn.
				// learner.stop();
				// return false;
				throw new LStarException(Type.RestartLearning);
			}
		}
	}

	@Override
	public Trace candidateQuery(DFA dfa) throws LStarException {
		logger.info("------------Candidate Query Iteration " + iterationCount++
				+ "------------------");
		dfa.print();
		logger.info("dfa state size: " + dfa.getStateSize());
		// Step 1: Use automata testing strategies to generate the positive and
		// negative traces.

		TracesPair pair = dfa.randomWalk(maxMemberSize * 20, maxMemberSize + 2);

		List<Trace> acceptingTraces = pair.acceptingTraces;
		List<Trace> refusingTraces = pair.refusingTraces;
		logger.info("positive traces size:" + acceptingTraces.size());
		logger.info("negative traces size:" + refusingTraces.size());
		for (Trace trace : acceptingTraces) {
			Query query = new Query(trace);
			tester.candidateTest(query);
		}

		for (Trace trace : refusingTraces) {
			Query query = new Query(trace);
			tester.candidateTest(query);
		}

		// Witness evid = refiner.candidateRefinement(dfa);
		Witness evid = refiner.refineCandidate(dfa, null);

		if (!evid.success) {
			// If we can't find the counterpart data for the specified query,
			// we just return the inconsistent trace's query string as a
			// counterexample to L* and let L* do the control refinement
			logger.info("return counterexample.");
			return evid.counterexample;
		} else if (evid.divider == null) {
			// There is inconsistency, while TzuYu cannot find a divider,
			// we terminate TzuYu.
			throw new TzuYuException("cannot find a divider "
					+ "for inconsistent transtion");
		} else if (evid.divider instanceof True) {
			return Trace.epsilon;
		}

		TzuYuAlphabet newSigma = sigma.incrementalRefine(evid.divider,
				evid.action);
		logger.info("alphabet refined");
		if (newSigma.equals(sigma)) {
			// We find an alphabet which was found before, is this possible?
			return Trace.epsilon;
		} else {
			// At this point we need to notify the learner
			// to refine the alphabet and restart to learn.
			sigma = newSigma;
			// needRestart = true;
			// learner.stop();
			// return Trace.epsilon;
			throw new LStarException(Type.RestartLearning);
		}
	}

	@Override
	public void report(TzReportHandler reporter) {
		tester.report(reporter);
	}

	@Override
	public void setProject(TzProject project) {
		refiner.setProject(project);
		tester.setProject(project);
	}

}
