/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package learntest.core.commons.xml.coverage.report.element;

import java.util.List;

/**
 * @author LLT
 *
 */
public class InputValueSetElement extends AbstractXmlElement {
	private List<InputValueElement> inputValues;

	public InputValueSetElement(List<InputValueElement> inputValues) {
		this.inputValues = inputValues;
	}

	@Override
	protected List<? extends AbstractXmlElement> getChildren() {
		return inputValues;
	}

	@Override
	protected String getTagName() {
		return "inputValueSet";
	}

}
