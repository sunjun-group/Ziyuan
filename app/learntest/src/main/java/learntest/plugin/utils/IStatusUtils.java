/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package learntest.plugin.utils;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import learntest.plugin.LearntestPlugin;

/**
 * @author LLT
 *
 */
public class IStatusUtils {
	
	public static IStatus error(String msg) {
		return status(IStatus.ERROR, msg);
	}

	public static IStatus warning(String msg) {
		return status(IStatus.WARNING, msg);
	}

	public static IStatus info(String msg) {
		return status(IStatus.INFO, msg);
	}
	
	public static IStatus status(int type, String msg) {
		return new Status(type, LearntestPlugin.PLUGIN_ID, msg);
	}
	
	public static IStatus ok() {
		return status(IStatus.OK, ""); //$NON-NLS-1$
	}
	
	public static IStatus cancel() {
		return status(IStatus.CANCEL, ""); //$NON-NLS-1$
	}
	
	public static IStatus exception(Throwable ex, String msg) {
		return new Status(IStatus.ERROR, LearntestPlugin.PLUGIN_ID, msg, ex);
	}
	
	public static IStatus afterRunning(IProgressMonitor monitor) {
		return monitor.isCanceled() ? cancel() : ok();
	}
}
