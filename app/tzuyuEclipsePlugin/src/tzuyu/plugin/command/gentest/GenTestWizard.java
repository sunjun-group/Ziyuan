/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package tzuyu.plugin.command.gentest;

import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Composite;

import tzuyu.plugin.TzuyuPlugin;
import tzuyu.plugin.core.constants.Messages;
import tzuyu.plugin.ui.OptionWizardPage;

/**
 * @author LLT
 * @author Peter Kalauskas [Randoop, RandoopLaunchConfigurationWizard]
 */
@Deprecated
public class GenTestWizard extends Wizard {
	private Messages messages;
	private IJavaProject project;
	private GenTestPreferences config;
	private ProjectInputPage testProjectInputPage;

	public GenTestWizard(IJavaProject project, GenTestPreferences config) {
		super();
		messages = TzuyuPlugin.getMessages();
		this.project = project;
		this.config = config;
		testProjectInputPage = new ProjectInputPage(project, config);
		addPage(testProjectInputPage);

		setTitleBarColor(new RGB(167, 215, 250));
		setWindowTitle(messages.gentest_config_wizard_title());
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public void createPageControls(Composite pageContainer) {
		super.createPageControls(pageContainer);
		
		for (IWizardPage page : getPages()) {
			((OptionWizardPage) page).initFrom(config);
		}
		setNeedsProgressMonitor(true);
		setHelpAvailable(false);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public boolean performFinish() {
		for (IWizardPage page : getPages()) {
			if (!((OptionWizardPage) page).isValid(config)) {
				return false;
			}
		}
		return false;
	}

}
