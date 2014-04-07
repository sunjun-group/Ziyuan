/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package tzuyu.engine.main;

import org.apache.log4j.Logger;

import tzuyu.engine.iface.AbstractLogger;
import tzuyu.engine.iface.ILogger;
import tzuyu.engine.utils.Globals;
import tzuyu.engine.utils.StringUtils;

/**
 * @author LLT
 *
 */
public class CommandLineLogger extends AbstractLogger<CommandLineLogger>
		implements ILogger<CommandLineLogger> {
	private static final Logger logger = Logger.getRootLogger();
	private static final CommandLineLogger instance = new CommandLineLogger(); 
	
	public static CommandLineLogger instance() {
		return instance;
	}
	
	public CommandLineLogger info(Object... msgs) {
		String msg = StringUtils.spaceJoin(msgs);
		logger.info(msg);
		System.out.println(msg);
		return this;
	}
	
	public CommandLineLogger error(Object... msgs) {
		String msg = StringUtils.spaceJoin(msgs);
		logger.error(msg);
		System.out.println(msg);
		return this;
	}

	@Override
	public void close() {
		// do nothing
	}

	@Override
	public void logEx(Exception ex, String msg) {
		logger.error(msg, ex);
		ex.printStackTrace(System.err);
	}

	@Override
	protected void logEx(Exception ex, Enum<?> type) {
		logEx(ex, type == null ? "" : type.name());
	}

	@Override
	public void debug(String msg) {
		if (Globals.DEBUG) {
			info(msg);
		}
	}

	@Override
	protected boolean isDebug() {
		return Globals.DEBUG;
	}
}
