/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package tzuyu.plugin.tester.command.gentestwizard;

import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.swt.widgets.Composite;

import tzuyu.plugin.tester.command.gentest.GenTestPreferences;
import tzuyu.plugin.tester.preferences.OutputPanel;
import tzuyu.plugin.tester.ui.AppEventManager;

/**
 * @author LLT
 *
 */
public class OutputWizardPage extends GenTestWizardPage {
	private IJavaProject project;
	private OutputPanel outputPanel;

	protected OutputWizardPage(IJavaProject project, GenTestPreferences prefs, AppEventManager eventManager) {
		super("outputWizard", prefs, eventManager);
		setTitle(msg.gentest_prefs_output());
		this.project = project;
		this.prefs = prefs;
	}

	public void createControl(Composite parent) {
		outputPanel = new OutputPanel(this, parent, project, getShell());
		outputPanel.setAutoUpdateContainerMsg(false);
		outputPanel.setEventManager(eventManager);
		outputPanel.refresh(prefs);
		setControl(outputPanel);
		registerListener();
	}
	
	private void registerListener() {
		registerStatusChangeListener(outputPanel);
	}

	@Override
	public void preformFinish() {
		outputPanel.performOk(prefs);
	}
}
