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
public abstract class EditDialog extends TitleAreaDialog {
	private OperationMode mode = OperationMode.NEW;
	protected Messages msg = TzuyuPlugin.getMessages();
	
	public EditDialog(Shell parentShell) {
		super(parentShell);
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Composite content = (Composite) super.createDialogArea(parent);
		initializeDialogUnits(content);
		setTitle(initTitle());
		createContent(content);
		registerListener();
		return content;
	}

	protected void registerListener() {
		// Do nothing at this time.
	}

	protected String initTitle() {
		if (mode == OperationMode.NEW) {
			return msg.editDialog_new_title(getTitleSuffix(mode));
		}
		return msg.editDialog_edit_title(getTitleSuffix(mode));
	}

	protected String getTitleSuffix(OperationMode mode) {
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
