/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package tzuyu.plugin.tester.command.gentestwizard;

import org.eclipse.swt.widgets.Composite;

import tzuyu.plugin.tester.command.gentest.GenTestPreferences;
import tzuyu.plugin.tester.preferences.ParameterPanel;
import tzuyu.plugin.tester.ui.AppEventManager;

/**
 * @author LLT
 *
 */
public class ParameterWizardPage extends GenTestWizardPage {
	private ParameterPanel paramPanel;
	
	protected ParameterWizardPage(GenTestPreferences prefs, AppEventManager eventManager) {
		super("parameterWizard", prefs, eventManager);
		setTitle(msg.gentest_prefs_param());
	}

	public void createControl(Composite parent) {
		paramPanel = new ParameterPanel(this, parent);
		paramPanel.setEventManager(eventManager); 
		setControl(paramPanel);
		paramPanel.refresh(prefs);
		registerListener();
	}

	private void registerListener() {
		registerStatusChangeListener(paramPanel);
	}

	@Override
	public void preformFinish() {
		paramPanel.performOk(prefs);
	}

}
