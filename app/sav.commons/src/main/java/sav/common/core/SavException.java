/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package sav.common.core;

import sav.common.core.utils.CollectionUtils;

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
	
	public SavException(String textFormat, Object... params) {
		this(getText(textFormat, params));
	}
	
	public static String getText(String textFormat, Object... params) {
		if (CollectionUtils.isEmpty(params)) {
			return textFormat;
		}
		return String.format(textFormat, (Object[])params);
	}
	
	public SavException(String msg) {
		this(msg, ModuleEnum.UNSPECIFIED);
	}
	
	public SavException(Exception ex) {
		this(ex, ModuleEnum.UNSPECIFIED);
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
