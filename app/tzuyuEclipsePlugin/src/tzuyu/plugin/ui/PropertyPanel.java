/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package tzuyu.plugin.ui;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.jdt.internal.ui.dialogs.StatusUtil;
import org.eclipse.jface.dialogs.DialogPage;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

import tzuyu.plugin.TzuyuPlugin;
import tzuyu.plugin.core.constants.Messages;
import tzuyu.plugin.core.utils.IStatusUtils;

/**
 * @author LLT
 *
 */
public abstract class PropertyPanel<T> extends Composite {
	protected Messages msg;
	protected boolean dirty = false;
	protected DialogPage messageContainer;
	private IStatus[] statusArr;
	
	public PropertyPanel(Composite parent, DialogPage messageContainer) {
		this(parent, SWT.NONE);
		if (getFieldNums() > 0) {
			statusArr = new IStatus[getFieldNums()];
			for (int i = 0; i < getFieldNums(); i++) {
				statusArr[i] = IStatusUtils.OK_STATUS;
			}
		}
		this.messageContainer = messageContainer;
	}
	
	private PropertyPanel(Composite parent, int style) {
		super(parent, style);
		msg = TzuyuPlugin.getMessages();
	}
	
	@SuppressWarnings("restriction")
	protected void updateStatus(Enum<?> field, IStatus status) {
		if (statusArr == null) {
			return;
		}
		statusArr[field.ordinal()] = status;
		IStatus mostSevere = StatusUtil.getMostSevere(statusArr);
		if (mostSevere.getSeverity() == IStatus.ERROR) {
			messageContainer.setErrorMessage(mostSevere.getMessage());
		} else if (mostSevere.getSeverity() == IStatus.WARNING) {
			messageContainer.setMessage(mostSevere.getMessage(), IMessageProvider.WARNING);
		} else {
			messageContainer.setErrorMessage(null);
		}
	}
	
	protected abstract int getFieldNums();
	
	protected void registerValueChangeListener() {
		for (FieldEditor editor : getFieldEditors()) {
			editor.setPropertyChangeListener(new IPropertyChangeListener() {
				
				@Override
				public void propertyChange(PropertyChangeEvent event) {
					dirty = true;
				}
			});
		}
	}
	
	public boolean isDirty() {
		return true;
	}
	
	public abstract void refresh(T data);

	public abstract boolean isValid();

	public abstract void performOk(T data);
	
	public FieldEditor[] getFieldEditors() {
		return new FieldEditor[]{};
	}
}
