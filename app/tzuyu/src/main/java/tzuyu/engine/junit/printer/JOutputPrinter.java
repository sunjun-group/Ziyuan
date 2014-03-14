/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package tzuyu.engine.junit.printer;

import tzuyu.engine.utils.Globals;

/**
 * @author LLT
 * 
 */
public abstract class JOutputPrinter {

	public JOutputPrinter newLine() {
		return append(Globals.lineSep);
	}

	public JOutputPrinter tab() {
		return append("\t");
	}

	public abstract JOutputPrinter append(String str);

	public void close() {

	}
}
