/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package tzuyu.plugin.reporter;

import java.io.FileWriter;
import java.io.PrintStream;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.widgets.Display;

import tzuyu.engine.TzClass;
import tzuyu.engine.iface.ILogger;
import tzuyu.engine.iface.IPrintStream;
import tzuyu.engine.iface.TzReportHandler;
import tzuyu.engine.model.TzuYuAlphabet;
import tzuyu.engine.model.dfa.DFA;
import tzuyu.engine.utils.TzUtils;
import tzuyu.plugin.TzuyuPlugin;
import tzuyu.plugin.command.gentest.GenTestPreferences;
import tzuyu.plugin.console.PluginConsolePrintStream;
import tzuyu.plugin.view.dfa.DfaView;

/**
 * @author LLT
 *
 */
public class GenTestReporter extends TzReportHandler {
	private GenTestPreferences prefs;
	private static IPrintStream consoleOut = new PluginConsolePrintStream();;
	private IProgressMonitor monitor;
	
	public GenTestReporter(GenTestPreferences prefs) {
		super(prefs.getTzConfig());
		this.prefs = prefs;
	}
	

	public void reportDFA(final DFA lastDFA, TzuYuAlphabet sigma) {
		saveDFA(lastDFA, sigma.getProject());
		Display.getDefault().asyncExec(new Runnable() {
			
			@Override
			public void run() {
				DfaView dfaView = (DfaView) TzuyuPlugin
						.getShowedView(TzuyuPlugin.DFA_VIEW_ID);
				if (dfaView != null) {
					dfaView.displayDFA(lastDFA);
				}
			}
		});
	}

	private void saveDFA(DFA dfa, TzClass tzProject) {
		if (dfa != null) {
			String dot = dfa.createDotRepresentation();
			String fileName = tzProject.getConfiguration()
					.getAbsoluteAddress(TzUtils.getDfaFileName(tzProject));
			try {
				FileWriter writer = new FileWriter(fileName);
				writer.write(dot);
				writer.close();
			} catch (Exception e) {
				PluginLogger.getLogger().logEx(e,
						"Cannot write DFA to file " + fileName);
			}
		}
	}
	
	@Override
	public PrintStream getSystemOutStream() {
		return super.getSystemOutStream();
	}
	
	@Override
	public IPrintStream getOutStream(OutputType type) {
		switch (type) {
		case SVM:
			return consoleOut;
		case TZ_OUTPUT:
			return consoleOut;
		}
		return consoleOut;
	}
	
	public void comit() {
		//nothing to do at this moment.
	}

	public GenTestPreferences getPrefs() {
		return prefs;
	}
	
	@Override
	public ILogger<?> getLogger() {
		return PluginLogger.getLogger();
	}
	
	@Override
	public boolean isInterrupted() {
		return monitor.isCanceled();
	}

	public void setProgressMonitor(IProgressMonitor monitor) {
		this.monitor = monitor;
	}
}
