/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package learntest.core.commons.exception;

/**
 * @author LLT
 *
 */
public class LearnTestException extends Exception {
	private static final long serialVersionUID = 1L;

	public LearnTestException(String msg) {
		super(msg);
	}

	public LearnTestException(Exception e) {
		super(e);
	}

	public LearnTestException(String message, Throwable cause) {
		super(message, cause);
	}

}
