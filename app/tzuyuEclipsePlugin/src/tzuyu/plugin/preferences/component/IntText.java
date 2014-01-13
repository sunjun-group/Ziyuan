/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package tzuyu.plugin.preferences.component;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

import tzuyu.engine.utils.StringUtils;
import tzuyu.plugin.TzuyuPlugin;
import tzuyu.plugin.core.utils.IStatusUtils;

/**
 * @author LLT
 *
 */
public class IntText {
	private Object fieldEnumOrFieldName;
	private Text text;
	
	public IntText(Composite parent, Object fieldEnumOrFieldName) {
		text = new Text(parent, SWT.BORDER);
		text.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		this.fieldEnumOrFieldName = fieldEnumOrFieldName;
	}
	
	public IStatus validate() {
		try {
			Integer.parseInt(text.getText());
		} catch (NumberFormatException e) {
			return IStatusUtils.error(TzuyuPlugin.getMessages()
					.intText_parse_error(fieldEnumOrFieldName));
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

}
