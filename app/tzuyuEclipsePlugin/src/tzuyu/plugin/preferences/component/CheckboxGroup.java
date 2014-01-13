/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package tzuyu.plugin.preferences.component;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;

/**
 * @author LLT
 * 
 */
public class CheckboxGroup<T> {
	private Group cbGroup;
	private List<Button> btns = new ArrayList<Button>();

	public CheckboxGroup(Composite parent, String text, int colSpan) {
		cbGroup = new Group(parent, SWT.SHADOW_ETCHED_OUT);
		cbGroup.setText(text);
		cbGroup.setLayout(new GridLayout(1, true));
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL
				| GridData.GRAB_HORIZONTAL);
		gridData.horizontalSpan = colSpan;
		cbGroup.setLayoutData(gridData);
	}

	public void addCb(String text, T value) {
		Button btn = new Button(cbGroup, SWT.CHECK);
		btn.setText(text);
		GridData layoutData = new GridData();
		layoutData.horizontalIndent = 20;
		layoutData.verticalIndent = 5;
		btn.setLayoutData(layoutData);
		btn.setData(value);
		btns.add(btn);
	}

	@SuppressWarnings("unchecked")
	public List<T> getValue() {
		List<T> value = new ArrayList<T>();
		for (Button btn : btns) {
			if (btn.getSelection()) {
				value.add((T) btn.getData());
			}
		}
		return value;
	}

	public void setValue(List<T> values) {
		for (Button btn : btns) {
			btn.setSelection(values.contains(btn.getData()));
		}
	}
}
