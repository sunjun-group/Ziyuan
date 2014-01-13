/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package tzuyu.plugin.preferences;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.ui.dialogs.PropertyPage;

import tzuyu.plugin.TzuyuPlugin;
import tzuyu.plugin.command.gentest.GenTestPreferences;
import tzuyu.plugin.core.constants.Messages;
import tzuyu.plugin.ui.PropertyPanel;

/**
 * @author LLT
 *
 */
public class ProjectPropertyPage extends PropertyPage {
	private OutputPanel outputPanel;
	private ParameterPanel paramPanel;
	
	private IProject project;
	private IPreferenceStore projectStore;
	private GenTestPreferences orgPrefs;
	private GenTestPreferences curPrefs;
	private Messages msg = TzuyuPlugin.getMessages();
	
	@Override
	protected Control createContents(Composite parent) {
		IAdaptable resource = getElement();
		if (resource != null) {
			project = (IProject) resource.getAdapter(IProject.class);
		}
		initPreferenceStore(project);
		
		decorateContent(parent);
		refreshData();
		return parent;
	}

	private void decorateContent(Composite parent) {
		// add tab
		TabFolder tabFolder = new TabFolder(parent, SWT.TOP);
		GridData layoutData = new GridData(GridData.FILL_HORIZONTAL
				| GridData.GRAB_HORIZONTAL | GridData.FILL_VERTICAL
				| GridData.GRAB_VERTICAL);
		tabFolder.setLayoutData(layoutData);
		// output tab
		outputPanel = new OutputPanel(this, tabFolder, JavaCore.create(project), getShell());
		TabItem outputTab = new TabItem(tabFolder, SWT.NONE);
		outputTab.setText(msg.gentest_prefs_tab_output());
		outputTab.setControl(outputPanel);
		
		// parameters tab
		paramPanel = new ParameterPanel(this, tabFolder);
		TabItem paramTab = new TabItem(tabFolder, SWT.NONE);
		paramTab.setText(msg.gentest_prefs_tab_param());
		paramTab.setControl(paramPanel);
	}

	private void initPreferenceStore(IProject curProject) {
		assert curProject != null;
		// TODO LLT: curProject null if select properties on class
//		projectStore = new ScopedPreferenceStore(new ProjectScope(curProject),
//				TzuyuPlugin.PLUGIN_ID);
//		setPreferenceStore(projectStore);
		loadPreferences(curProject);
	}

	private void loadPreferences(IProject curProject) {
		orgPrefs = TzuyuPlugin.getDefault().getGenTestPreferences(JavaCore.create(curProject));
		curPrefs = orgPrefs.clone();
	}
	
	@Override
	protected void performDefaults() {
		TzPreferenceInitializer.restoreDefault(projectStore);
		curPrefs = GenTestPreferences.createDefault(JavaCore.create(project));
		refreshData();
		super.performDefaults();
	}

	private void refreshData() {
		for (PropertyPanel<GenTestPreferences> widget : getWidgets()) {
			widget.refresh(curPrefs);
		}
	}
	
	@Override
	public boolean performOk() {
		boolean dirty = false;
		for (PropertyPanel<GenTestPreferences> widget : getWidgets()) {
			widget.performOk(curPrefs);
			dirty |= widget.isDirty();
		}
		if (dirty) {
			TzuyuPlugin.getDefault().persistGenTestPreferences(project, curPrefs);
		}
		return true;
	}

	@Override
	public boolean isValid() {
		for (PropertyPanel<GenTestPreferences> widget : getWidgets()) {
			if (!widget.isValid()) {
				return false;
			}
		}
		return true;
	}
	
	@SuppressWarnings("unchecked")
	private PropertyPanel<GenTestPreferences>[] getWidgets() {
		return new PropertyPanel[]{outputPanel, paramPanel};
	}
}
