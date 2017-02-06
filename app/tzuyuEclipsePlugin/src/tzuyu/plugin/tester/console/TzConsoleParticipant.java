/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package tzuyu.plugin.tester.console;

import org.eclipse.jface.action.Action;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsoleConstants;
import org.eclipse.ui.console.IConsolePageParticipant;
import org.eclipse.ui.part.IPageBookViewPage;

import tzuyu.plugin.TzuyuPlugin;
import tzuyu.plugin.commons.constants.PluginConstants;

/**
 * @author LLT
 *
 */
public class TzConsoleParticipant implements IConsolePageParticipant {
	private ShowDfaViewAction showDfaAction;
	
	@Override
	public Object getAdapter(@SuppressWarnings("rawtypes") Class adapter) {
		return null;
	}

	@Override
	public void init(IPageBookViewPage page, IConsole console) {
		showDfaAction = new ShowDfaViewAction();
		IActionBars bars = page.getSite().getActionBars();
		bars.getToolBarManager().appendToGroup(IConsoleConstants.LAUNCH_GROUP,
				showDfaAction);
	}

	@Override
	public void dispose() {
		showDfaAction = null;
	}

	@Override
	public void activated() {
		// do nothing
	}

	@Override
	public void deactivated() {
		// do nothing
	}

	private static class ShowDfaViewAction extends Action {
		
		public ShowDfaViewAction() {
			super("Show Dfa", TzuyuPlugin.getDefault().getImageDescriptor(
					PluginConstants.SHOW_DFA_ICON));
		}
		
		@Override
		public void run() {
			TzuyuPlugin.showDfaView();
		}
	}
}
