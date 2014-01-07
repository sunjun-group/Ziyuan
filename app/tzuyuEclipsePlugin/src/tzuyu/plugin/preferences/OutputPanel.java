/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package tzuyu.plugin.preferences;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import tzuyu.plugin.TzuyuPlugin;
import tzuyu.plugin.core.constants.Messages;


/**
 * @author LLT
 *
 */
public class OutputPanel extends Composite {
	private Messages msg = TzuyuPlugin.getDefault().getMessages();
	
	public OutputPanel(Composite parent) {
		super(parent, SWT.NONE);
		setFont(parent.getFont());
		// title
		Label title = new Label(this, SWT.NONE);
		title.setText(msg.gentest_prefs_output());
	}
	
	
}
