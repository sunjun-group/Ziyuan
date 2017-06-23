/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package tzuyu.engine.junit.printer;

/**
 * @author LLT
 *
 */
public class JStrOutputPrinter extends JOutputPrinter {
	private StringBuilder sb;

	public JStrOutputPrinter(StringBuilder sb) {
		this.sb = sb;
	}
	
	@Override
	public JOutputPrinter append(String str) {
		sb.append(str);
		return this;
	}
}
