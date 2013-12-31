/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package tzuyu.plugin.reporter;

import tzuyu.engine.TzConfiguration;
import tzuyu.engine.iface.TzReportHandler;
import tzuyu.engine.model.TzuYuAlphabet;
import tzuyu.engine.model.dfa.DFA;

/**
 * @author LLT
 *
 */
public class Reporter extends TzReportHandler {

	public Reporter(TzConfiguration config) {
		super(config);
	}

	@Override
	public void reportDFA(DFA lastDFA, TzuYuAlphabet sigma) {

	}

}
