/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package tzuyu.plugin.command.gentestwizard;

import org.eclipse.jdt.ui.wizards.NewElementWizardPage;

import tzuyu.plugin.TzuyuPlugin;
import tzuyu.plugin.command.gentest.GenTestPreferences;
import tzuyu.plugin.core.constants.Messages;

/**
 * @author LLT
 *
 */
public abstract class GenTestWizardPage extends NewElementWizardPage {
	protected GenTestPreferences prefs;
	
	protected Messages msg = TzuyuPlugin.getMessages();
	
	protected GenTestWizardPage(String pageName, GenTestPreferences prefs) {
		super(pageName);
		this.prefs = prefs;
	}

	public abstract void preformFinish();
	
	
}
