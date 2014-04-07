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
	
	public TzException(TzExceptionType type) {
		this(type, type.name());
	}
	
	public TzException(TzExceptionType type, String msg) {
		this(msg);
		this.type = type;
	}
	
	public TzException(String message) {
		super(message);
	}
	
	public TzException(LStarException e) {
		this(TzExceptionType.fromLStar(e.getType()));
	}

	public TzExceptionType getType() {
		return type;
	}

	public static void rethrow(Exception e) throws TzException {
		throw new TzException(e.getMessage());
	}
}
