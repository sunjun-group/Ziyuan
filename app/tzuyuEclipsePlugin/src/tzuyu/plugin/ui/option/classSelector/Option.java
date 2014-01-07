/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package tzuyu.plugin.ui.option.classSelector;

import tzuyu.plugin.core.dto.TzuyuPreferences;

/**
 * @author LLT
 * @author Peter Kalauskas (Randoop)
 */
public abstract class Option<T extends TzuyuPreferences> {
	public abstract void initFrom(T config);
}
