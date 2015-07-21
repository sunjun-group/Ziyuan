/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package icsetlv.common.dto;

import sav.common.core.formula.Formula;
import sav.strategies.dto.BreakPoint;

/**
 * @author LLT
 * 
 */
public class BkpInvariantResult {
	private BreakPoint bkp;
	private Formula learnedLogic;

	public BkpInvariantResult(BreakPoint bkp, Formula learnedLogic) {
		this.bkp = bkp;
		this.learnedLogic = learnedLogic;
	}

	public BreakPoint getBkp() {
		return bkp;
	}

	public void setBkp(BreakPoint bkp) {
		this.bkp = bkp;
	}

	public Formula getLearnedLogic() {
		return learnedLogic;
	}

	public void setLearnedLogic(Formula learnedLogic) {
		this.learnedLogic = learnedLogic;
	}

}
