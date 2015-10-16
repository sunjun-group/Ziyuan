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
public class CfgExitNode implements CfgNode {

	@Override
	public Type getType() {
		return Type.EXIT;
	}

}
