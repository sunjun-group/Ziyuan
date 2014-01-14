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
public class ValueChangedEvent<T> implements AppEvent {
	public static final String TYPE = "ValueChangedEvent";
	
	private T oldVal;
	private T newVal;
	private Object source;
	public ValueChangedEvent(Object source, T oldVal, T newVal) {
		this.oldVal = oldVal;
		this.newVal = newVal; 
		this.source = source;
	}
	
	public T getOldVal() {
		return oldVal;
	}

	public T getNewVal() {
		return newVal;
	}
	
	@Override
	public String getType() {
		return TYPE;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void execute(AppListener listener) {
		if (listener instanceof ValueChangedListener<?>) {
			ValueChangedListener<?> valueChangedListener = (ValueChangedListener<?>) listener;
			if (this.source.equals(valueChangedListener.getSource())) {
				((ValueChangedListener<T>)valueChangedListener).onValueChanged(this);
			}
		}
	}

}
