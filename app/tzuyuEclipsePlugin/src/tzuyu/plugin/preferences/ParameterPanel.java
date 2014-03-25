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
	private IntText arrayMaxLengthTx;
	private IntText classMaxDepthTx;
	private IntText stringMaxLengthTx;
	private IntText testsPerQueryTx;
	private TypeScopesTablePanel genericTypePanel;

	public ParameterPanel(DialogPage msgContainer, Composite parent) {
		super(parent, msgContainer);
		GridLayout grid = new GridLayout(1, false);
		setLayout(grid);
		GridData layoutData = new GridData(GridData.FILL_BOTH); 
		setLayoutData(layoutData);
		grid.marginRight = 20;
		
		decorateContent(this);
		addModifyListener();
	}

	private void decorateContent(Composite contentPanel) {
		int colNum = 1;
		/* description */
		Label decsLb = SWTFactory.createLabel(contentPanel,
				msg.gentest_prefs_param_description(), colNum);
		GridData data = new GridData();
        data.verticalAlignment = GridData.FILL;
        data.horizontalAlignment = GridData.FILL;
        decsLb.setLayoutData(data);
        
        Composite row2 = SWTFactory.createGridPanel(contentPanel, 2);
        /* learning configuration section */
		createLearningSection(row2, colNum);
		
        /* parameter section */
		createPrimitiveParamSection(row2, colNum);
		
		createGenericParamSection(contentPanel, colNum);
	}

	private void createLearningSection(Composite contentPanel, int colNum) {
		Group lcGroup = SWTFactory.createGroup(contentPanel,
				msg.gentest_prefs_param_group_learning(), colNum);
		lcGroup.setLayout(new GridLayout(2, false));
		SWTFactory.createLabel(lcGroup,
				msg.gentest_prefs_param_testPerQuery());
		testsPerQueryTx = new IntText(lcGroup, ParamField.TESTS_PER_QUERY)
		.setPositive().setMandatory();
	}
	
	private void createPrimitiveParamSection(Composite contentPanel, int colNum) {
		Group primitiveGroup = SWTFactory.createGroup(contentPanel,
				msg.gentest_prefs_param_group_primitive(), colNum);
		primitiveGroup.setLayout(new GridLayout(2, false));
		SWTFactory.createLabel(primitiveGroup,
				msg.gentest_prefs_param_arrayMaxDepth());
		arrayMaxLengthTx = new IntText(primitiveGroup,
				ParamField.ARRAY_MAX_LENGTH).setPositive().setMandatory();
		
		SWTFactory.createLabel(primitiveGroup,
				msg.gentest_prefs_param_classMaxDepth());
		classMaxDepthTx = new IntText(primitiveGroup, ParamField.CLASS_MAX_DEPTH)
		.setPositive().setMandatory();
		
		SWTFactory.createLabel(primitiveGroup,
				msg.gentest_prefs_param_stringMaxLength());
		stringMaxLengthTx = new IntText(primitiveGroup,
				ParamField.STRING_MAX_LENGTH).setPositive().setMandatory();
	}
	
	private void createGenericParamSection(Composite contentPanel, int colNum) {
		Group genericGroup = SWTFactory.createGroup(contentPanel,
				msg.gentest_prefs_param_group_generic(), colNum);
		
		genericTypePanel = new TypeScopesTablePanel(genericGroup);
		SWTFactory.horizontalSpan(genericTypePanel.getWidget(), colNum);
	}


	@Override
	public void refresh(GenTestPreferences data) {
		TzConfiguration tzConfig = data.getTzConfig();
		arrayMaxLengthTx.setValue(tzConfig.getArrayMaxLength());
		classMaxDepthTx.setValue(tzConfig.getClassMaxDepth());
		stringMaxLengthTx.setValue(tzConfig.getStringMaxLength());
		testsPerQueryTx.setValue(tzConfig.getTestsPerQuery());
		genericTypePanel.setValue(data);
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
		tzConfig.setTestsPerQuery(testsPerQueryTx.getValue());
		genericTypePanel.updateData(prefs);
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
