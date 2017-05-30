/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package learntest.core.commons.xml.coverage.report.element;

import java.util.List;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import sav.common.core.utils.CollectionUtils;

/**
 * @author LLT
 *
 */
public abstract class AbstractXmlElement {
	
	public Node toXml(Document doc) {
		Element element = doc.createElement(getTagName());
		appendAttributes(doc, element);
		/* children */
		appendChildren(doc, element);
		return element;
	}

	protected void appendChildren(Document doc, Element element) {
		for (AbstractXmlElement child : CollectionUtils.nullToEmpty(getChildren())) {
			Node node = child.toXml(doc);
			element.appendChild(node);
		}
	}
	
	protected void appendAttributes(Document doc, Element element, String attName, String attVal) {
		Attr nameAttr = doc.createAttribute(attName);
		nameAttr.setValue(attVal);
		element.setAttributeNode(nameAttr);
	}
	
	protected void appendNameElement(Document doc, Element element, String name) {
		appendElement(doc, element, "name", name);
	}
	
	protected void appendElement(Document doc, Element element, String tagName, String value) {
		Element nameEle = doc.createElement(tagName);
		nameEle.appendChild(doc.createTextNode(value));
		element.appendChild(nameEle);
	}
	
	protected abstract List<? extends AbstractXmlElement> getChildren();

	protected void appendAttributes(Document doc, Element element) {
		// do nothing by default.
	}

	protected abstract String getTagName();
}
