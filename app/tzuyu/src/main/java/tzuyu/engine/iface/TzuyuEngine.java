/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package tzuyu.engine.iface;

import tzuyu.engine.model.exception.ReportException;
import tzuyu.engine.model.exception.TzException;

/**
 * @author LLT
 * 
 */
public interface TzuyuEngine {
	public void run() throws ReportException, InterruptedException, TzException;
}
