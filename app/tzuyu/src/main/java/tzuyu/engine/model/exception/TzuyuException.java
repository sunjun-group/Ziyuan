/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package tzuyu.engine.model.exception;

/**
 * @author LLT
 *
 */
public class TzuyuException extends Exception {
	private static final long serialVersionUID = 1L;
	private Type type;
	
	public TzuyuException(Type type, String msg) {
		this(msg);
		this.type = type;
	}
	
	public TzuyuException(String message) {
		super(message);
	}
	
	public Type getType() {
		return type;
	}

	public static void rethrow(Exception e) throws TzuyuException {
		throw new TzuyuException(e.getMessage());
	}

	public static enum Type {
		ParameterSelector_Fail_Init_Class
	}
}
