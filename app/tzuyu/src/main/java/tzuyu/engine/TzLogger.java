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
		logger.info(StringUtils.spaceJoin(msgs));
		return this;
	}
	
	public TzLogger error(Object... msgs) {
		logger.error(StringUtils.spaceJoin(msgs));
		return this;
	}
}
