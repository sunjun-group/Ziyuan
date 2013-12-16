/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package lstar;

/**
 * @author LLT
 * 
 */
public class LStarException extends Exception {
	private static final long serialVersionUID = 1L;
	private Type type;
	
	public LStarException(Type type) {
		this.type = type;
	}
	
	public Type getType() {
		return type;
	}
	
	public enum Type {
		RestartLearning;
	}
}
