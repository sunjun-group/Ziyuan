/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package tzuyu.plugin.command.gentestwizard;

import org.eclipse.swt.widgets.Composite;

import tzuyu.plugin.command.gentest.GenTestPreferences;
import tzuyu.plugin.preferences.ParameterPanel;

/**
 * @author LLT
 *
 */
public class ParameterWizardPage extends GenTestWizardPage {
	private ParameterPanel paramPanel;
	
	protected ParameterWizardPage(GenTestPreferences prefs) {
		super("parameterWizard", prefs);
		setTitle(msg.gentest_prefs_param());
	}

	@Override
	public void createControl(Composite parent) {
		paramPanel = new ParameterPanel(this, parent);
		setControl(paramPanel);
		paramPanel.refresh(prefs);
	}

	@Override
	public void preformFinish() {
		paramPanel.performOk(prefs);
	}

}
