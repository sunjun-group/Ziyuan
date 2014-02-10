/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package tzuyu.engine.main;

import org.apache.log4j.Logger;

import tzuyu.engine.iface.ILogger;
import tzuyu.engine.utils.StringUtils;

/**
 * @author LLT
 *
 */
public class CommandLineLogger implements ILogger<CommandLineLogger> {
	private static final Logger logger = Logger.getRootLogger();
	private static final CommandLineLogger instant = new CommandLineLogger(); 
	
	public static CommandLineLogger instance() {
		return instant;
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

}
