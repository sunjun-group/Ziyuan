/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package tzuyu.plugin.command.gentestwizard;

import org.eclipse.jface.wizard.Wizard;
import org.eclipse.swt.widgets.Composite;

import tzuyu.plugin.TzuyuPlugin;
import tzuyu.plugin.command.gentest.GenTestPreferences;
import tzuyu.plugin.core.constants.Messages;
import tzuyu.plugin.core.dto.WorkObject;

/**
 * @author LLT
 * 
 */
public class GenTestWizard extends Wizard {
	private Messages msg = TzuyuPlugin.getMessages();
	private InputWizardPage inputPage;
	private OutputWizardPage outputPage;
	private ParameterWizardPage paramPage;

	public GenTestWizard(WorkObject workObject, GenTestPreferences prefs) {
		setWindowTitle(msg.genTestWizard_title());
		inputPage = new InputWizardPage(workObject, prefs);
		addPage(inputPage);
		outputPage = new OutputWizardPage(workObject.getProject(), prefs);
		addPage(outputPage);
		paramPage = new ParameterWizardPage(prefs);
		addPage(paramPage);
	}
	
	public void createPageControls(Composite pageContainer) {
		super.createPageControls(pageContainer);
		setHelpAvailable(false);
	}

	@Override
	public boolean performFinish() {
		for (GenTestWizardPage page : getAllPages()) {
			page.preformFinish();
		}
		return true;
	}

	private GenTestWizardPage[] getAllPages() {
		return new GenTestWizardPage[]{inputPage, outputPage, paramPage};
	}
}
