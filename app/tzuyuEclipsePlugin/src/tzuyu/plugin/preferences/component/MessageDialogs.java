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
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import tzuyu.plugin.TzuyuPlugin;
import tzuyu.plugin.core.constants.Messages;

/**
 * @author LLT
 *
 */
public class MessageDialogs {
	private static final Messages msgs = TzuyuPlugin.getMessages();
	
	public static boolean confirm(Shell shell, String msg) {
		MessageDialog dialog = new MessageDialog(shell, 
				msgs.message_dialog_title(), null, msg, MessageDialog.CONFIRM, 
				new String[] {IDialogConstants.YES_LABEL, IDialogConstants.NO_LABEL }, 
				0);
		return dialog.open() == 0;
	}
	
	public static boolean warningConfirm(Shell shell, String msg) {
		MessageDialog dialog = new MessageDialog(shell, 
				msgs.message_dialog_title(), null, msg, MessageDialog.WARNING, 
				new String[] {IDialogConstants.YES_LABEL, IDialogConstants.NO_LABEL }, 
				0);
		return dialog.open() == 0;
	}
	
	public static void error(Shell shell, String msg) {
		MessageDialog.openError(shell, msgs.message_dialog_title(), msg);
	}
	
	public static void warn(Shell shell, String msg) {
		MessageDialog.openWarning(shell, msgs.message_dialog_title(), msg);
	}
	
	public static void showErrorInUI(final String msg) { 
		Display.getDefault().asyncExec(new Runnable() {

			@Override
			public void run() {
				MessageDialogs.error(TzuyuPlugin.getActiveWorkbenchWindow()
						.getShell(), msg);
			}
		});
	}
	
	public static void showWarningInUI(final String msg) { 
		Display.getDefault().asyncExec(new Runnable() {

			@Override
			public void run() {
				MessageDialogs.warn(TzuyuPlugin.getActiveWorkbenchWindow()
						.getShell(), msg);
			}
		});
	}
}
