/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package gentest.builder;

import gentest.core.RandomTester;
import gentest.core.data.Sequence;
import gentest.main.GentestConstants;

import java.util.List;


import sav.common.core.Pair;
import sav.common.core.SavException;

/**
 * @author LLT
 *
 */
public class RandomTraceGentestBuilder extends GentestBuilder<RandomTraceGentestBuilder> {
	private int queryMaxLength = GentestConstants.DEFAULT_QUERY_MAX_LENGTH;
	private int testPerQuery = GentestConstants.DEFAULT_TEST_PER_QUERY;

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
