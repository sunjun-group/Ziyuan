/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package icsetlv;

import icsetlv.common.exception.IcsetlvException;
import icsetlv.vm.SimpleDebugger;
import icsetlv.vm.VMConfiguration;

import org.junit.Assert;
import org.junit.Test;

import com.sun.jdi.VirtualMachine;

import sav.common.core.utils.CollectionUtils;

/**
 * @author LLT
 *
 */
public class SimpleDebuggerTest extends AbstractTest {
	
	@Test
	public void test() throws IcsetlvException {
		VMConfiguration config = initVmConfig();
		config.setProgramArgs(CollectionUtils.listOf("testdata.slice.FindMaxCallerPassTest1"));
		SimpleDebugger debugger = new SimpleDebugger();
		VirtualMachine vm = debugger.run(config);
		Assert.assertNotNull(vm);
	}
}
