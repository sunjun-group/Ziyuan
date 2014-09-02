/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package tzuyu.plugin.tester.preferences.component;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;

import sav.common.core.utils.Assert;
import tzuyu.plugin.TzuyuPlugin;
import tzuyu.plugin.commons.constants.Messages;

/**
 * @author LLT
 *
 */
public abstract class SelectionBoxGroup<T> {
	private Group sbGroup;
	protected List<Button> btns = new ArrayList<Button>();
	protected Messages msg = TzuyuPlugin.getMessages();
	private int colNum;
	
	public SelectionBoxGroup(Composite parent, String text, int colSpan) {
		this(parent, text, colSpan, 1);
	}
	
	public SelectionBoxGroup(Composite parent, String text, int colSpan,
			int colNum) {
		sbGroup = new Group(parent, SWT.SHADOW_ETCHED_OUT);
		sbGroup.setText(text);
		sbGroup.setLayout(new GridLayout(colNum, true));
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL
				| GridData.GRAB_HORIZONTAL);
		gridData.horizontalSpan = colSpan;
		sbGroup.setLayoutData(gridData);
		this.colNum = colNum;
	}
	
	public Button add(Button btn, int colSpan) {
		GridData layoutData = new GridData();
		layoutData.horizontalIndent = 20;
		layoutData.verticalIndent = 5;
		layoutData.horizontalSpan = colSpan;
		btn.setLayoutData(layoutData);
		btns.add(btn);
		return btn;
	}
	
	public Button add(String text, T value) {
		Button btn = new Button(sbGroup, getButtonStyle());
		btn.setText(text);
		btn.setData(value);
		return add(btn, colNum);
	}
	
	public Button add(T value) {
		// only accepted if T is Enum type
		Assert.assertTrue(value instanceof Enum<?>,
				"Illegal Argument: expected Enum<?>");
		return add(msg.getMessage((Enum<?>) value), value);
	}
	
	protected abstract int getButtonStyle();
	
	public Group getWidget() {
		return sbGroup;
	}
}
