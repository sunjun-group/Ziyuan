/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package tzuyu.plugin.tester.preferences.component;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

import sav.common.core.utils.Assert;
import tzuyu.plugin.tester.ui.ValueChangedEvent;
import tzuyu.plugin.tester.ui.ValueChangedListener;

/**
 * @author LLT
 *
 */
public class RadioBtnGroup<T> extends SelectionBoxGroup<T> {

	public RadioBtnGroup(Composite parent, String text, int colSpan) {
		super(parent, text, colSpan);
	}

	public RadioBtnGroup(Composite parent, String text, int colSpan, int colNum) {
		super(parent, text, colSpan, colNum);
	}
	
	@SuppressWarnings("unchecked")
	public T getValue() {
		for (Button btn : btns) {
			if (btn.getSelection()) {
				return (T) btn.getData();
			}
		}
		return null;
	}
	
	public void setValue(T value) {
		Assert.notNull(value, "Value for an radio group can not be null");
		for (Button btn : btns) {
			if (value.equals(btn.getData())) {
				btn.setSelection(true);
			} else {
				btn.setSelection(false);
			}
		}
	}
	
	public void addValueChangedListener(final ValueChangedListener<T> listener,
			final boolean excludeDefault) {
		for (Button btn : btns) {
			btn.addSelectionListener(new SelectionListener() {

				public void widgetSelected(SelectionEvent e) {
					listener.onValueChanged(new ValueChangedEvent<T>(this,
							null, getValue()));
				}

				public void widgetDefaultSelected(SelectionEvent e) {
					if (!excludeDefault) {
						listener.onValueChanged(new ValueChangedEvent<T>(this,
								null, getValue()));
					}

				}
			});
		}
	}

	@Override
	protected int getButtonStyle() {
		return SWT.RADIO;
	}
}
