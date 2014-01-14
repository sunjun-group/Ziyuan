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

import tzuyu.engine.utils.CollectionUtils;
import tzuyu.plugin.command.gentest.GenTestPreferences;
import tzuyu.plugin.core.dto.WorkObject;
import tzuyu.plugin.core.utils.IStatusUtils;
import tzuyu.plugin.preferences.InputTreeViewer;
import tzuyu.plugin.ui.AppEventManager;
import tzuyu.plugin.ui.ValueChangedEvent;
import tzuyu.plugin.ui.ValueChangedListener;

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

	@Override
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
