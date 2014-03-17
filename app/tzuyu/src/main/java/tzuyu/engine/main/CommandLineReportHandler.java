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
import tzuyu.engine.TzClass;
import tzuyu.engine.iface.ILogger;
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
	
	public void reportDFA(DFA lastDFA, TzuYuAlphabet sigma) {
		saveDFA(lastDFA, sigma.getProject());
	}

	private void saveDFA(DFA dfa, TzClass tzProject) {
		if (dfa != null) {
			String dot = dfa.createDotRepresentation();
			try {
				String fileName = tzProject.getConfiguration()
						.getAbsoluteAddress(tzProject.getClassName() + ".dot");
				FileWriter writer = new FileWriter(fileName);
				writer.write(dot);
				writer.close();
			} catch (Exception e) {
				System.out.println(e.getMessage());
			}
		}
	}

	@Override
	public ILogger<?> getLogger() {
		return CommandLineLogger.instance();
	}

	@Override
	public void comit() {
		
	}
}
