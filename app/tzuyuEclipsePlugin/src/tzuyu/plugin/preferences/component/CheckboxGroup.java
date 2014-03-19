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
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;

import tzuyu.engine.utils.Assert;
import tzuyu.plugin.TzuyuPlugin;
import tzuyu.plugin.core.constants.Messages;
import tzuyu.plugin.ui.ValueChangedEvent;
import tzuyu.plugin.ui.ValueChangedListener;

/**
 * @author LLT
 * 
 */
public class CheckboxGroup<T> {
	private Group cbGroup;
	private List<Button> btns = new ArrayList<Button>();
	private Messages msg = TzuyuPlugin.getMessages();
	private int colNum;
	
	public CheckboxGroup(Composite parent, String text, int colSpan, int colNum) {
		cbGroup = new Group(parent, SWT.SHADOW_ETCHED_OUT);
		cbGroup.setText(text);
		cbGroup.setLayout(new GridLayout(colNum, true));
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL
				| GridData.GRAB_HORIZONTAL);
		gridData.horizontalSpan = colSpan;
		cbGroup.setLayoutData(gridData);
		this.colNum = colNum;
	}

	public CheckboxGroup(Composite parent, String text, int colSpan) {
		this(parent, text, colSpan, 1);
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
		Button btn = new Button(cbGroup, getButtonStyle());
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
	
	protected int getButtonStyle() {
		return SWT.CHECK;
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
	
	public void addValueChangedListener(
			final ValueChangedListener<List<T>> listener,
			final boolean excludeDefault) {
		for (Button btn : btns) {
			btn.addSelectionListener(new SelectionListener() {
				
				public void widgetSelected(SelectionEvent e) {
					listener.onValueChanged(new ValueChangedEvent<List<T>>(
							this, null, getValue()));
				}
				
				public void widgetDefaultSelected(SelectionEvent e) {
					if (!excludeDefault) {
						listener.onValueChanged(new ValueChangedEvent<List<T>>(
								this, null, getValue()));
					}
					
				}
			});
		}
	}
	
	public Group getWidget() {
		return cbGroup;
	}
}
