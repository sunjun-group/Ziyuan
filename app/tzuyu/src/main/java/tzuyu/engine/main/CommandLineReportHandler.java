/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package tzuyu.engine.main;

import java.io.FileWriter;

import tzuyu.engine.TzConfiguration;
import tzuyu.engine.iface.TzReportHandler;
import tzuyu.engine.model.TzuYuAlphabet;
import tzuyu.engine.model.dfa.DFA;

/**
 * @author LLT
 * 
 */
public class CommandLineReportHandler extends TzReportHandler {
	
	public CommandLineReportHandler(TzConfiguration config) {
		super(config);
	}
	
	@Override
	public void reportDFA(DFA lastDFA, TzuYuAlphabet sigma) {
		saveDFA(lastDFA, sigma.getProject().getConfiguration());
	}

	private void saveDFA(DFA dfa, TzConfiguration config) {
		if (dfa != null) {
			String dot = dfa.createDotRepresentation();
			try {
				String fileName = config.getAbsoluteAddress(getTargetClassName() + ".dot");
				FileWriter writer = new FileWriter(fileName);
				writer.write(dot);
				writer.close();
			} catch (Exception e) {
				System.out.println(e.getMessage());
			}
		}
	}
}
