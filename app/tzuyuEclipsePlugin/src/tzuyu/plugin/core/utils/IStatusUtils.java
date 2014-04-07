/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package tzuyu.plugin.core.utils;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import tzuyu.plugin.TzuyuPlugin;

/**
 * @author LLT
 *
 */
public class IStatusUtils {
	public static final IStatus OK_STATUS = status(IStatus.OK, ""); //$NON-NLS-1$
	
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
}
