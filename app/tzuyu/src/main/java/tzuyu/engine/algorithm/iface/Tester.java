/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package tzuyu.engine.algorithm.iface;

import tzuyu.engine.TzClass;
import tzuyu.engine.iface.HasReport;
import tzuyu.engine.model.Query;
import tzuyu.engine.model.QueryResult;
import tzuyu.engine.model.TzuYuAction;
import tzuyu.engine.model.TzuYuAlphabet;

/**
 * @author LLT
 * 
 */
public interface Tester extends HasReport<TzuYuAlphabet> {

	QueryResult test(Query query);

	boolean confirmWishfulThinking(TzuYuAction stmt);

	QueryResult executeAllOldTestCases();
	
	public void setProject(TzClass project);
}
