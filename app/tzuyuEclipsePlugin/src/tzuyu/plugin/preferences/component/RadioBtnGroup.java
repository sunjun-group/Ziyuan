/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package tzuyu.plugin.preferences.component;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

/**
 * @author LLT
 *
 */
public class RadioBtnGroup<T> extends CheckboxGroup<T> {

	public RadioBtnGroup(Composite parent, String text, int colSpan) {
		super(parent, text, colSpan);
	}

	public RadioBtnGroup(Composite parent, String text, int colSpan, int colNum) {
		super(parent, text, colSpan, colNum);
	}

	@Override
	protected int getButtonStyle() {
		return SWT.RADIO;
	}
}
