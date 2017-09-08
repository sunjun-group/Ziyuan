/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package sav.strategies.vm;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import sav.common.core.utils.CollectionUtils;
import sav.common.core.utils.StringUtils;

/**
 * @author khanh
 *
 */
public class ProgramArgumentBuilder {
	private List<String> arguments = new ArrayList<String>();

	public ProgramArgumentBuilder addArgument(String option, Collection<String> testClassNames) {
		if (!isBlank(testClassNames)) {
			arguments.add("-" + option);
			arguments.addAll(testClassNames);
		}
		return this;
	}

	private boolean isBlank(Collection<String> testClassNames) {
		if (CollectionUtils.isEmpty(testClassNames)) {
			return true;
		}
		for (String val : testClassNames) {
			if (!StringUtils.isEmpty(val)) {
				return false;
			}
		}
		return true;
	}

	public ProgramArgumentBuilder addArgument(String option, String... values) {
		return addArgument(option, Arrays.asList(values));
	}
	
	public ProgramArgumentBuilder addArgument(String option, long value) {
		return addArgument(option, String.valueOf(value));
	}
	
	public ProgramArgumentBuilder addOptionArgument(String option) {
		arguments.add("-" + option);
		return this;
	}

	public List<String> build() {
		return arguments;
	}

}
