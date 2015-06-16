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
import java.util.List;

import sav.common.core.utils.CollectionUtils;

/**
 * @author khanh
 *
 */
public class ProgramArgumentBuilder {
	private List<String> arguments = new ArrayList<String>();

	public ProgramArgumentBuilder addArgument(String option, List<String> values) {
		if (!CollectionUtils.isEmpty(values)) {
			arguments.add("-" + option);
			arguments.addAll(values);
		}
		return this;
	}

	public ProgramArgumentBuilder addArgument(String option, String... values) {
		return addArgument(option, Arrays.asList(values));
	}

	public List<String> build() {
		return arguments;
	}

}
