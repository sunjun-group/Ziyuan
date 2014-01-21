/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package tzuyu.plugin.reporter;

import org.eclipse.jdt.core.JavaModelException;

import tzuyu.engine.iface.JClassWriter;
import tzuyu.plugin.command.gentest.GenTestPreferences;

/**
 * @author LLT
 *
 */
public class PluginClassWriter implements JClassWriter {
	private GenTestPreferences prefs;
	
	public PluginClassWriter(GenTestPreferences prefs) {
		this.prefs = prefs;
	}

	public void writeClass(String className, String packageName,
			String content) {
		try {
			prefs.getOutputPackage().createCompilationUnit(className + ".java", content, true, null);
		} catch (JavaModelException e) {
			PluginLogger.logEx(e);
		}
	}
	
}
