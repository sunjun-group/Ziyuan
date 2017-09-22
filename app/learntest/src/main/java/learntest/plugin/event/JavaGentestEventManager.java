/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package learntest.plugin.event;

import org.eclipse.core.commands.common.EventManager;

import learntest.plugin.commons.event.IJavaGentestListener;
import learntest.plugin.commons.event.JavaGentestEvent;

/**
 * @author LLT
 *
 */
public class JavaGentestEventManager extends EventManager {

	public void fireOnChanged(JavaGentestEvent event) {
		for (Object listener : getListeners()) {
			if (listener instanceof IJavaGentestListener) {
				((IJavaGentestListener) listener).onChanged(event);
			}
		}
	}
	
	public void addListener(IJavaGentestListener listener) {
		addListenerObject(listener);
	}
}
