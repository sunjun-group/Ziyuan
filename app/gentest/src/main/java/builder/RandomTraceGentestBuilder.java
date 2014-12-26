/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package builder;

import gentest.RandomTester;
import gentest.data.Sequence;

import java.util.List;

import main.GentestConstant;

import sav.common.core.Pair;
import sav.common.core.SavException;

/**
 * @author LLT
 *
 */
public class RandomTraceGentestBuilder extends GentestBuilder<RandomTraceGentestBuilder> {
	private int queryMaxLength = GentestConstant.DEFAULT_QUERY_MAX_LENGTH;
	private int testPerQuery = GentestConstant.DEFAULT_TEST_PER_QUERY;

	public RandomTraceGentestBuilder(int numberOfTcs) {
		super(numberOfTcs);
	}
	
	public RandomTraceGentestBuilder queryMaxLength(int queryMaxLength) {
		this.queryMaxLength = queryMaxLength;
		return this;
	}
	
	public RandomTraceGentestBuilder testPerQuery(int testPerQuery) {
		this.testPerQuery = testPerQuery;
		return this;
	}

	@Override
	public Pair<List<Sequence>, List<Sequence>> doGenerate() throws SavException {
		RandomTester tester = new RandomTester(queryMaxLength, testPerQuery, numberOfTcs);
		return tester.test(methodCalls);
	}

	
}
