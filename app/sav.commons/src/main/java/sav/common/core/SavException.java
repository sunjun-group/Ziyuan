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
	
	public SavException(String msg, Enum<?> type) {
		super(msg);
		this.type = type;
	}
	
	public SavException(Exception ex, Enum<?> type, Object... params) {
		super(ex);
		this.type = type;
		this.params = params;
		if (params == null) {
			params = new Object[] { ex.getClass().getSimpleName() };
		}
	}

	public Enum<?> getType() {
		return type;
	}

	public Object[] getParams() {
		return params;
	}
	
	@Override
	public String getMessage() {
		return super.getMessage();
	}

	public static boolean isExceptionOfType(Exception e, Enum<?> type) {
		return ((e instanceof SavException) && ((SavException) e).type == type);
	}
	
}
