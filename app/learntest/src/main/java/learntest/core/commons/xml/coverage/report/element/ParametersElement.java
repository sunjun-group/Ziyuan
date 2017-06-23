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
public class ParametersElement extends AbstractXmlElement {
	private List<ParameterElement> parameters;
	
	public ParametersElement(List<ParameterElement> parameters) {
		this.parameters = parameters;
	}

	@Override
	protected List<? extends AbstractXmlElement> getChildren() {
		return parameters;
	}

	@Override
	protected String getTagName() {
		return "parameters";
	}

}
