/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package tzuyu.engine.algorithm.impl;

import gentest.dto.TestQuery;
import gentest.dto.TestResult;
import gentest.iface.ITester;
import lstar.LStarException;
import tzuyu.engine.algorithm.iface.Teacher;
import tzuyu.engine.iface.IAlgorithmFactory;
import tzuyu.engine.lstar.TeacherImpl;
import tzuyu.engine.model.Trace;
import tzuyu.engine.model.TzuYuAlphabet;

/**
 * @author LLT
 *
 */
public class TeacherNewImpl extends TeacherImpl implements Teacher<TzuYuAlphabet> {
	private ITester tester;
	
	public TeacherNewImpl(IAlgorithmFactory<TzuYuAlphabet> tzFactory) {
		super(tzFactory);
	}

	@Override
	public boolean membershipQuery(Trace str) throws LStarException {
		assert sigma != null;
		
		if (str.isEpsilon()) {
			return true;
		}
		TestResult randomTestsResult = tester.test(toQuery(str));
		if (randomTestsResult.isPositiveAndNegativeSetEmpty()) {
			// wishful thinking => true = membership
			// !wishful thinking => false = !membership
			return confirmWishfulThinking(randomTestsResult.getUnknownSet());
		}
		if (randomTestsResult.isPositiveSetEmpty()) {
			return false;
		}
		if (randomTestsResult.isNegativeSetEmpty()) {
			return true;
		}
		// if there are positive and negative tests at the same time. 
		// => find the condition to separate 2 cases and redefine the sigma
		return false;
		
	}

	private TestQuery toQuery(Trace str) {
		// TODO Auto-generated method stub
		return null;
	}

	private boolean confirmWishfulThinking(Object unknownSet) {
		// TODO Auto-generated method stub
		return false;
	}
}
