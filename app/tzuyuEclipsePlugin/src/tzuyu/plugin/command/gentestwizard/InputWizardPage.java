/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package tzuyu.plugin.command.gentestwizard;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import tzuyu.plugin.command.gentest.GenTestPreferences;
import tzuyu.plugin.core.dto.WorkObject;
import tzuyu.plugin.preferences.InputTreeViewer;

/**
 * @author LLT
 *
 */
public class InputWizardPage extends GenTestWizardPage {
	private InputTreeViewer inputTree;
	private WorkObject workObject;
	
	protected InputWizardPage(WorkObject workObject, GenTestPreferences prefs) {
		super("inputWizard", prefs);
		setTitle(msg.gentest_prefs_input());
		this.workObject = workObject;
	}

	@Override
	public void createControl(Composite parent) {
		Composite comp = new Composite(parent, SWT.NONE);
		comp.setLayout(new GridLayout(1, false));
		comp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL
				| GridData.GRAB_HORIZONTAL));
		setControl(comp);
		Label lb = new Label(comp, SWT.BOLD);
		lb.setText(msg.gentest_prefs_input());
		inputTree = new InputTreeViewer(comp);
		inputTree.setData(workObject);
	}

	@Override
	public boolean canFlipToNextPage() {
		return true;
	}

	@Override
	public void preformFinish() {
		inputTree.updateData(workObject);
	}
	
	
}
