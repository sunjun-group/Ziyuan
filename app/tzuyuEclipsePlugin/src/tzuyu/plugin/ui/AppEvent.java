/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package tzuyu.plugin.ui;

/**
 * @author LLT
 *
 */
public interface AppEvent {
	public String getType();

	public void execute(AppListener listener);
}
