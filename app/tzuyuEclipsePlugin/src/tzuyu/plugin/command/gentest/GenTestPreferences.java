/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package tzuyu.plugin.command.gentest;

import org.eclipse.jface.preference.IPreferenceStore;
import org.osgi.service.prefs.Preferences;

import tzuyu.engine.TzConfiguration;
import tzuyu.plugin.core.dto.TzuyuPreferences;

/**
 * @author LLT
 * 
 */
public class GenTestPreferences extends TzuyuPreferences implements Cloneable {
	public static final String CONFIG_NAME = "Start GenTest";
	private static final String ATT_ARRAY_MAX_LENGTH = "arrayMaxLength";
	private static final String ATT_PRETTY_PRINT = "prettyPrint";
	
	private TzConfiguration config;

	public GenTestPreferences() {
		config = new TzConfiguration();
	}

	private GenTestPreferences(GenTestPreferences initPrefs) {
		config = initPrefs.getTzConfig().clone();
	}

	public void read(Preferences pref) {
		config.setArrayMaxLength(pref.getInt(ATT_ARRAY_MAX_LENGTH,
				config.getArrayMaxLength())); 
		config.setPrettyPrint(pref.getBoolean(ATT_PRETTY_PRINT,
				config.isPrettyPrint()));
	}
	
	public void read(IPreferenceStore store) {
		config.setArrayMaxLength(store.getInt(ATT_ARRAY_MAX_LENGTH));
	}

	public void write(Preferences projectNode) {
		projectNode.putInt(ATT_ARRAY_MAX_LENGTH, config.getArrayMaxLength());
		projectNode.putBoolean(ATT_PRETTY_PRINT, config.isPrettyPrint());
	}

	public TzConfiguration getTzConfig() {
		return config;
	}
	
	@Override
	public GenTestPreferences clone() {
		return new GenTestPreferences(this);
	}
}
