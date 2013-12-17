/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package tzuyu.engine.iface;

import java.util.List;

import lstar.ReportHandler;
import tzuyu.engine.model.Sequence;
import tzuyu.engine.model.TzuYuAlphabet;

/**
 * @author LLT
 *
 */
public interface TzReportHandler extends ReportHandler<TzuYuAlphabet>{

	void writeTestCases(List<Sequence> allTestCases);
	
}
