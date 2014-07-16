/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package tzuyu.plugin.tester.reporter;

import java.io.FileWriter;
import java.io.PrintStream;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.widgets.Display;

import sav.common.core.iface.ILogger;
import tzuyu.engine.TzClass;
import tzuyu.engine.iface.IPrintStream;
import tzuyu.engine.iface.TzReportHandler;
import tzuyu.engine.model.TzuYuAlphabet;
import tzuyu.engine.model.dfa.DFA;
import tzuyu.engine.model.exception.ReportException;
import tzuyu.engine.model.exception.TzException;
import tzuyu.engine.utils.TzUtils;
import tzuyu.plugin.TzuyuPlugin;
import tzuyu.plugin.commons.constants.PluginConstants;
import tzuyu.plugin.tester.command.gentest.GenTestPreferences;
import tzuyu.plugin.tester.console.PluginConsolePrintStream;
import tzuyu.plugin.tester.reporter.assertion.AssertionWriter;
import tzuyu.plugin.tester.view.dfa.DfaView;

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

	public void reportDFA(final DFA lastDFA, TzuYuAlphabet sigma) throws ReportException {
		monitor.beginTask("report DFA", 3);
		// we can handle the tzuyu output data right away, or further.
		monitor.subTask("generate testcases");
		saveDFA(lastDFA, sigma.getProject());
		monitor.worked(1);
		Display.getDefault().asyncExec(new Runnable() {
			
			@Override
			public void run() {
				monitor.subTask("show DFA viewer");
				DfaView dfaView = (DfaView) TzuyuPlugin
						.getShowedView(PluginConstants.ID_DFA_VIEW);
				if (dfaView != null) {
					dfaView.displayDFA(lastDFA);
				}
				monitor.worked(1);
			}
		});
		
		monitor.subTask("generate assert statement");
		AssertionWriter assertWriter = new AssertionWriter(lastDFA, sigma
				.getProject().getTarget(), prefs.getProject());
		monitor.worked(1);
		try {
			assertWriter.writeAssertion(monitor);
		} catch (TzException e) {
			throw new ReportException(e);
		}
		monitor.done();
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
