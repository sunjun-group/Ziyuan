/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package learntest.handler.filter.methodfilter;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.jdt.core.dom.MethodDeclaration;

/**
 * @author LLT
 *
 */
public class MethodNameFilter implements TargetMethodFilter {
	private Set<String> excludedMethods;
	
	public MethodNameFilter() {
		excludedMethods = new HashSet<String>();
		excludedMethods.add("demuxOutput");
	}
	

	@Override
	public boolean isValid(MethodDeclaration md) {
		return !excludedMethods.contains(md.getName().toString());
	}
	

}
