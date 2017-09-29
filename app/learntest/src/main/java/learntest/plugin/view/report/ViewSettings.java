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
	private GroupBy groupBy = GroupBy.PROJECT;

	public GroupBy getGroupBy() {
		return groupBy;
	}

	public void setGroupBy(GroupBy groupBy) {
		if (groupBy == null) {
			this.groupBy = GroupBy.PROJECT;
		} else {
			this.groupBy = groupBy;
		}
	}
	
	public static enum GroupBy {
		PROJECT,
		METHOD
	}
}
