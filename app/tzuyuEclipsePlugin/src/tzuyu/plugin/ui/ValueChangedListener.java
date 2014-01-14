/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package tzuyu.plugin.ui;

/**
 * @author LLT
 *
 */
public abstract class ValueChangedListener<T> implements AppListener {
	private Object source;
	
	public ValueChangedListener(Object source) {
		this.source = source;
	}
	
	public abstract void onValueChanged(ValueChangedEvent<T> event);
	
	public Object getSource() {
		return source;
	}
}
