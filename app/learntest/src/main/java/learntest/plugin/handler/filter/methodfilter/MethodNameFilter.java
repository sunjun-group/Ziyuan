/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package learntest.plugin.handler.filter.methodfilter;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import learntest.plugin.utils.IMethodUtils;

/**
 * @author LLT
 *
 */
public class MethodNameFilter implements IMethodFilter {
	private static Logger log = LoggerFactory.getLogger(MethodNameFilter.class);
	private Collection<String> specificMethods = Collections.EMPTY_LIST;
	boolean filterKind; // if TRUE, only reserve specialClasses, otherwise discard specialClasses
	
	public MethodNameFilter(String excludedFileName, boolean filterKind) {
		this.filterKind = filterKind;
		File file = new File(excludedFileName);
		try {
			List<?> lines = FileUtils.readLines(file);
			specificMethods = new ArrayList<String>(lines.size());
			for (Object line : lines) {
				String methodId = (String) line;
				specificMethods.add(methodId);
				
			}
		} catch (IOException e) {
			log.debug("cannot load file {}", excludedFileName);
			// ignore
			// TODO LLT: allow to configure in target eclipse.
		}
	}
	
	public MethodNameFilter(Collection<String> excludedMethods) {
		this.specificMethods = excludedMethods;
	}

	@Override
	public boolean isValid(CompilationUnit cu, MethodDeclaration method) {
		String methodId = IMethodUtils.getMethodId(cu, method);
		if (specificMethods.contains(methodId)) {
			log.debug(filterKind + " specific method: {}", methodId);
			return filterKind;
		}
		return !filterKind;
	}


}
