/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package cfgcoverage.jacoco;

import sav.common.core.SystemVariables;
import sav.strategies.dto.AppJavaClassPath;

/**
 * @author LLT
 * 
 */
public class CfgJaCoCoConfigs {
	/*
	 * boolean: indicate whether a testcase with same probes (with previous one)
	 * should still be counted
	 */
	public static final String DUPLICATE_FILTER = "duplicate_filter";
	private boolean duplicateFilter;
	private long timeout;
	
	public CfgJaCoCoConfigs(AppJavaClassPath appClasspath) {
		duplicateFilter = appClasspath.getPreferences().<Boolean>get(DUPLICATE_FILTER, false);
		timeout = appClasspath.getPreferences().get(SystemVariables.TESTCASE_TIMEOUT);
	}

	public boolean needToFilterDuplicate() {
		return duplicateFilter;
	}

	public void setDuplicateFilter(boolean duplicateFilter) {
		this.duplicateFilter = duplicateFilter;
	}

	public long getTimeout() {
		return timeout;
	}

	public void setTimeout(long timeout) {
		this.timeout = timeout;
	}
	
}
