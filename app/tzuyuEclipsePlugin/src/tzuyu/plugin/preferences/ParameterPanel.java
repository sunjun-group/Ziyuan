/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package tzuyu.plugin.preferences;

import org.eclipse.jface.dialogs.DialogPage;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;

import tzuyu.engine.TzConfiguration;
import tzuyu.plugin.command.gentest.GenTestPreferences;
import tzuyu.plugin.preferences.component.IntText;
import tzuyu.plugin.ui.PropertyPanel;
import tzuyu.plugin.ui.SWTFactory;

/**
 * @author LLT
 * 
 */
public class ParameterPanel extends PropertyPanel<GenTestPreferences> {
	private Label arrayMaxLengthLb;
	private IntText arrayMaxLengthTx;
	private Label classMaxDepthLb;
	private IntText classMaxDepthTx;
	private Label stringMaxLengthLb;
	private IntText stringMaxLengthTx;
	private Button objToIntCb;
	private Label testsPerQueryLb;
	private IntText testsPerQueryTx;

	public ParameterPanel(DialogPage msgContainer, Composite parent) {
		super(parent, msgContainer);
		GridLayout grid = new GridLayout(2, false);
		setLayout(grid);
		GridData layoutData = new GridData(GridData.FILL_BOTH); 
		setLayoutData(layoutData);
		grid.marginRight = 20;
		
		decorateContent(this);
	}

	private void decorateContent(Composite contentPanel) {
		int colNum = 2;
		Group group1 = SWTFactory.createGroup(contentPanel, "", colNum);
		group1.setLayout(new GridLayout(2, false));
		arrayMaxLengthLb = SWTFactory.createLabel(group1,
				msg.gentest_prefs_param_arrayMaxDepth());
		arrayMaxLengthTx = new IntText(group1,
				ParamField.ARRAY_MAX_LENGTH).positive().mandatory();
		
		classMaxDepthLb = SWTFactory.createLabel(group1,
				msg.gentest_prefs_param_classMaxDepth());
		classMaxDepthTx = new IntText(group1, ParamField.CLASS_MAX_DEPTH).positive().mandatory();
		
		stringMaxLengthLb = SWTFactory.createLabel(group1,
				msg.gentest_prefs_param_stringMaxLength());
		stringMaxLengthTx = new IntText(group1,
				ParamField.STRING_MAX_LENGTH).positive().mandatory();
		
		Group group2 = SWTFactory.createGroup(contentPanel, "", colNum);
		objToIntCb = SWTFactory.createCheckbox(group2,
				msg.gentest_prefs_param_objectToInteger(), colNum);
		
		Group group3 = SWTFactory.createGroup(contentPanel, "", colNum);
		group3.setLayout(new GridLayout(2, false));
		testsPerQueryLb = SWTFactory.createLabel(group3,
				msg.gentest_prefs_param_testPerQuery());
		testsPerQueryTx = new IntText(group3, ParamField.TESTS_PER_QUERY).positive().mandatory();
		
		addModifyListener();
	}

	@Override
	public void refresh(GenTestPreferences data) {
		TzConfiguration tzConfig = data.getTzConfig();
		arrayMaxLengthTx.setValue(tzConfig.getArrayMaxLength());
		classMaxDepthTx.setValue(tzConfig.getClassMaxDepth());
		stringMaxLengthTx.setValue(tzConfig.getStringMaxLength());
		objToIntCb.setSelection(tzConfig.isObjectToInteger());
		testsPerQueryTx.setValue(tzConfig.getTestsPerQuery());
	}

	private void addModifyListener() {
		addModifyListener(ParamField.ARRAY_MAX_LENGTH, arrayMaxLengthTx);
		addModifyListener(ParamField.CLASS_MAX_DEPTH, classMaxDepthTx);
		addModifyListener(ParamField.STRING_MAX_LENGTH, stringMaxLengthTx);
		addModifyListener(ParamField.TESTS_PER_QUERY, testsPerQueryTx);
	}

	@Override
	public void performOk(GenTestPreferences prefs) {
		TzConfiguration tzConfig = prefs.getTzConfig();
		tzConfig.setArrayMaxLength(arrayMaxLengthTx.getValue());
		tzConfig.setClassMaxDepth(classMaxDepthTx.getValue());
		tzConfig.setStringMaxLength(stringMaxLengthTx.getValue());
		tzConfig.setObjectToInteger(objToIntCb.getSelection());
		tzConfig.setTestsPerQuery(testsPerQueryTx.getValue());
	}

	@Override
	protected int getFieldNums() {
		return ParamField.values().length;
	}

	/**
	 * for error field display order
	 */
	private static enum ParamField {
		ARRAY_MAX_LENGTH, CLASS_MAX_DEPTH, STRING_MAX_LENGTH, LONG_FORMAT, OBJ_TO_INT, TESTS_PER_QUERY
	}

}
