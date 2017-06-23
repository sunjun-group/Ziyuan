/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package tzuyu.plugin.tester.command.gentestwizard;

import org.eclipse.jface.wizard.Wizard;
import org.eclipse.swt.widgets.Composite;

import tzuyu.plugin.TzuyuPlugin;
import tzuyu.plugin.commons.constants.Messages;
import tzuyu.plugin.commons.dto.WorkObject;
import tzuyu.plugin.tester.command.gentest.GenTestPreferences;
import tzuyu.plugin.tester.ui.AppEventManager;

/**
 * @author LLT
 * 
 */
public class GenTestWizard extends Wizard {
	private Messages msg = TzuyuPlugin.getMessages();
	private InputWizardPage inputPage;
	private OutputWizardPage outputPage;
	private ParameterWizardPage paramPage;
	private AppEventManager eventManager;

	public GenTestWizard(WorkObject workObject, GenTestPreferences prefs) {
		setWindowTitle(msg.genTestWizard_title());
		eventManager = new AppEventManager();
		inputPage = new InputWizardPage(workObject, prefs, eventManager);
		addPage(inputPage);
		outputPage = new OutputWizardPage(workObject.getProject(), prefs, eventManager);
		addPage(outputPage);
		paramPage = new ParameterWizardPage(prefs, eventManager);
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
