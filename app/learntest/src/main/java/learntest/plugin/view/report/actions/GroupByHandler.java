/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package learntest.plugin.view.report.actions;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

import learntest.plugin.view.report.ReportTreeViewer;
import learntest.plugin.view.report.ViewSettings;
import learntest.plugin.view.report.ViewSettings.GroupBy;

/**
 * @author LLT
 *
 */
public class GroupByHandler extends AbstractHandler {
	public static final String ID = "learntest.plugin.commands.reportview.groupBy"; //$NON-NLS-1$
	private static final String PARAMETER = "report.groupby.type";
	
	private ReportTreeViewer view;
	private ViewSettings settings;

	public GroupByHandler(ReportTreeViewer viewer, ViewSettings settings) {
		this.view = viewer;
		this.settings = settings;
	}

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		String value = event.getParameter(PARAMETER);
		GroupBy groupBy = GroupBy.valueOf(value);
		settings.setGroupBy(groupBy);
		view.refresh();
		return null;
	}


}
