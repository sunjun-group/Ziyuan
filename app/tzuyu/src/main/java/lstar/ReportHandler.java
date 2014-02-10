/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package lstar;

import tzuyu.engine.iface.ILogger;
import tzuyu.engine.model.dfa.Alphabet;
import tzuyu.engine.model.dfa.DFA;

/**
 * @author LLT
 *
 */
public interface ReportHandler <A extends Alphabet>{
	/**
	 * report last DFA which get from LStar learner.
	 * (ex: print DFA to files)
	 */
	void reportDFA(DFA lastDFA, A sigma);

	ILogger<?> getLogger();
	
	public void done();
}
