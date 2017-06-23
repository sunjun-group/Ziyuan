/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package tzuyu.engine.iface;

import tzuyu.engine.TzClass;
import tzuyu.engine.model.exception.ReportException;
import tzuyu.engine.model.exception.TzException;

/**
 * @author LLT
 * 
 */
public interface TzuyuEngine {

	/**
	 * generate testcases randomly
	 */
	void generateTest(TzClass project) throws TzException;

	void dfaLearning(TzClass project) throws ReportException,
			InterruptedException, TzException;

}
