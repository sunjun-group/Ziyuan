/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package invariant;

import icsetlv.InvariantLearner;
import icsetlv.common.dto.BreakpointData;

import java.util.ArrayList;
import java.util.List;

import libsvm.core.Machine;

import org.junit.Before;
import org.junit.Test;

import sav.commons.AbstractTest;

/**
 * @author LLT
 *
 */
public class InvariantLearnerTest extends AbstractTest {
	private InvariantLearner learner;
	
	@Before
	public void setup() {
		learner = new InvariantLearner(new Machine());
	}

	@Test
	public void testLearn() {
		List<BreakpointData> datapoints = new ArrayList<BreakpointData>();
		
		learner.learn(datapoints);
	}
	
}
