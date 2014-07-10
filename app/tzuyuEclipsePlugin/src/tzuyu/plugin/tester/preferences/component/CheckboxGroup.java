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
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

import tzuyu.plugin.tester.ui.ValueChangedEvent;
import tzuyu.plugin.tester.ui.ValueChangedListener;

/**
 * @author LLT
 * 
 */
public class CheckboxGroup<T> extends SelectionBoxGroup<T> {
	
	public CheckboxGroup(Composite parent, String text, int colSpan, int colNum) {
		super(parent, text, colSpan, colNum);
	}

	public CheckboxGroup(Composite parent, String text, int colSpan) {
		super(parent, text, colSpan);
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
}
