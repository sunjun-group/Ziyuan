/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package icsetlv;

import icsetlv.common.dto.BkpInvariantResult;
import icsetlv.common.dto.BreakpointData;
import icsetlv.sampling.SelectiveSampling;
import icsetlv.variable.DebugValueInstExtractor;
import icsetlv.variable.TestcasesExecutor;

import java.util.List;
import java.util.Map;

import libsvm.core.Machine;
import sav.common.core.SavException;
import sav.common.core.utils.Assert;
import sav.strategies.dto.BreakPoint;
import sav.strategies.vm.VMConfiguration;

/**
 * @author LLT
 *
 */
public class InvariantMediator {
	private TestcasesExecutor tcExecutor;
	private Machine machine;
	private SelectiveSampling selectiveSampling;

	public List<BkpInvariantResult> learn(VMConfiguration config, List<String> allTests,
			List<BreakPoint> bkps) throws SavException {
		Assert.assertNotNull(tcExecutor, "TestcasesExecutor cannot be null!");
		Assert.assertNotNull(machine, "machine cannot be null!");
		List<BreakpointData> bkpsData = debugTestAndCollectData(config, allTests, bkps);
		InvariantLearner learner = new InvariantLearner(this);
		return learner.learn(bkpsData);
	}
	
	private List<BreakpointData> debugTestAndCollectData(VMConfiguration config,
			List<String> allTests, List<BreakPoint> bkps) throws SavException {
		ensureTcExecutor();
		tcExecutor.setup(config, allTests);
		return debugTestAndCollectData(bkps);
	}
	
	public List<BreakpointData> debugTestAndCollectData(List<BreakPoint> bkps)
			throws SavException {
		tcExecutor.run(bkps);
		return tcExecutor.getResult();
	}
	
	public List<BreakpointData> instDebugAndCollectData(
			List<BreakPoint> bkps, Map<String, Object> instrVarMap) throws SavException {
		ensureTcExecutor();
		tcExecutor.setValueExtractor(new DebugValueInstExtractor(instrVarMap), true);
		List<BreakpointData> result = debugTestAndCollectData(bkps);
		tcExecutor.setValueExtractor(null);
		return result;
	}
	
	public void ensureSelectiveSampling() {
		if (selectiveSampling == null) {
			selectiveSampling = new SelectiveSampling(this);
		}
	}
	
	public void ensureTcExecutor() {
		if (tcExecutor == null) {
			tcExecutor = new TestcasesExecutor(DefaultValues.DEBUG_VALUE_RETRIEVE_LEVEL); 
		}
	}
	
	public void setTcExecutor(TestcasesExecutor tcExecutor) {
		this.tcExecutor = tcExecutor;
	}

	public void setMachine(Machine machine) {
		this.machine = machine;
	}
	
	public Machine getMachine() {
		return machine;
	}
}
