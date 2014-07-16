/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package tzuyu.plugin.tester.preferences.component;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

import sav.common.core.utils.ObjectUtils;
import sav.common.core.utils.StringUtils;
import tzuyu.plugin.TzuyuPlugin;
import tzuyu.plugin.commons.constants.Messages;
import tzuyu.plugin.commons.utils.IStatusUtils;

/**
 * @author LLT
 *
 */
public class IntText {
	private Object fieldEnumOrFieldName;
	private Text text;
	private boolean positive;
	private boolean mandatory;
	
	public IntText(Composite parent, Object fieldEnumOrFieldName) {
		text = new Text(parent, SWT.BORDER);
		text.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		this.fieldEnumOrFieldName = fieldEnumOrFieldName;
	}
	
	public IntText setMandatory() {
		this.mandatory = true;
		return this;
	}
	
	public IntText setPositive() {
		this.positive = true;
		return this;
	}
	
	public IStatus validate() {
		Messages msg = TzuyuPlugin.getMessages();
		try {
			if (org.apache.commons.lang.StringUtils.isBlank(text.getText())
					&& mandatory) {
				return IStatusUtils.error(msg
						.error_mandatory(fieldEnumOrFieldName));
			}
			int val = Integer.parseInt(text.getText());
			if (positive && !ObjectUtils.isPositive(val)) {
				return IStatusUtils.error(msg
						.intText_error_not_positive(fieldEnumOrFieldName));
			}
		} catch (NumberFormatException e) {
			return IStatusUtils.error(msg
					.intText_error_parse(fieldEnumOrFieldName));
		}
		return IStatusUtils.OK_STATUS;
	}

	public int getValue() {
		return Integer.parseInt(text.getText());
	}

	public void addModifyListener(ModifyListener modifyListener) {
		text.addModifyListener(modifyListener);
	}

	public void setValue(int value) {
		text.setText(StringUtils.toStringNullToEmpty(value));
	}

	public Text asWidget() {
		return text;
	}
}
