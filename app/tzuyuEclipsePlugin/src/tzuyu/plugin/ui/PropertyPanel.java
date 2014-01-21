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
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.widgets.Composite;

import tzuyu.plugin.TzuyuPlugin;
import tzuyu.plugin.core.constants.Messages;
import tzuyu.plugin.core.utils.IStatusUtils;
import tzuyu.plugin.preferences.component.IntText;

/**
 * @author LLT
 *
 */
public abstract class PropertyPanel<T> extends Composite {
	protected Messages msg;
	protected DialogPage messageContainer;
	private IStatus[] statusArr;
	protected AppEventManager eventManager;
	private boolean autoUpdateContainerMsg = true;
	
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
		// only update message container page if this flat is on
		// (in case of wizard, we prefer to let the wizard page update by itself,
		// using event handler
		if (autoUpdateContainerMsg) {
			IStatus mostSevere = StatusUtil.getMostSevere(statusArr);
			if (mostSevere.getSeverity() == IStatus.ERROR) {
				messageContainer.setErrorMessage(mostSevere.getMessage());
			} else if (mostSevere.getSeverity() == IStatus.WARNING) {
				messageContainer.setMessage(mostSevere.getMessage(),
						IMessageProvider.WARNING);
			} else {
				messageContainer.setErrorMessage(null);
			}
		}
		fireEvent(new ValueChangedEvent<IStatus[]>(this, null,
					statusArr));
	}
	
	protected void addModifyListener(final Enum<?> field, final IntText txt) {
		txt.addModifyListener(new ModifyListener() {

			public void modifyText(ModifyEvent e) {
				updateStatus(field, txt.validate());
			}
		});
	}
	
	public void fireEvent(AppEvent event) {
		if (eventManager != null) {
			eventManager.fireEvent(event);
		}
	}
	
	protected abstract int getFieldNums();
	
	public boolean isDirty() {
		return true;
	}
	
	public abstract void refresh(T data);

	public boolean isValid() {
		if (statusArr == null) {
			return true;
		}
		for (IStatus status : statusArr) {
			if (status != IStatusUtils.OK_STATUS) {
				return false;
			}
		}
		return true;
	}

	public abstract void performOk(T data);
	
	public FieldEditor[] getFieldEditors() {
		return new FieldEditor[]{};
	}
	
	public void setEventManager(AppEventManager eventManager) {
		this.eventManager = eventManager;
	}
	
	public void setAutoUpdateContainerMsg(boolean autoUpdateContainerMsg) {
		this.autoUpdateContainerMsg = autoUpdateContainerMsg;
	}
}
