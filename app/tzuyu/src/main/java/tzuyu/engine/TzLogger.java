/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package tzuyu.engine;

import org.apache.log4j.Logger;

import tzuyu.engine.utils.StringUtils;

/**
 * @author LLT
 *
 */
public class TzLogger {
	private static final Logger logger = Logger.getRootLogger();
	private static final TzLogger instant = new TzLogger(); 
	
	private TzLogger() { }
	
	public static TzLogger log() {
		return instant;
	}
	
	public TzLogger info(Object... msgs) {
		String msg = StringUtils.spaceJoin(msgs);
		logger.info(msg);
		System.out.println(msg);
		return this;
	}
	
	public TzLogger error(Object... msgs) {
		String msg = StringUtils.spaceJoin(msgs);
		logger.error(msg);
		System.out.println(msg);
		return this;
	}
	
}
