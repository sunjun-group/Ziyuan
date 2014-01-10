/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package tzuyu.plugin.preferences;

import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import tzuyu.plugin.command.gentest.GenTestPreferences;
import tzuyu.plugin.ui.PropertyPanel;

/**
 * @author LLT
 *
 */
public class ParameterPanel extends PropertyPanel<GenTestPreferences> {
	
	public ParameterPanel(Composite parent) {
		super(parent);
		Label title = new Label(this, SWT.NONE);
		title.setText(msg.gentest_prefs_param());
	}

	@Override
	public void refresh(GenTestPreferences data) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isValid() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public void performOk(GenTestPreferences prefs) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public FieldEditor[] getFieldEditors() {
		// TODO Auto-generated method stub
		return null;
	}

}
