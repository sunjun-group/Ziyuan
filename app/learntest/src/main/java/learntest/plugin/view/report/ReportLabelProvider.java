/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package learntest.plugin.view.report;

import org.eclipse.ui.model.WorkbenchLabelProvider;

/**
 * @author LLT
 *
 */
public class ReportLabelProvider {
	private WorkbenchLabelProvider workbenchLabelProvider = new WorkbenchLabelProvider();

	public String getJEleColumnText(Object element) {
		return workbenchLabelProvider.getText(element);
	}
}
