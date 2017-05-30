/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package learntest.core.commons.xml.coverage.report.element;

import java.util.List;

import sav.common.core.utils.CollectionUtils;

/**
 * @author LLT
 *
 */
public class MethodsElement extends AbstractXmlElement {
	private List<MethodElement> methods;
	
	@Override
	protected List<MethodElement> getChildren() {
		return methods;
	}
	
	protected String getTagName() {
		return "methods";
	}
	
	public void setMethods(List<MethodElement> methods) {
		this.methods = methods;
	}

	public void add(MethodElement methodElement) {
		methods = CollectionUtils.initIfEmpty(methods);
		methods.add(methodElement);
	}
}
