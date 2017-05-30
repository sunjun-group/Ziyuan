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
public class ParameterElement extends AbstractXmlElement {
	private String name;
	private String type; 
	
	public ParameterElement(String name, String type) {
		this.name = name;
		this.type = type;
	}

	@Override
	protected void appendChildren(Document doc, Element element) {
		Element nameEle = doc.createElement("name");
		nameEle.appendChild(doc.createTextNode(name));
		element.appendChild(nameEle);
		
		Element typeEle = doc.createElement("type");
		typeEle.appendChild(doc.createTextNode(type));
		element.appendChild(typeEle);
	}

	@Override
	protected List<? extends AbstractXmlElement> getChildren() {
		return null;
	}

	@Override
	protected String getTagName() {
		return "parameter";
	}

}
