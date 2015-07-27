/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package icsetlv.variable;

import icsetlv.common.dto.BreakpointValue;

import java.util.Map;

import sav.common.core.ModuleEnum;
import sav.common.core.SavException;
import sav.common.core.SavRtException;
import sav.strategies.dto.BreakPoint.Variable;

import com.sun.jdi.IncompatibleThreadStateException;
import com.sun.jdi.LocalVariable;
import com.sun.jdi.StackFrame;
import com.sun.jdi.ThreadReference;
import com.sun.jdi.Value;
import com.sun.jdi.VirtualMachine;

/**
 * @author LLT
 * 
 */
public class DebugValueInstExtractor extends DebugValueExtractor {
	private Map<String, Object> instVals;

	public DebugValueInstExtractor(Map<String, Object> instrVarMap) {
		this.instVals = instrVarMap;
	}

	@Override
	protected void extractValue(BreakpointValue bkVal, ThreadReference thread,
			Map<Variable, JdiParam> allVariables) throws SavException {
		for (Variable var : allVariables.keySet()) {
			Object newVal = instVals.get(var.getFullName());
			JdiParam jdiParam = allVariables.get(var);
			if (jdiParam.getLocalVariable() != null) {
				instLocalVar(thread, jdiParam, newVal, var);
			}
		}
		super.extractValue(bkVal, thread, allVariables);
	}

	private void instLocalVar(ThreadReference thread, JdiParam jdiParam,
			Object newVal, Variable var) {
		LocalVariable localVariable = jdiParam.getLocalVariable();
		if (var.getSimpleName().equals(localVariable.name())) {
			VirtualMachine vm = thread.virtualMachine();
			try {
				Value newValue = jdiValueOf(newVal, vm);
				if (newValue != null) {
					getFrame(thread).setValue(localVariable, newValue);
					jdiParam.setValue(newValue);
				}
			} catch (Exception e) {
				throw new SavRtException(e);
			}
		}
	}

	private Value jdiValueOf(Object newVal, VirtualMachine vm) {
		if (newVal instanceof Integer) {
			return vm.mirrorOf((int) newVal);
		}
		if (newVal instanceof Boolean) {
			return vm.mirrorOf((Boolean) newVal);
		}
		return null;
	}

	public StackFrame getFrame(ThreadReference thread) throws SavException {
		try {
			return thread.frame(0);
		} catch (IncompatibleThreadStateException e) {
			throw new SavException(ModuleEnum.JVM, e);
		}
	}
}
