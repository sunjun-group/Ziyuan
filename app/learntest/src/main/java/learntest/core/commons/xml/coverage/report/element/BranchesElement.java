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
public class BranchesElement extends AbstractXmlElement {
	private List<BranchElement> branches;
	
	public BranchesElement(List<BranchElement> branches) {
		this.branches = branches;
	}

	@Override
	protected List<? extends AbstractXmlElement> getChildren() {
		return branches;
	}

	@Override
	protected String getTagName() {
		return "branches";
	}

}
