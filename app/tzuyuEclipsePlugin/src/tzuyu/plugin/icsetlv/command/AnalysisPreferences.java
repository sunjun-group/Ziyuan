/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package tzuyu.plugin.icsetlv.command;

import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.osgi.service.prefs.Preferences;

import icsetlv.IcsetlvInput;
import tzuyu.plugin.commons.dto.TzPreferences;
import tzuyu.plugin.tester.command.gentest.GenTestPreferences;

/**
 * @author LLT
 *
 */
public class AnalysisPreferences extends TzPreferences {
	private IcsetlvInput config;
	private IPackageFragment passPkg;
	private IPackageFragment failPkg;
	
	public AnalysisPreferences(IJavaProject project) {
		super();
		setJavaProject(project);
	}

	@Override
	public void read(Preferences pref) {
		GenTestPreferences gentestPrefs = new GenTestPreferences(project, false);
		gentestPrefs.read(pref);
		passPkg = gentestPrefs.getPassPackage();
		failPkg = gentestPrefs.getFailPackage();
	}

	public IcsetlvInput getConfig() {
		return config;
	}

	public IPackageFragment getPassPkg() {
		return passPkg;
	}

	public IPackageFragment getFailPkg() {
		return failPkg;
	}
	
}
