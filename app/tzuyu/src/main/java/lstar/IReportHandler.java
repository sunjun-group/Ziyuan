/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package lstar;

import tzuyu.engine.iface.IPrintStream;
import tzuyu.engine.model.dfa.Alphabet;
import tzuyu.engine.model.dfa.DFA;
import tzuyu.engine.model.exception.ReportException;

/**
 * @author LLT
 *
 */
public interface IReportHandler <A extends Alphabet<?>>{
	/**
	 * report last DFA which get from LStar learner.
	 * (ex: print DFA to files)
	 */
	void reportDFA(DFA lastDFA, A sigma) throws ReportException;

	public void comit();
	
	IPrintStream getOutStream(OutputType type);
	
	public static enum OutputType {
		SVM,
		TZ_OUTPUT,
		LOG,
		JUNIT_GENERATION
	}
}
