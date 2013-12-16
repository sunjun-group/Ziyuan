/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package tzuyu.engine.iface.algorithm;

import tzuyu.engine.TzProject;
import tzuyu.engine.iface.HasTzReport;
import tzuyu.engine.model.Query;
import tzuyu.engine.model.QueryResult;
import tzuyu.engine.model.TzuYuAction;

/**
 * @author LLT
 * 
 */
public interface Tester extends HasTzReport {

	QueryResult test(Query query);

	boolean confirmWishfulThinking(TzuYuAction stmt);

	QueryResult executeAllOldTestCases();
	
	public void setProject(TzProject project);
}
