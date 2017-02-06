/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package tzuyu.plugin.tester.ui;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author LLT
 * 
 */
public class AppEventManager {
	private Map<String, Set<AppListener>> listenersMap = new HashMap<String, Set<AppListener>>();

	public void register(String type, AppListener listener) {
		Set<AppListener> listeners = listenersMap.get(type);
		if (listeners == null) {
			listeners = new HashSet<AppListener>();
			listenersMap.put(type, listeners);
		}
		listeners.add(listener);
	}

	public void fireEvent(AppEvent event) {
		Set<AppListener> listeners = listenersMap.get(event.getType());
		if (listeners == null || listeners.isEmpty()) {
			return;
		}
		for (AppListener listener : listeners) {
			event.execute(listener);
		}
		listenersMap.get(event.getType());
	}
}
