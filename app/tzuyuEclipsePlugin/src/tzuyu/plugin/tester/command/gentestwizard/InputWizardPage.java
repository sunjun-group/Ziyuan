/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package tzuyu.plugin.tester.command.gentestwizard;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import sav.common.core.utils.CollectionUtils;
import tzuyu.plugin.commons.dto.WorkObject;
import tzuyu.plugin.commons.utils.IStatusUtils;
import tzuyu.plugin.tester.command.gentest.GenTestPreferences;
import tzuyu.plugin.tester.preferences.InputTreeViewer;
import tzuyu.plugin.tester.ui.AppEventManager;
import tzuyu.plugin.tester.ui.ValueChangedEvent;
import tzuyu.plugin.tester.ui.ValueChangedListener;

/**
 * @author LLT
 *
 */
public class InputWizardPage extends GenTestWizardPage {
	private InputTreeViewer inputTree;
	private WorkObject workObject;
	
	protected InputWizardPage(WorkObject workObject, GenTestPreferences prefs, AppEventManager eventManager) {
		super("inputWizard", prefs, eventManager);
		setTitle(msg.gentest_prefs_input());
		this.workObject = workObject;
	}

	public void createControl(Composite parent) {
		Composite comp = new Composite(parent, SWT.NONE);
		comp.setLayout(new GridLayout(1, false));
		comp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL
				| GridData.GRAB_HORIZONTAL));
		setControl(comp);
		Label lb = new Label(comp, SWT.BOLD);
		lb.setText(msg.gentest_prefs_input());
		inputTree = new InputTreeViewer(comp, eventManager);
		inputTree.setData(workObject);
		registerListener();
	}

	/**
	 * this page is quite different from others, it doesn't extend Property
	 * panel and have to handle error message itself
	 */
	private void registerListener() {
		// value change handler
		// show error if no node is selected
		eventManager.register(ValueChangedEvent.TYPE, new ValueChangedListener<Object[]>(inputTree) {

			@Override
			public void onValueChanged(ValueChangedEvent<Object[]> event) {
				if (CollectionUtils.isEmpty(event.getNewVal())) {
					updateStatus(IStatusUtils.error(msg.gentest_prefs_input_error_empty_selection()));
				} else {
					updateStatus(IStatusUtils.OK_STATUS);
				}
			}
		});
	}

	@Override
	public void preformFinish() {
		inputTree.updateData(workObject);
	}
	
	
}
