/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package tzuyu.engine.model.exception;

import lstar.LStarException;
import sav.common.core.SavException;

/**
 * @author LLT
 *
 */
public class TzException extends SavException {
	private static final long serialVersionUID = 1L;
	
	public TzException(TzExceptionType type, Object... params) {
		super(type, params);
	}
	
	public TzException(Exception e) {
		super(TzExceptionType.RETHROW, e);
		type = TzExceptionType.RETHROW;
	}
	
	public TzException(LStarException e) {
		this(TzExceptionType.fromLStar(e.getType()));
	}
	
	public static void rethrow(Exception e) throws TzException {
		throw new TzException(e);
	}
}
