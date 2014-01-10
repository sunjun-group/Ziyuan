/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package tzuyu.plugin.preferences;

import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jface.dialogs.DialogPage;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import tzuyu.engine.utils.StringUtils;
import tzuyu.plugin.TzuyuPlugin;
import tzuyu.plugin.command.gentest.GenTestPreferences;
import tzuyu.plugin.core.constants.Messages;
import tzuyu.plugin.ui.AppEventManager;
import tzuyu.plugin.ui.PropertyPanel;


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
	private Text className;
	private DialogPage msgContainer;
	
	private AppEventManager eventManager;
	
	public OutputPanel(Composite parent, IJavaProject project, Shell shell) {
		super(parent);
		
		eventManager = new AppEventManager();
		setLayout(new GridLayout());
		
		Label desc = new Label(this, SWT.NONE);
		desc.setText(msg.sourceFolderEditor_description());
		
		Composite contentPanel = new Composite(this, SWT.NONE);
		GridData layoutData = new GridData(GridData.FILL_HORIZONTAL
				| GridData.GRAB_HORIZONTAL | GridData.FILL_VERTICAL
				| GridData.GRAB_VERTICAL);
		contentPanel.setLayoutData(layoutData);
		this.shell = shell;
		this.project = project;
		decorateContent(contentPanel);
		classNameLb.setVisible(false);
		className.setVisible(false);
	}

	private void decorateContent(Composite contentPanel) {
		// target folder
		folderEditor = new SourceFolderEditor(contentPanel, project, shell);
		folderEditor.setLabelText(msg.gentest_prefs_output_folder());
		folderEditor.setEventManager(eventManager);
		
		// target package
		packageEditor = new PackageEditor(contentPanel);
		packageEditor.setLabelText(msg.gentest_prefs_output_package());
		packageEditor.setEventManager(eventManager);
		
		// test class name
		classNameLb = new Label(contentPanel, SWT.NONE);
		classNameLb.setText(msg.gentest_prefs_output_className());
		className = new Text(contentPanel, SWT.SINGLE | SWT.BORDER);
		className.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		className.addFocusListener(new FocusListener() {
			
			@Override
			public void focusLost(FocusEvent e) {
				validateClassName();
			}
			
			@Override
			public void focusGained(FocusEvent e) {
				validateClassName();
			}
		});
	}
	
	private void validateClassName() {
		String warningMsg = msg.gentest_prefs_output_warning_className_lowercase();
		String errorMsg = msg.gentest_prefs_output_error_className_empty();
		String text = StringUtils.toStringNullToEmpty(className.getText());
		// clear msg
		if (warningMsg.equals(msgContainer.getMessage())) {
			msgContainer.setMessage(null);
		}
		if (errorMsg.equals(msgContainer.getErrorMessage())) {
			msgContainer.setErrorMessage(null);
		}
		if (text.isEmpty()) {
			msgContainer.setErrorMessage(errorMsg);
		} else if (!StringUtils.isStartWithUppercaseLetter(text)) {
			msgContainer.setMessage(warningMsg, 
					IMessageProvider.WARNING);
		} 
	}
	
	public void setMessageContainer(DialogPage page) {
		this.msgContainer = page;
		folderEditor.setPage(page);
	}

	@Override
	public void refresh(GenTestPreferences data) {
		folderEditor.setOutSourceFolder(data.getOutputFolder());
		packageEditor.setSelectedPackage(data.getOutputPackage());
	}

	@Override
	public boolean isValid() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public void performOk(GenTestPreferences prefs) {
		prefs.setOutputFolder(folderEditor.getValue());
		prefs.setOutputPackage(packageEditor.getValue());
	}
	
	@Override
	protected void registerValueChangeListener() {
		super.registerValueChangeListener();
		className.addFocusListener(new FocusListener() {
			
			@Override
			public void focusLost(FocusEvent e) {
				dirty = true;
			}
			
			@Override
			public void focusGained(FocusEvent e) {
				dirty = true;
			}
		});
	}

	@Override
	public FieldEditor[] getFieldEditors() {
		return new FieldEditor[]{folderEditor, packageEditor};
	}
	
	
}
