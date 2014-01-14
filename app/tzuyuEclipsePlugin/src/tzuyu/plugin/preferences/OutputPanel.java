/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package tzuyu.plugin.preferences;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jface.dialogs.DialogPage;
import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import tzuyu.plugin.TzuyuPlugin;
import tzuyu.plugin.command.gentest.GenTestPreferences;
import tzuyu.plugin.core.constants.Messages;
import tzuyu.plugin.core.utils.IStatusUtils;
import tzuyu.plugin.preferences.component.CheckboxGroup;
import tzuyu.plugin.ui.AppEventManager;
import tzuyu.plugin.ui.PropertyPanel;
import tzuyu.plugin.ui.SWTFactory;
import tzuyu.plugin.ui.ValueChangedEvent;
import tzuyu.plugin.ui.ValueChangedListener;

/**
 * @author LLT
 *
 */
public class OutputPanel extends PropertyPanel<GenTestPreferences> {
	private Messages msg = TzuyuPlugin.getMessages();
	
	private IJavaProject project;
	private Shell shell;
	private SourceFolderEditor folderEditor;
	private PackageEditor packageEditor;
	private Label classNameLb;
	private Text classNameTx;
	private CheckboxGroup<TestCaseType> passFailCbGroup;
	
	public OutputPanel(DialogPage msgContainer, Composite parent, IJavaProject project, Shell shell) {
		super(parent, msgContainer);
		
		eventManager = new AppEventManager();
		setLayout(new GridLayout());
		
		// output folder panel
		Composite contentPanel = new Composite(this, SWT.NONE);
		GridData layoutData = new GridData(GridData.FILL_HORIZONTAL
				| GridData.GRAB_HORIZONTAL | GridData.FILL_VERTICAL
				| GridData.GRAB_VERTICAL);
		contentPanel.setLayoutData(layoutData);
		this.shell = shell;
		this.project = project;
		decorateContent(contentPanel);
		classNameLb.setVisible(false);
		classNameTx.setVisible(false);
	}
	
	private void decorateContent(Composite contentPanel) {
		int colSpan = 3;
		SWTFactory.createHorizontalSpacer(contentPanel, colSpan);
		// target folder
		folderEditor = new SourceFolderEditor(contentPanel, project, shell);
		folderEditor.setLabelText(msg.gentest_prefs_output_folder());
		folderEditor.setEventManager(eventManager);
		
		// target package
		packageEditor = new PackageEditor(contentPanel, eventManager);
		packageEditor.setLabelText(msg.gentest_prefs_output_package());
		packageEditor.setEventManager(eventManager);
		
		// test class name
		classNameLb = new Label(contentPanel, SWT.NONE);
		classNameLb.setText(msg.gentest_prefs_output_className());
		classNameTx = new Text(contentPanel, SWT.SINGLE | SWT.BORDER);
		classNameTx.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		passFailCbGroup = new CheckboxGroup<TestCaseType>(contentPanel,
				msg.gentest_prefs_output_testcaseType_question(), colSpan);
		passFailCbGroup.addCb(msg.gentest_prefs_output_testcaseType_pass(), TestCaseType.PASS);
		passFailCbGroup.addCb(msg.gentest_prefs_output_testcaseType_fail(), TestCaseType.FAIL);
		registerListener();
	}

	private void registerListener() {
		eventManager.register(ValueChangedEvent.TYPE, new ValueChangedListener<IPackageFragmentRoot>(folderEditor) {

			@Override
			public void onValueChanged(
					ValueChangedEvent<IPackageFragmentRoot> event) {
				IStatus status = IStatusUtils.OK_STATUS;
				if (event.getNewVal() == null) {
					status = IStatusUtils.error(msg.sourceFolderEditor_errorMessage());
				}
				updateStatus(OutputField.FOLDER, status);
			}
		});
		
		eventManager.register(ValueChangedEvent.TYPE, new ValueChangedListener<IPackageFragment>(packageEditor) {

			@Override
			public void onValueChanged(ValueChangedEvent<IPackageFragment> event) {
				IStatus status = IStatusUtils.OK_STATUS;
				if (event.getNewVal() == null) {
					status = IStatusUtils.error(msg.packageEditor_errorMessage());
				}
				updateStatus(OutputField.PACKAGE, status);
			}
		});
		
		passFailCbGroup.addValueChangedListener(new ValueChangedListener<List<TestCaseType>>(passFailCbGroup) {

			@Override
			public void onValueChanged(
					ValueChangedEvent<List<TestCaseType>> event) {
				IStatus status = IStatusUtils.OK_STATUS;
				if (event.getNewVal().isEmpty()) {
					status = IStatusUtils.error(msg.gentest_prefs_output_error_testCaseType_empty());
				}
				updateStatus(OutputField.PASS_FAIL, status);
			}
		}, true);
	}
	
	@Override
	public void refresh(GenTestPreferences data) {
		folderEditor.setOutSourceFolder(data.getOutputFolder());
		packageEditor.setSelectedPackage(data.getOutputPackage());
		passFailCbGroup.setValue(TestCaseType.values(data.getTzConfig().isPrintPassTests(),
				data.getTzConfig().isPrintFailTests()));
	}

	@Override
	public void performOk(GenTestPreferences prefs) {
		prefs.setOutputFolder(folderEditor.getValue());
		prefs.setOutputPackage(packageEditor.getValue());
		List<TestCaseType> passFailValue = passFailCbGroup.getValue();
		prefs.getTzConfig().setPrintPassTests(passFailValue.contains(TestCaseType.PASS));
		prefs.getTzConfig().setPrintFailTests(passFailValue.contains(TestCaseType.FAIL));
	}

	@Override
	public FieldEditor[] getFieldEditors() {
		return new FieldEditor[]{folderEditor, packageEditor};
	}
	
	private enum TestCaseType {
		PASS, FAIL;

		public static List<TestCaseType> values(boolean printPassTests,
				boolean printFailTests) {
			List<TestCaseType> types = new ArrayList<OutputPanel.TestCaseType>();
			if (printFailTests) {
				types.add(FAIL);
			}
			if (printPassTests) {
				types.add(PASS);
			}
			return types;
		}
	}
	
	private enum OutputField {
		FOLDER,
		PACKAGE,
		PASS_FAIL
	}

	@Override
	protected int getFieldNums() {
		return OutputField.values().length;
	}
}
