/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package icsetlv.sampling;

import icsetlv.DefaultValues;
import icsetlv.Engine;
import icsetlv.InvariantLearner;
import icsetlv.common.dto.BkpInvariantResult;
import icsetlv.common.dto.BreakpointData;
import icsetlv.variable.TestcasesExecutor;

import java.util.List;

import libsvm.core.Machine;

import sav.common.core.SavException;
import sav.strategies.dto.BreakPoint;
import sav.strategies.vm.VMConfiguration;

/**
 * @author LLT
 * TODO: replace {@link Engine} with this one.
 */
public class SelectiveSampling {
	private TestcasesExecutor tcExecutor;

	public void learn(VMConfiguration config, List<String> allTests,
			List<BreakPoint> bkps) throws SavException {
		List<BreakpointData> bkpsData = debugTestAndCollectData(config, allTests, bkps);
		InvariantLearner learner = new InvariantLearner(new Machine());
		List<BkpInvariantResult> result = learner.learn(bkpsData);
	}

	private List<BreakpointData> debugTestAndCollectData(VMConfiguration config,
			List<String> allTests, List<BreakPoint> bkps) throws SavException {
		ensureTcExecutor();
		tcExecutor.setup(config, allTests);
		tcExecutor.run(bkps);
		return tcExecutor.getResult();
	}
	
	public void ensureTcExecutor() {
		if (tcExecutor == null) {
			tcExecutor = new TestcasesExecutor(DefaultValues.DEBUG_VALUE_RETRIEVE_LEVEL);
		}
	}
	
	public void setTcExecutor(TestcasesExecutor tcExecutor) {
		this.tcExecutor = tcExecutor;
	}
}
