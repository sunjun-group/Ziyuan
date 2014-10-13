/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package icsetlv;

import icsetlv.common.exception.IcsetlvException;
import icsetlv.vm.VMConfiguration;
import icsetlv.vm.VMRunner;

import org.junit.Test;
import org.junit.experimental.categories.Category;

import sav.common.core.utils.CollectionUtils;

/**
 * @author LLT
 *
 */
public class VmRunnerTest extends AbstractTest {
	
	@Test
	@Category(sav.commons.TzuyuTestCase.class)
	public void test() throws IcsetlvException {
		VMConfiguration config = initVmConfig();
		config.setProgramArgs(CollectionUtils.listOf("testdata.slice.FindMaxCallerPassTest1"));
		VMRunner.startJVM(config);
	}
}
