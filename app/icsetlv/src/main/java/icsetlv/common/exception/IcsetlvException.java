/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package icsetlv.common.exception;


/**
 * @author LLT
 * 
 */
public class IcsetlvException extends Exception {
	private static final long serialVersionUID = 1L;
	
	public IcsetlvException(Throwable ex, String msg) {
		super(msg, ex);
	}
	
	public IcsetlvException(Throwable ex) {
		super(ex);
	}
	
	public IcsetlvException(String msg) {
		super(msg);
	}

	public static void rethrow(Throwable e) throws IcsetlvException {
		throw new IcsetlvException(e);
	}
	
	public static void rethrow(Throwable e, String msg) throws IcsetlvException {
		throw new IcsetlvException(e, msg);
	}

}
