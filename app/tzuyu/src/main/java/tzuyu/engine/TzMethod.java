/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package tzuyu.engine;

/**
 * @author LLT
 * 
 */
public class TzMethod {
	private String name;
	private String signature;

	public TzMethod(String name, String signature) {
		this.name = name;
		this.signature = signature;
	}

	public String getName() {
		return name;
	}

	public String getSignature() {
		return signature;
	}

	public void setSignature(String signature) {
		this.signature = signature;
	}
}
