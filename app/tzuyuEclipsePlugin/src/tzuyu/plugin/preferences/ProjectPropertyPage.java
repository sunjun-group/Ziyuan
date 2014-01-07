/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package tzuyu.plugin.preferences;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ProjectScope;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.dialogs.PropertyPage;
import org.eclipse.ui.preferences.ScopedPreferenceStore;

import tzuyu.plugin.TzuyuPlugin;
import tzuyu.plugin.command.gentest.GenTestPreferences;

/**
 * @author LLT
 *
 */
public class ProjectPropertyPage extends PropertyPage {
	private IProject project;
	private IPreferenceStore projectStore;
	private GenTestPreferences orgPrefs;
	private GenTestPreferences curPrefs;
	
	public ProjectPropertyPage() {
		super();
	}
	
	@Override
	protected Control createContents(Composite parent) {
		noDefaultAndApplyButton();
		
		IAdaptable resource = getElement();
		if (resource != null) {
			project = (IProject) resource.getAdapter(IProject.class);
		}
		initPreferenceStore(project);
		
		decorateContent();
		
		return parent;
	}

	private void decorateContent() {
		
	}

	private void initPreferenceStore(IProject curProject) {
		assert curProject != null;
		projectStore = new ScopedPreferenceStore(new ProjectScope(curProject),
				TzuyuPlugin.PLUGIN_ID);
		setPreferenceStore(projectStore);
		loadPreferences(curProject);
	}

	private void loadPreferences(IProject curProject) {
		orgPrefs = TzuyuPlugin.getDefault().getGenTestPreferences(curProject);
		curPrefs = orgPrefs.clone();
	}

}
