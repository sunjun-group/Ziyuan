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
public class NullTzPrintStream implements IPrintStream {

	public void print(byte b) {
	}

	public void print(char c) {
	}

	public void print(double d) {
	}

	public void print(String s) {
	}

	public void println(String s) {
	}

	public void println(Object[] e) {
	}

	@Override
	public IPrintStream writeln(String msg) {
		return this;
	}

}
