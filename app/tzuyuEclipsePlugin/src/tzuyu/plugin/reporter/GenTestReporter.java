/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package tzuyu.plugin.reporter;

import java.io.File;
import java.util.List;

import tzuyu.engine.TzClass;
import tzuyu.engine.experiment.NewSequencePrettyPrinter;
import tzuyu.engine.iface.TzReportHandler;
import tzuyu.engine.model.Sequence;
import tzuyu.engine.model.TzuYuAlphabet;
import tzuyu.engine.model.dfa.DFA;
import tzuyu.plugin.command.gentest.GenTestPreferences;

/**
 * @author LLT
 *
 */
public class GenTestReporter extends TzReportHandler {
	private GenTestPreferences prefs;
	
	public GenTestReporter(GenTestPreferences prefs) {
		super(prefs.getTzConfig());
		this.prefs = prefs;
	}
	
	@Override
	public List<File> writeJUnitTestCases(List<Sequence> allTestCases,
			TzClass tzClazz) {
		NewSequencePrettyPrinter.setUp(new PluginClassWriter(prefs), 
				tzClazz, tzClazz.getConfiguration())
						.print(allTestCases);
		return null;
	}

	@Override
	public void reportDFA(DFA lastDFA, TzuYuAlphabet sigma) {

	}

}
