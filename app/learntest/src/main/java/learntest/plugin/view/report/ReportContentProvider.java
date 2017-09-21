/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package learntest.plugin.view.report;

import org.eclipse.ui.model.WorkbenchContentProvider;

/**
 * @author LLT
 *
 */
public class ReportContentProvider extends WorkbenchContentProvider {
	public static final Object LOADING = new Object();
	
	@Override
	public Object[] getElements(Object element) {
		return super.getElements(element);
	}
}
