/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package tzuyu.engine.model.exception;

import lstar.LStarException;

/**
 * @author LLT
 *
 */
public class TzException extends Exception {
	private static final long serialVersionUID = 1L;
	private TzExceptionType type;
	private Object[] params;
	
	public TzException(TzExceptionType type, Object... params) {
		this.type = type;
		this.params = params;
	}
	
	public TzException(Exception e) {
		super(e);
		type = TzExceptionType.RETHROW;
		params = new Object[]{e.getClass().getSimpleName()};
	}
	
	public TzException(LStarException e) {
		this(TzExceptionType.fromLStar(e.getType()));
	}

	public TzExceptionType getType() {
		return type;
	}
	
	public Object[] getParams() {
		return params;
	}

	public static void rethrow(Exception e) throws TzException {
		throw new TzException(e);
	}
}
