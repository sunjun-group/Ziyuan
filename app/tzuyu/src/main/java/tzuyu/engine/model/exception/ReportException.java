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
public class ReportException extends TzException {
	private static final long serialVersionUID = 1L;

	public ReportException(TzException ex) {
		super(ex.getType(), ex.getMessage());
	}
	
	public ReportException(String message) {
		super(message);
	}
	
}
