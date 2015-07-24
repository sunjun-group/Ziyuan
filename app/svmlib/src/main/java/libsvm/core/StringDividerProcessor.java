/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package libsvm.core;

import sav.common.core.formula.StringVar;

/**
 * @author LLT
 *
 */
public class StringDividerProcessor extends FormulaProcessor<StringVar> {
	
	@Override
	protected StringVar getVar(String label) {
		return new StringVar(label);
	}
}
