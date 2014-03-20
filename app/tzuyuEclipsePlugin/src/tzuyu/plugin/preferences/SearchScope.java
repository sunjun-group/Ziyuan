/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package tzuyu.plugin.preferences;

import tzuyu.engine.utils.Assert;
import tzuyu.engine.utils.StringUtils;

/**
 * @author LLT
 *
 */
public enum SearchScope {
	SOURCE,
	SOURCE_JARS,
	USER_DEFINED;
	
	public SearchScope findScope(String str) {
		Assert.assertTrue(!StringUtils.isEmpty(str));
		for (SearchScope scope : values()) {
			if (str.startsWith(scope.name())) {
				return scope;
			}
		}
		Assert.assertFail("Can not find scope with string: "+ str);
		return null; 
	}
}
