/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package tzuyu.plugin.tester.command;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.ITypeRoot;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.HandlerUtil;

import tzuyu.plugin.TzuyuPlugin;
import tzuyu.plugin.commons.constants.Messages;
import tzuyu.plugin.commons.dto.TzPreferences;
import tzuyu.plugin.commons.dto.WorkObject;
import tzuyu.plugin.tester.reporter.PluginLogger;

/**
 * @author LLT
 * 
 */
public abstract class TzCommandHandler<C extends TzPreferences> extends
		AbstractHandler {
	protected Messages messages = TzuyuPlugin.getMessages();

	public Object execute(ExecutionEvent event) throws ExecutionException {
		ISelection selection = HandlerUtil.getActiveMenuSelection(event);
		WorkObject workObject = null;
		if (selection instanceof ITextSelection) {
			try {
				workObject = buildWorkObject((ITextSelection) selection, event);
			} catch (JavaModelException e) {
				PluginLogger.getLogger().logEx(e);
				return null;
			}
		}
		if (selection instanceof IStructuredSelection) {
			if (selection == null || selection.isEmpty()) {
				openDialog(messages.gentest_selection_empty());
			}
			workObject = WorkObject
					.getResourcesPerProject((IStructuredSelection) selection);
		}
		if (workObject != null) {
			run(workObject, initConfiguration(workObject));
		}
		return null;
	}
	
	private WorkObject buildWorkObject(ITextSelection selection,
			ExecutionEvent event) throws JavaModelException {
		IEditorPart editor = HandlerUtil.getActiveEditor(event);
		ITypeRoot typeRoot = JavaUI.getEditorInputTypeRoot(editor
				.getEditorInput());
		boolean isEmptySelection = selection.isEmpty()
				|| selection.getLength() <= 0;
		if (typeRoot instanceof ICompilationUnit && !isEmptySelection) {
			ICompilationUnit cu = (ICompilationUnit) typeRoot;
			cu.reconcile(ICompilationUnit.NO_AST, false, null, null);
			IJavaElement selEle = cu.getElementAt(selection.getOffset());
			if (selEle != null) {
				return WorkObject.from(selEle);
			}
		}

		return WorkObject.from(typeRoot);
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
