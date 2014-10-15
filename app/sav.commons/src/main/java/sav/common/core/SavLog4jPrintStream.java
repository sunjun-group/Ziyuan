/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package sav.common.core;

import org.apache.log4j.Logger;

/**
 * @author LLT
 *
 */
public class SavLog4jPrintStream extends AbstractPrintStream {
	private static final Logger logger = Logger.getRootLogger();
	
	@Override
	public void print(byte b) {
		logger.debug(b);
	}

	@Override
	public void print(char c) {
		logger.debug(c);
	}

	@Override
	public void print(double d) {
		logger.debug(d);
	}

	@Override
	public void print(String s) {
		logger.debug(s);
	}

	@Override
	public void println(String s) {
		logger.debug(s);
	}

	@Override
	public void println(Object[] e) {
		logger.debug(e);
	}

}
