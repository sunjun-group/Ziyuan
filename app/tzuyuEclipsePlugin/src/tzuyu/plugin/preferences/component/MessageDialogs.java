/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package tzuyu.plugin.preferences.component;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;

/**
 * @author LLT
 *
 */
public class MessageDialogs {
	
	
	public static boolean confirm(Shell shell, String msg) {
		MessageDialog dialog = new MessageDialog(shell, 
				"confirm", null, msg, MessageDialog.CONFIRM, 
				new String[] {IDialogConstants.YES_LABEL, IDialogConstants.NO_LABEL }, 
				0);
		return dialog.open() == 0;
	}
}
