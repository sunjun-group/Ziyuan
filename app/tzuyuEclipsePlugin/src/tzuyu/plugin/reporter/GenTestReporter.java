/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package tzuyu.plugin.reporter;

import java.io.File;
import java.io.FileWriter;
import java.util.List;

import org.eclipse.swt.widgets.Display;

import tzuyu.engine.TzClass;
import tzuyu.engine.experiment.NewSequencePrettyPrinter;
import tzuyu.engine.iface.ILogger;
import tzuyu.engine.iface.TzReportHandler;
import tzuyu.engine.model.Sequence;
import tzuyu.engine.model.TzuYuAlphabet;
import tzuyu.engine.model.dfa.DFA;
import tzuyu.plugin.TzuyuPlugin;
import tzuyu.plugin.command.gentest.GenTestPreferences;
import tzuyu.plugin.console.TzConsole;
import tzuyu.plugin.view.dfa.DfaView;

/**
 * @author LLT
 *
 */
public class GenTestReporter extends TzReportHandler {
	private GenTestPreferences prefs;
	private PluginLogger logger;
	
	
	public GenTestReporter(GenTestPreferences prefs) {
		super(prefs.getTzConfig());
		this.prefs = prefs;
		logger = new PluginLogger(TzConsole.getOutputStream());
	}
	
	@Override
	public List<File> writeJUnitTestCases(List<Sequence> allTestCases,
			TzClass tzClazz) {
		NewSequencePrettyPrinter.setUp(new PluginClassWriter(prefs), 
				tzClazz, tzClazz.getConfiguration())
						.print(allTestCases);
		return null;
	}

	public void reportDFA(final DFA lastDFA, TzuYuAlphabet sigma) {
		saveDFA(lastDFA, sigma.getProject());
		Display.getDefault().asyncExec(new Runnable() {
			
			@Override
			public void run() {
				DfaView dfaView = (DfaView) TzuyuPlugin.getShowedView(TzuyuPlugin.DFA_VIEW_ID);
//				DfaView dfaView = (DfaView) TzuyuPlugin.showDfaView();
				if (dfaView != null) {
					dfaView.displayDFA(lastDFA);
				}
			}
		});
	}

	private void saveDFA(DFA dfa, TzClass tzProject) {
		if (dfa != null) {
			String dot = dfa.createDotRepresentation();
			try {
				String fileName = tzProject.getConfiguration().getAbsoluteAddress(getTargetClassName(tzProject) + ".dot");
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
		return logger;
	}
	
	public void done() {
		logger.close();
	}

}
