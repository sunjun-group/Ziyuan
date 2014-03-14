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

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaConventions;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jface.dialogs.DialogPage;
import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import tzuyu.engine.TzConfiguration;
import tzuyu.plugin.TzuyuPlugin;
import tzuyu.plugin.command.gentest.GenTestPreferences;
import tzuyu.plugin.core.constants.Messages;
import tzuyu.plugin.core.utils.IStatusUtils;
import tzuyu.plugin.preferences.component.CheckboxGroup;
import tzuyu.plugin.preferences.component.Dropdown;
import tzuyu.plugin.preferences.component.IntText;
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
	/* Parameter declaration format: */
	private Dropdown<ParamDeclarationFormat> paramDeclFormatComb;
	private Dropdown<TcPrintMode> tcPrintModeComb;
	
	private IntText maxMethods;

	private IntText maxLine;
	
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
		
		// pass fail testcases
		passFailCbGroup = new CheckboxGroup<TestCaseType>(contentPanel,
				msg.gentest_prefs_output_testcaseType_question(), colSpan);
		passFailCbGroup.addCb(msg.gentest_prefs_output_testcaseType_pass(), TestCaseType.PASS);
		passFailCbGroup.addCb(msg.gentest_prefs_output_testcaseType_fail(), TestCaseType.FAIL);
		
		Group formatGroup = SWTFactory.createGroup(contentPanel, StringUtils.EMPTY, colSpan);
		formatGroup.setLayout(new GridLayout(2, false));
		// parameter declaration format:
		SWTFactory.createLabel(formatGroup,
				msg.gentest_prefs_output_paramDeclarationFormat());
		paramDeclFormatComb = new Dropdown<ParamDeclarationFormat>(formatGroup,
				ParamDeclarationFormat.values());
		
		SWTFactory.createLabel(formatGroup,
				msg.gentest_prefs_output_testClassFormat());
		tcPrintModeComb = new Dropdown<OutputPanel.TcPrintMode>(formatGroup,
				TcPrintMode.values());
		
		Label maxMethodsLb = SWTFactory.createLabel(formatGroup, msg.gentest_prefs_output_maxMethodsPerClass());
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		maxMethodsLb.setLayoutData(gd);
		gd.minimumWidth = 60;
		maxMethods = new IntText(formatGroup, OutputField.MAX_METHODS_NUM);
//		SWTFactory.createLabel(formatGroup, msg.gentest_prefs_output_maxLinesPerClass());
		maxLine = new IntText(formatGroup, OutputField.MAX_LINE_NUM);
		maxLine.asWidget().setVisible(false);
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
				IPackageFragment pkg = event.getNewVal();
				if (pkg == null) {
					status = IStatusUtils.error(msg.packageEditor_errorMessage());
				} else {
					status = JavaConventions.validatePackageName(pkg.getElementName(), 
							project.getOption(JavaCore.COMPILER_SOURCE, true), 
							project.getOption(JavaCore.COMPILER_COMPLIANCE, true));
					if (status.isOK()) {
						status = IStatusUtils.OK_STATUS;
					}
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
		addModifyListener(OutputField.MAX_METHODS_NUM, maxMethods);
		addModifyListener(OutputField.MAX_LINE_NUM, maxLine);
	}
	
	@Override
	public void refresh(GenTestPreferences data) {
		folderEditor.setOutSourceFolder(data.getOutputFolder());
		packageEditor.setSelectedPackage(data.getOutputPackage());
		TzConfiguration tzConfig = data
				.getTzConfig();
		passFailCbGroup.setValue(TestCaseType.values(tzConfig.isPrintPassTests(),
				tzConfig.isPrintFailTests()));
		maxMethods.setValue(tzConfig.getMaxMethodsPerGenTestClass());
		maxLine.setValue(tzConfig.getMaxLinesPerGenTestClass());
		paramDeclFormatComb.setValue(ParamDeclarationFormat.getTypeIf(tzConfig.isLongFormat()));
		tcPrintModeComb.setValue(TcPrintMode.getTypeIf(tzConfig.isPrettyPrint())); 
	}

	@Override
	public void performOk(GenTestPreferences prefs) {
		prefs.setOutputFolder(folderEditor.getValue());
		prefs.setOutputPackage(packageEditor.getValue());
		List<TestCaseType> passFailValue = passFailCbGroup.getValue();
		TzConfiguration tzConfig = prefs.getTzConfig();
		tzConfig.setPrintPassTests(
				passFailValue.contains(TestCaseType.PASS));
		tzConfig.setPrintFailTests(
				passFailValue.contains(TestCaseType.FAIL));
		tzConfig.setMaxMethodsPerGenTestClass(maxMethods.getValue());
		tzConfig.setMaxLinesPerGenTestClass(maxLine.getValue());
		tzConfig.setLongFormat(
				paramDeclFormatComb.getValue().isLongFormat());
		tzConfig.setPrettyPrint(tcPrintModeComb.getValue() == TcPrintMode.PRETTY);
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
		PASS_FAIL,
		MAX_METHODS_NUM,
		MAX_LINE_NUM,
		FILE_FORMAT_LONG
	}

	@Override
	protected int getFieldNums() {
		return OutputField.values().length;
	}
	
	enum TcPrintMode {
		PRETTY,
		UNFORMATTED;

		public static TcPrintMode getTypeIf(boolean prettyPrint) {
			if (prettyPrint) {
				return PRETTY;
			}
			return UNFORMATTED;
		}
	}
}
