/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package sav.common.core;

/**
 * @author LLT
 *
 */
public class SavException extends Exception {
	private static final long serialVersionUID = 1L;
	protected Enum<?> type;
	protected Object[] params;
	
	public SavException(Enum<?> type, Object... params) {
		this.type = type;
		this.params = params;
	}
	
	public SavException(Exception e) {
		super(e);
		params = new Object[]{e.getClass().getSimpleName()};
	}

	public Enum<?> getType() {
		return type;
	}

	public Object[] getParams() {
		return params;
	}
}
