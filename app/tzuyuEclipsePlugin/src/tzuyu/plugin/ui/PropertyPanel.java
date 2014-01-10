/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package tzuyu.plugin.ui;

import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

import tzuyu.plugin.TzuyuPlugin;
import tzuyu.plugin.core.constants.Messages;

/**
 * @author LLT
 *
 */
public abstract class PropertyPanel<T> extends Composite {
	protected Messages msg;
	protected boolean dirty = false;
	
	public PropertyPanel(Composite parent) {
		this(parent, SWT.NONE);
	}
	
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
	
	public PropertyPanel(Composite parent, int style) {
		super(parent, style);
		msg = TzuyuPlugin.getMessages();
	}

	public abstract void refresh(T data);

	public abstract boolean isValid();

	public abstract void performOk(T data);
	
	public abstract FieldEditor[] getFieldEditors();
}
