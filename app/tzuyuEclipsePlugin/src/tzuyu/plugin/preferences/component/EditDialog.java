/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package tzuyu.plugin.preferences.component;

import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

import tzuyu.engine.utils.StringUtils;
import tzuyu.plugin.TzuyuPlugin;
import tzuyu.plugin.core.constants.Messages;

/**
 * @author LLT
 * 
 */
public abstract class EditDialog<T> extends TitleAreaDialog {
	protected OperationMode mode = OperationMode.NEW;
	protected Messages msg = TzuyuPlugin.getMessages();
	private T data;
	
	public EditDialog(Shell parentShell, T data) {
		super(parentShell);
		if (data != null) {
			this.data = data;
			mode = OperationMode.EDIT;
		} else {
			this.data = initData();
			mode = OperationMode.NEW;
		}
	}
	
	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		if (mode == OperationMode.NEW) {
			newShell.setText(msg.editDialog_new_title(getShellTitleSuffix(mode)));
		} else {
			newShell.setText(msg.editDialog_edit_title(getShellTitleSuffix(mode)));
		}
	}

	protected abstract T initData();

	@Override
	protected Control createDialogArea(Composite parent) {
		Composite content = (Composite) super.createDialogArea(parent);
		initializeDialogUnits(content);
		createContent(content);
		registerListener();
		refresh(data);
		setTitle(initShellTitle());
		return content;
	}
	
	public void setData(T data) {
		this.data = data;
		refresh(data);
	}
	
	@Override
	protected void okPressed() {
		String error = validate();
		if (error == null) {
			updateData(data);
			super.okPressed();
		} else {
			setErrorMessage(error);
		}
	}
	
	protected String validate() {
		return null;
	}

	protected abstract void refresh(T data);

	public T getData() {
		return data;
	}

	protected abstract void updateData(T data);

	protected void registerListener() {
		// Do nothing at this time.
	}

	protected String initShellTitle() {
		if (mode == OperationMode.NEW) {
			return msg.editDialog_new_title(getShellTitleSuffix(mode));
		}
		return msg.editDialog_edit_title(getShellTitleSuffix(mode));
	}

	protected String getShellTitleSuffix(OperationMode mode) {
		return StringUtils.EMPTY;
	}
	
	protected abstract void createContent(Composite parent);
	
	public void setMode(OperationMode mode) {
		this.mode = mode;
	}
	
	public enum OperationMode {
		NEW, READ_ONLY, EDIT 
	}
}
