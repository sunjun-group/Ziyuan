/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package icsetlv.vm;

import icsetlv.common.exception.IcsetlvException;
import sav.common.core.SavException;
import sav.strategies.vm.VMConfiguration;
import sav.strategies.vm.VMListener;
import sav.strategies.vm.VMRunner;

import com.sun.jdi.VirtualMachine;
import com.sun.jdi.connect.IllegalConnectorArgumentsException;

/**
 * @author LLT
 *
 */
public class SimpleDebugger {
	private Process process;
	private VMRunner vmRunner;

	/**
	 * using scenario Target VM attaches to previously-running debugger.
	 */
	public VirtualMachine run(VMConfiguration config) throws IcsetlvException, SavException {
		VMListener listener = new VMListener();
		listener.startListening(config);
		try {
			vmRunner = new VMRunner();
			process = vmRunner.startVm(config);
			if (process != null) {
				return listener.connect(process);
			}
		} catch (IllegalConnectorArgumentsException e) {
			IcsetlvException.rethrow(e);
		} finally {
			listener.stopListening();
		}
		return null;
	}
	
	public void waitProcessUntilStop() throws SavException {
		vmRunner.waitUntilStop(process);
	}
}