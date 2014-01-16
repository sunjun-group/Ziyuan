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

import tzuyu.engine.TzConstants;
import tzuyu.engine.utils.Pair;
import tzuyu.plugin.TzuyuPlugin;

/**
 * @author LLT
 *
 */
public class TzPreferenceInitializer extends AbstractPreferenceInitializer {

	@Override
	public void initializeDefaultPreferences() {
		IPreferenceStore store = TzuyuPlugin.getDefault().getPreferenceStore();
		for (Pair<?, ?> param : TzConstants.ALL_PARAMS) {
			store.setDefault(param.a.toString(), param.b.toString());
		}
	}

	public static void restoreDefault(IPreferenceStore store) {
		store = TzuyuPlugin.getDefault().getPreferenceStore();
		for (Pair<?, ?> param : TzConstants.ALL_PARAMS) {
			store.setToDefault(param.a.toString());
		}
	}
}
