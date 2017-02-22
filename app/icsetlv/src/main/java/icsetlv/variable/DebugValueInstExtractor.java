/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package icsetlv.variable;

import java.awt.Frame;
import java.util.HashMap;
import java.util.Map;

import com.sun.jdi.ArrayReference;
import com.sun.jdi.ClassNotLoadedException;
import com.sun.jdi.IncompatibleThreadStateException;
import com.sun.jdi.InvalidTypeException;
import com.sun.jdi.LocalVariable;
import com.sun.jdi.StackFrame;
import com.sun.jdi.ThreadReference;
import com.sun.jdi.Value;
import com.sun.jdi.VirtualMachine;

import icsetlv.common.dto.BreakpointValue;
import icsetlv.variable.JdiParam.JdiParamType;
import sav.common.core.ModuleEnum;
import sav.common.core.SavException;
import sav.strategies.dto.BreakPoint.Variable;

/**
 * @author LLT
 * 
 */
public class DebugValueInstExtractor extends DebugValueExtractor {
	/**
	 * instrument value map for {@link Variable#getId()}
	 */
	private Map<String, Object> instVals;

	public DebugValueInstExtractor(int valRetrieveLevel, Map<String, Object> instrVarMap) {
		super(valRetrieveLevel);
		this.instVals = instrVarMap;
	}

	@Override
	protected void collectValue(BreakpointValue bkVal, ThreadReference thread,
			Map<Variable, JdiParam> allVariables) throws SavException {
		modifyValues(thread, allVariables);
		super.collectValue(bkVal, thread, allVariables);
	}

	private void modifyValues(ThreadReference thread, Map<Variable, JdiParam> allVariables) {
		for (Variable var : allVariables.keySet()) {
			JdiParam jdiParam = allVariables.get(var);
			Map<String, JdiParam> modificationMap = getInstrMap(var, jdiParam);
			
			for (String varId : modificationMap.keySet()) {
				
				JdiParam param = modificationMap.get(varId);
				if (param == null) {
					try{
						String fieldName = varId.substring(var.getFullName().length()+1);	
						if(fieldName.equals("isNull")){
							setNullValue(thread, jdiParam, varId);
						}
					}
					catch(Exception e){}
					
					continue;
				}
				
				Object newVal = instVals.get(varId);
				switch (param.getType()) {
				case ARRAY_ELEMENT:
					instArrElement(thread, param, newVal, var);
					break;
				case LOCAL_VAR:
					instLocalVar(thread, param, newVal , var);
					break;
				case NON_STATIC_FIELD:
					System.currentTimeMillis();
//					String fieldName = varId.substring(varId.indexOf("."));
//					if (ArrayReference.class.isAssignableFrom(jdiParam.getValue().getClass()) && fieldName.equals("length")){
//						instArr(thread, jdiParam, newVal, var);
//					}
//					else{
//						instObjField(thread, param, newVal, var);						
//					}
					
					instObjField(thread, param, newVal, var);
					
					break;
				}
			}
		}
	}

	private void setNullValue(ThreadReference thread, JdiParam jdiParam, String varId)
			throws SavException, InvalidTypeException, ClassNotLoadedException {
		Object newVal = instVals.get(varId);
		if(newVal instanceof Boolean){
			Boolean isNull = (Boolean)newVal;
			if(isNull){
				if(jdiParam.getType()==JdiParamType.LOCAL_VAR){
					StackFrame frame = getFrame(thread);
					frame.setValue(jdiParam.getLocalVariable(), null);
				}
				else if(jdiParam.getType()==JdiParamType.NON_STATIC_FIELD || jdiParam.getType()==JdiParamType.NON_STATIC_FIELD){
					thread.setValue(jdiParam.getField(), null);
				}
			}
		}
	}
	
	private void instArr(ThreadReference thread, JdiParam arrayParam, Object arrayLength, Variable var) {
		Value length = jdiValueOf(arrayLength, thread);
		try{
			Integer len = Integer.valueOf(length.toString());
			Value aValue = arrayParam.getValue();
			if(aValue instanceof ArrayReference){
				ArrayReference arrayRef = (ArrayReference)aValue;
				for(int i=0; i<len; i++){
					arrayRef.setValue(i, null);
				}
			}
		}
		catch(Exception e){
			e.printStackTrace();
		}
		
	}

	private Map<String, JdiParam> getInstrMap(Variable var, JdiParam param) {
		Map<String, JdiParam> instrMap = new HashMap<String, JdiParam>();
		String varId = var.getId();
		for (String key : instVals.keySet()) {
			if (!key.startsWith(varId)) {
				continue;
			}
			if (key.length() == varId.length()) {
				instrMap.put(key, param);
				continue;
			}
			// instrument variable is a property of the current param
			// find the correct param for instrument variable
			JdiParam subParam = recursiveMatch(param, extractSubProperty(key));
			if (subParam != param) {
				instrMap.put(key, subParam);
			}
			
			System.currentTimeMillis();
		}
		return instrMap;
	}

	private void instObjField(ThreadReference thread, JdiParam jdiParam,
			Object newVal, Variable var) {
		try {
			if (newVal != null && jdiParam.getField() != null) {
				Value newValue = jdiValueOf(newVal, thread);
				jdiParam.getObj().setValue(jdiParam.getField(), newValue);
				jdiParam.setValue(newValue);
			}
		} catch (Exception e) {
			// ignore the instrument value
			log.warn("cannot modify variable value: " + e.getMessage());
		}
	}

	private void instLocalVar(ThreadReference thread, JdiParam jdiParam,
			Object newVal, Variable var) {
		LocalVariable localVariable = jdiParam.getLocalVariable();
		if (var.getSimpleName().equals(localVariable.name())) {
			try {
				Value newValue = jdiValueOf(newVal, thread);
				if (newValue != null) {
					getFrame(thread).setValue(localVariable, newValue);
					jdiParam.setValue(newValue);
				}
			} catch (Exception e) {
				// ignore the instrument value
				log.warn("cannot modify variable value: " + e.getMessage());
			}
		}
	}
	
	private void instArrElement(ThreadReference thread, JdiParam jdiParam,
			Object newVal, Variable var) {
		try {
			Value newValue = jdiValueOf(newVal, thread);
			ArrayReference arrayRef = jdiParam.getArrayRef();
			if (arrayRef == null || jdiParam.getIdx() < 0
					|| jdiParam.getIdx() >= arrayRef.length()) {
				return;
			}
			arrayRef.setValue(jdiParam.getIdx(), newValue);
			jdiParam.setValue(newValue);
		} catch (Exception e) {
			// ignore the instrument value
			log.warn("cannot modify variable value: " + e.getMessage());
		}
		
	}

	private Value jdiValueOf(Object newVal, ThreadReference thread) {
		VirtualMachine vm = thread.virtualMachine();
		if (newVal instanceof Integer) {
			return vm.mirrorOf((Integer) newVal);
		}
		if (newVal instanceof Long) {
			return vm.mirrorOf((Long) newVal);
		}
		if (newVal instanceof Float) {
			return vm.mirrorOf((Float) newVal);
		}
		if (newVal instanceof Boolean) {
			return vm.mirrorOf((Boolean) newVal);
		}
		if (newVal instanceof Double) {
			return vm.mirrorOf((Double) newVal);
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
