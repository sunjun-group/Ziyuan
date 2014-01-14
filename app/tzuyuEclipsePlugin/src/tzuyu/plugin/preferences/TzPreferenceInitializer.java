/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package tzuyu.plugin.preferences;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

import tzuyu.engine.TzConstants.TzParamType;
import tzuyu.plugin.TzuyuPlugin;

/**
 * @author LLT
 *
 */
public class TzPreferenceInitializer extends AbstractPreferenceInitializer {

	@Override
	public void initializeDefaultPreferences() {
		IPreferenceStore store = TzuyuPlugin.getDefault().getPreferenceStore();
		for (TzParamType param : TzParamType.values()) {
			store.setDefault(param.name(), param.defaultVal().toString());
		}
	}

	public static void restoreDefault(IPreferenceStore store) {
		store = TzuyuPlugin.getDefault().getPreferenceStore();
		for (TzParamType param : TzParamType.values()) {
			store.setToDefault(param.name());
		}
	}
}
