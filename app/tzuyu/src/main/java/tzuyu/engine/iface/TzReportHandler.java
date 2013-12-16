/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package tzuyu.engine.iface;

import java.util.List;

import tzuyu.engine.model.Sequence;
import tzuyu.engine.model.dfa.DFA;

/**
 * @author LLT
 *
 */
public interface TzReportHandler {

	/**
	 * report last DFA which get from LStar learner.
	 * (ex: print DFA to files)
	 */
	void reportDFA(DFA lastDFA);

	void writeTestCases(List<Sequence> allTestCases);
	
}
