/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package tzuyu.engine.main;

import java.io.FileWriter;

import sav.common.core.iface.ILogger;
import tzuyu.engine.TzClass;
import tzuyu.engine.TzConfiguration;
import tzuyu.engine.iface.IPrintStream;
import tzuyu.engine.iface.TzPrintStream;
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
	public IPrintStream getOutStream(lstar.IReportHandler.OutputType type) {
		return new TzPrintStream(System.out);
	}

	@Override
	public void comit() {
		
	}

	@Override
	public ILogger<?> getLogger() {
		return CommandLineLogger.instance();
	}
}
