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
	public static final IStatus OK_STATUS = new Status(IStatus.OK, TzuyuPlugin.PLUGIN_ID, ""); //$NON-NLS-1$
	
	public static IStatus error(String msg) {
		return new Status(IStatus.ERROR, TzuyuPlugin.PLUGIN_ID, 
				msg);
	}

	public static IStatus warning(String msg) {
		return new Status(IStatus.WARNING, TzuyuPlugin.PLUGIN_ID, 
				msg);
	}

}
