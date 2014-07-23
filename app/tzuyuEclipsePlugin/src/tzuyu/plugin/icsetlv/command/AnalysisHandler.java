/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package tzuyu.plugin.icsetlv.command;

import java.util.List;

import icsetlv.common.dto.BreakPoint;
import icsetlv.common.exception.IcsetlvException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;

import tzuyu.plugin.AppAdaptorFactory;
import tzuyu.plugin.TzuyuPlugin;
import tzuyu.plugin.commons.dto.WorkObject;
import tzuyu.plugin.commons.exception.PluginException;
import tzuyu.plugin.icsetlv.marker.SlicingMarker;
import tzuyu.plugin.tester.command.TzCommandHandler;
import tzuyu.plugin.tester.command.TzJob;

/**
 * @author LLT
 *
 */
public class AnalysisHandler extends TzCommandHandler<AnalysisPreferences> {

	@Override
	protected void run(WorkObject workObject, AnalysisPreferences config) {
		AnalysisJob job = new AnalysisJob(workObject, config);
		job.scheduleJob();
	}

	@Override
	protected AnalysisPreferences initConfiguration(WorkObject workObject) {
		return TzuyuPlugin.getDefault().getAnalysisPreferences(workObject.getProject());
	}

	private static class AnalysisJob extends TzJob {
		private WorkObject workObj;
		private AnalysisPreferences prefs;
		
		public AnalysisJob(WorkObject workObject, AnalysisPreferences config) {
			super("Analysing error");
			this.workObj = workObject;
			this.prefs = config;
		}

		@Override
		protected IStatus doJob(IProgressMonitor monitor) {
			try {
				List<BreakPoint> analysisResult = AppAdaptorFactory
						.getIcsetlvAdaptor().analyse(workObj, prefs);
				SlicingMarker.clearMarker(workObj.getProject());
				SlicingMarker.createMarkers(workObj.getProject(), analysisResult, monitor);
			} catch (IcsetlvException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (PluginException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}
		
	}
	
}
