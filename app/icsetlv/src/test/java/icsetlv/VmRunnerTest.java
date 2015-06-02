/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package icsetlv;

import icsetlv.common.exception.IcsetlvException;

import org.junit.Test;

import sav.common.core.SavException;
import sav.common.core.utils.CollectionUtils;
import sav.commons.AbstractTest;
import sav.strategies.vm.VMConfiguration;
import sav.strategies.vm.VMRunner;

/**
 * @author LLT
 *
 */
public class VmRunnerTest extends AbstractTest {
	
	@Test
	public void test() throws IcsetlvException, SavException {
		VMConfiguration config = initVmConfig();
		config.setProgramArgs(CollectionUtils.listOf("testdata.slice.FindMaxCallerPassTest1"));
		VMRunner.start(config);
	}
}
