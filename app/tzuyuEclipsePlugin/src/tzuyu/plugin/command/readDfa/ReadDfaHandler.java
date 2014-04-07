/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package tzuyu.plugin.command.readDfa;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.handlers.HandlerUtil;

import tzuyu.engine.utils.SimpleDotUtils;
import tzuyu.engine.utils.dfa.DfaGraph;
import tzuyu.plugin.TzuyuPlugin;
import tzuyu.plugin.reporter.PluginLogger;
import tzuyu.plugin.view.dfa.DfaView;

/**
 * @author LLT
 * 
 */
public class ReadDfaHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		ISelection selection = HandlerUtil.getActiveMenuSelection(event);
		if (selection instanceof IStructuredSelection) {
			Object element = ((IStructuredSelection) selection).getFirstElement();
			if (element instanceof IFile) {
				IFile file = (IFile) element;
				try {
					
					DfaGraph dfaGraph = SimpleDotUtils.readDot(file.getContents());
					DfaView dfaView = (DfaView) TzuyuPlugin.showDfaView();
					dfaView.displayDfaGraph(dfaGraph);
				} catch (CoreException e) {
					PluginLogger.getLogger().logEx(e);
				}
			}
		}

		return null;
	}

}
