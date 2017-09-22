/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package learntest.plugin.view.report;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;

/**
 * @author LLT
 *
 */
public class ReportView extends ViewPart {
	private ReportTreeViewer viewer;
	
	@Override
	public void createPartControl(Composite parent) {
	    viewer = new ReportTreeViewer(parent);
	}

	@Override
	public void setFocus() {
		viewer.getTree().setFocus();
	}

}
