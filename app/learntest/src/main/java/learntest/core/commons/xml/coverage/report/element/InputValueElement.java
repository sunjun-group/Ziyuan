/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package learntest.core.commons.xml.coverage.report.element;

import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * @author LLT
 *
 */
public class InputValueElement extends AbstractXmlElement {
	private String varId;
	private String value;
	
	public InputValueElement(String varId, String value) {
		this.varId = varId;
		this.value = value;
	}
	
	@Override
	protected void appendAttributes(Document doc, Element element) {
		appendAttributes(doc, element, "name", varId);
		appendAttributes(doc, element, "value", value);
	}

	@Override
	protected List<? extends AbstractXmlElement> getChildren() {
		return null;
	}

	@Override
	protected String getTagName() {
		return "input";
	}

}
