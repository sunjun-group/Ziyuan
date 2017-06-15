/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package learntest.core.jdart;

import java.util.ArrayList;
import java.util.List;

import icsetlv.common.dto.BreakpointValue;
import jdart.model.ArrayTestVar;
import jdart.model.ObjectTestVar;
import jdart.model.PrimaryTestVar;
import jdart.model.TestInput;
import jdart.model.TestVar;
import sav.common.core.utils.CollectionUtils;
import sav.common.core.utils.PrimitiveUtils;
import sav.strategies.dto.execute.value.ArrayValue;
import sav.strategies.dto.execute.value.BooleanValue;
import sav.strategies.dto.execute.value.ExecValue;
import sav.strategies.dto.execute.value.ExecVarType;
import sav.strategies.dto.execute.value.PrimitiveValue;
import sav.strategies.dto.execute.value.ReferenceValue;

/**
 * @author LLT
 *
 */
public class JdartTestInputUtils {
	private JdartTestInputUtils() {
	}
	
	public static List<BreakpointValue> toBreakpointValue(List<TestInput> inputs, String bkpId) {
		List<BreakpointValue> values = new ArrayList<BreakpointValue>(inputs.size());
		for (TestInput input : inputs) {
			values.add(toBreakpointValue(input, bkpId));
		}
		return values;
	}

	public static BreakpointValue toBreakpointValue(TestInput input, String bkpId) {
		BreakpointValue bkpValue = new BreakpointValue(bkpId);
		for (TestVar var : input.getParamList()) {
			addExecValue(bkpValue, var);
		}
		return bkpValue;
	}

	private static ExecValue addExecValue(ExecValue parent, TestVar var) {
		ExecValue value = null;
		if (var instanceof ArrayTestVar) {
			value = toArrayValue(parent, (ArrayTestVar) var);
		} else if (var instanceof PrimaryTestVar) {
			value = toPrimitiveValue(parent, (PrimaryTestVar) var);
		} else if (var instanceof ObjectTestVar) {
			value = toReferenceValue(parent, (ObjectTestVar) var);
		}
		for(TestVar childVar : CollectionUtils.nullToEmpty(var.getChildren())) {
			addExecValue(value, childVar);
		}
		parent.add(value);
		return value;
	}

	private static ExecValue toReferenceValue(ExecValue parent, ObjectTestVar var) {
		return new ReferenceValue(getChildId(parent, var), false);
	}

	private static ExecValue toPrimitiveValue(ExecValue parent, PrimaryTestVar var) {
		if (PrimitiveUtils.isBooleanType(var.getType())) {
			return new BooleanValue(getChildId(parent, var), Boolean.valueOf(var.getValue()));
		} else {
			ExecVarType type = ExecVarType.primitiveTypeOf(var.getType());
			return new PrimitiveValue(getChildId(parent, var), var.getValue()) {
				@Override
				public ExecVarType getType() {
					return type;
				}
			};
		}
	}

	private static ArrayValue toArrayValue(ExecValue parent, ArrayTestVar var) {
		return new ArrayValue(getChildId(parent, var));
	}

	private static String getChildId(ExecValue parent, TestVar var) {
		return parent.getChildId(var.getName());
	}

}
