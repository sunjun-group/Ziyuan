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
public class BranchElement extends AbstractXmlElement {
	private String id;
	private int lineNo;
	private String branchType;
	private List<InputValueSetElement> inputValues;
	
	public BranchElement(String id, int lineNo, String branchType) {
		this.id = id;
		this.lineNo = lineNo;
		this.branchType = branchType;
	}
	
	@Override
	protected void appendAttributes(Document doc, Element element) {
		appendAttributes(doc, element, "id", id);
		appendAttributes(doc, element, "lineNo", String.valueOf(lineNo));
		appendAttributes(doc, element, "branch", branchType);
	}

	@Override
	protected List<? extends AbstractXmlElement> getChildren() {
		return inputValues;
	}

	@Override
	protected String getTagName() {
		return "branch";
	}

	public void setInputValues(List<InputValueSetElement> inputValues) {
		this.inputValues = inputValues;
	}

}
