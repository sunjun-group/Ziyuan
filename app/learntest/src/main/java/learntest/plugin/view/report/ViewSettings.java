/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package learntest.plugin.view.report;

/**
 * @author LLT
 *
 */
public class ViewSettings {
	private GroupBy groupBy = getDefault();

	public GroupBy getGroupBy() {
		return groupBy;
	}

	public void setGroupBy(GroupBy groupBy) {
		if (groupBy == null) {
			this.groupBy = getDefault();
		} else {
			this.groupBy = groupBy;
		}
	}
	
	private static GroupBy getDefault() {
		return GroupBy.METHOD;
	}
	
	public static enum GroupBy {
		PROJECT,
		METHOD
	}
}
