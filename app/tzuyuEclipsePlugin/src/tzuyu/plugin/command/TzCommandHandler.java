/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package tzuyu.plugin.command;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.HandlerUtil;

import tzuyu.plugin.TzuyuPlugin;
import tzuyu.plugin.core.constants.Messages;
import tzuyu.plugin.core.dto.TzPreferences;
import tzuyu.plugin.core.dto.WorkObject;

/**
 * @author LLT
 * 
 */
public abstract class TzCommandHandler<C extends TzPreferences> extends
		AbstractHandler {
	// current selection
	protected ISelection selection;
	protected Messages messages = TzuyuPlugin.getMessages();

	public Object execute(ExecutionEvent event) throws ExecutionException {
		ISelection selection = HandlerUtil.getActiveMenuSelection(event);
		
		if (selection == null || selection.isEmpty()) {
			openDialog(messages.gentest_selection_empty());
		} else if (selection instanceof IStructuredSelection) {
			WorkObject workObject = WorkObject
					.getResourcesPerProject((IStructuredSelection) selection);
			run(workObject, initConfiguration(workObject));
		}
		return null;
	}
	
	protected void openDialog(String msg) {
		Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow()
				.getShell();
		TitleAreaDialog dialog = new TitleAreaDialog(shell);
		dialog.create();
		dialog.setMessage(msg);
		dialog.open();
	}

	protected abstract void run(WorkObject workObject, C config);

	protected abstract C initConfiguration(WorkObject workObject);

}
