/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package tzuyu.plugin.tester.preferences;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ProjectScope;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.ui.dialogs.PropertyPage;
import org.eclipse.ui.preferences.ScopedPreferenceStore;

import tzuyu.plugin.TzuyuPlugin;
import tzuyu.plugin.commons.constants.Messages;
import tzuyu.plugin.tester.command.gentest.GenTestPreferences;
import tzuyu.plugin.tester.ui.AppEventManager;
import tzuyu.plugin.tester.ui.PropertyPanel;
import tzuyu.plugin.tester.ui.ValueChangedEvent;
import tzuyu.plugin.tester.ui.ValueChangedListener;

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
	private AppEventManager eventManager;
	
	public ProjectPropertyPage() {
		eventManager = new AppEventManager();
	}
	
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
		outputPanel.setEventManager(eventManager);
		TabItem outputTab = new TabItem(tabFolder, SWT.NONE);
		outputTab.setText(msg.gentest_prefs_tab_output());
		outputTab.setControl(outputPanel);
		
		// parameters tab
		paramPanel = new ParameterPanel(this, tabFolder);
		paramPanel.setEventManager(eventManager);
		TabItem paramTab = new TabItem(tabFolder, SWT.NONE);
		paramTab.setText(msg.gentest_prefs_tab_param());
		paramTab.setControl(paramPanel);
		registerListener();
	}

	private void registerListener() {
		registerStatusChangeListener(outputPanel);
		registerStatusChangeListener(paramPanel);
	}

	protected void registerStatusChangeListener(Object source) {
		eventManager.register(ValueChangedEvent.TYPE, new ValueChangedListener<IStatus[]>(source) {

			@Override
			public void onValueChanged(ValueChangedEvent<IStatus[]> event) {
				updateApplyButton();
			}
		});
	}

	private void initPreferenceStore(IProject curProject) {
		assert curProject != null;
		projectStore = new ScopedPreferenceStore(new ProjectScope(curProject), TzuyuPlugin.PLUGIN_ID);
		loadPreferences(curProject);
	}

	private void loadPreferences(IProject curProject) {
		orgPrefs = TzuyuPlugin.getDefault().getGenTestPreferences(JavaCore.create(curProject));
		curPrefs = orgPrefs.clone();
	}
	
	@Override
	protected void performDefaults() {
		TzPreferenceInitializer.restoreDefault(projectStore);
//		TzPreferenceInitializer.restoreDefault(TzuyuPlugin.getDefault().getPreferenceStore());
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
		if (!isValid()) {
			return false;
		}
		for (PropertyPanel<GenTestPreferences> widget : getWidgets()) {
			widget.performOk(curPrefs);
		}
		TzuyuPlugin.getDefault().persistGenTestPreferences(project, curPrefs);
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
