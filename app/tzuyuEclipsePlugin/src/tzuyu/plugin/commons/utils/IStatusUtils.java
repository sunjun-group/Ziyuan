/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package tzuyu.plugin.commons.utils;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.internal.ole.win32.IMoniker;

import tzuyu.plugin.TzuyuPlugin;

/**
 * @author LLT
 *
 */
public class IStatusUtils {
	public static final IStatus OK_STATUS = status(IStatus.OK, ""); //$NON-NLS-1$
	public static final IStatus CANCEL_STATUS = status(IStatus.CANCEL, ""); //$NON-NLS-1$
	
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
		return new Status(type, TzuyuPlugin.PLUGIN_ID, msg);
	}
	
	public static IStatus exception(Throwable ex, String msg) {
		return new Status(IStatus.ERROR, TzuyuPlugin.PLUGIN_ID, msg, ex);
	}
	
	public static IStatus afterRunning(IProgressMonitor monitor) {
		return monitor.isCanceled() ? CANCEL_STATUS : OK_STATUS;
	}
}
