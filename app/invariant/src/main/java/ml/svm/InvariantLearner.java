/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package ml.svm;

import java.util.List;

import sav.common.core.formula.Formula;

import libsvm.core.FormulaProcessor;
import libsvm.core.Machine;

/**
 * @author LLT
 *
 */
public class InvariantLearner {

	public void learn(List<BreakpointData> datapoints) {
		Machine machine = new Machine();
		
		machine.train();
		
		Formula formula = machine.getLearnedLogic(new FormulaProcessor());
	}
	
	public void selectSampling(Formula formula) {
		SelectSamplingVisitor visitor = new SelectSamplingVisitor();
		formula.accept(visitor);
	}
	
}
