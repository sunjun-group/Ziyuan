/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package sav.java.parser.cfg;

/**
 * @author LLT
 *
 */
public interface CfgNode {
	
	public Type getType();
	
	public static enum Type {
		ENTRY,
		EXIT,
		PROCESS,
		DECISIONS
	}
}
