/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package tzuyu.engine.iface;


/**
 * @author LLT
 *
 */
public abstract class AbstractPrintStream implements IPrintStream{
	
	public AbstractPrintStream writeln(String msg) {
		println(msg);
		return this;
	}
}
