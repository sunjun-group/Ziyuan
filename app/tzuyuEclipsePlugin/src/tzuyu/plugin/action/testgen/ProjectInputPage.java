/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package tzuyu.plugin.action.testgen;

import org.eclipse.debug.internal.ui.SWTFactory;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;

import tzuyu.plugin.ui.OptionWizardPage;
import tzuyu.plugin.ui.option.classSelector.ClassSelectorOption;

/**
 * @author LLT
 * 
 */
public class ProjectInputPage extends OptionWizardPage<GenTestConfiguration> {
	private static final String PAGE_NAME = "Test Project Inputs";
	private IJavaProject project;
	private GenTestConfiguration config;
	private ClassSelectorOption classSelectorOpt;

	public ProjectInputPage(IJavaProject project, GenTestConfiguration config) {
		super(PAGE_NAME);
		this.project = project;
		this.config = config;
	}

	@Override
	public void createControl(Composite parent) {
		Composite comp = SWTFactory.createComposite(parent, 1, 1,
				GridData.FILL_HORIZONTAL);
		setControl(comp);

		classSelectorOpt = new ClassSelectorOption(comp, getWizard()
				.getContainer(), project);

		addOption(classSelectorOpt);
	}

	@Override
	public boolean isValid(GenTestConfiguration config) {
		// TODO Auto-generated method stub
		return false;
	}
}
