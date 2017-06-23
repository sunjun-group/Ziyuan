/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package learntest.core.commons.xml.coverage.report.element;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import sav.common.core.utils.CollectionUtils;

/**
 * @author LLT
 *
 */
public class MethodElement extends AbstractXmlElement {
	private int totalTcs;
	private String name;
	private ParametersElement params;
	private BranchesElement branches;
	
	@Override
	protected void appendAttributes(Document doc, Element element) {
		appendAttributes(doc, element, "name", name);
	}

	@Override
	protected void appendChildren(Document doc, Element element) {
		appendElement(doc, element, "totalTestCases", String.valueOf(totalTcs));
		super.appendChildren(doc, element);
	}
	
	@Override
	protected List<? extends AbstractXmlElement> getChildren() {
		List<AbstractXmlElement> children = new ArrayList<AbstractXmlElement>();
		CollectionUtils.addIfNotNull(children, params);
		CollectionUtils.addIfNotNull(children, branches);
		return children;
	}
	

	@Override
	protected String getTagName() {
		return "method";
	}

	public void setTotalTcs(int totalTcs) {
		this.totalTcs = totalTcs;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setParams(ParametersElement params) {
		this.params = params;
	}

	public void setBranches(BranchesElement branches) {
		this.branches = branches;
	}

}
