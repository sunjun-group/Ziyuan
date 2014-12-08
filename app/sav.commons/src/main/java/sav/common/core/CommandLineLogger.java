/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package sav.common.core;

import org.apache.log4j.Logger;

import sav.common.core.utils.StringUtils;

/**
 * @author LLT
 *
 */
public class CommandLineLogger extends sav.common.core.Logger<CommandLineLogger> {
	private static final Logger logger = Logger.getRootLogger();
	private static final CommandLineLogger instance = new CommandLineLogger(); 
	
	public CommandLineLogger() {
		debug = logger.isDebugEnabled();
	}
	
	public static CommandLineLogger instance() {
		return instance;
	}
	
	public CommandLineLogger info(Object... msgs) {
		String msg = StringUtils.spaceJoin(msgs);
		logger.info(msg);
		return this;
	}
	
	@Override
	public CommandLineLogger warn(Object... msgs) {
		logger.warn(StringUtils.spaceJoin(msgs));
		return this;
	}
	
	public CommandLineLogger error(Object... msgs) {
		String msg = StringUtils.spaceJoin(msgs);
		logger.error(msg);
		return this;
	}

	@Override
	public void logEx(Exception ex, String msg) {
		logger.error(msg, ex);
	}

	@Override
	protected void logEx(Exception ex, Enum<?> type) {
		logEx(ex, type == null ? "" : type.name());
	}

	@Override
	public CommandLineLogger debug(String msg) {
		logger.debug(msg);
		return this;
	}

}
