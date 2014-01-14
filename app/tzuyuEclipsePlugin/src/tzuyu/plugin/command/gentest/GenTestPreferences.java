/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package tzuyu.plugin.command.gentest;

import static tzuyu.engine.TzConstants.TzParamType.*;

import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaModelException;
import org.osgi.service.prefs.Preferences;

import tzuyu.engine.TzConfiguration;
import tzuyu.plugin.core.dto.TzPreferences;
import tzuyu.plugin.core.utils.IProjectUtils;
import tzuyu.plugin.reporter.PluginLogger;

/**
 * @author LLT
 * 
 */
/**
 * @author LLT
 *
 */
public class GenTestPreferences extends TzPreferences implements Cloneable {
	public static final String ATT_OUTPUT_FOLDER = "outputSourceFolder";
	public static final String ATT_OUTPUT_PACKAGE = "outputPackage";
	
	public static final String OUTPUT_FOLDER = "src";
	public static final String OUTPUT_PACKAGE = "tzuyu";
//	public static final String OUTPUT_CLASS_NAME = "Tzuyu";
	
	private IPackageFragmentRoot outputFolder;
	private IPackageFragment outputPackage;
	
	private TzConfiguration config;

	public GenTestPreferences(IJavaProject project, boolean setDefault) {
		setJavaProject(project);
		config = new TzConfiguration(setDefault);
		if (setDefault) {
			outputFolder = IProjectUtils.toPackageFragmentRoot(project, OUTPUT_FOLDER);
			outputPackage = IProjectUtils.toPackageFragment(outputFolder, OUTPUT_PACKAGE);
		}
	}
	
	public static GenTestPreferences createDefault(IJavaProject project) {
		return new GenTestPreferences(project, true);
	}

	private GenTestPreferences(GenTestPreferences initPrefs) {
		config = initPrefs.getTzConfig().clone();
		project = initPrefs.getProject();
		outputFolder = initPrefs.getOutputFolder();
		outputPackage = initPrefs.getOutputPackage();
	}
	
	public void read(Preferences pref) {
		outputFolder = IProjectUtils.toPackageFragmentRoot(project, 
				pref.get(ATT_OUTPUT_FOLDER, OUTPUT_FOLDER));
		outputPackage = IProjectUtils.toPackageFragment(outputFolder, 
				pref.get(ATT_OUTPUT_PACKAGE, OUTPUT_PACKAGE));
		config.setArrayMaxLength(pref.getInt(arrayMaxLength.name(),
				(Integer) arrayMaxLength.defaultVal())); 
		config.setClassMaxDepth(pref.getInt(classMaxDepth.name(), 
				(Integer) classMaxDepth.defaultVal()));
		config.setDebugChecks(pref.getBoolean(debugChecks.name(),
				(Boolean) debugChecks.defaultVal()));
		config.setForbidNull(pref.getBoolean(forbitNull.name(),
				(Boolean) forbitNull.defaultVal()));
		config.setLongFormat(pref.getBoolean(longFormat.name(),
				(Boolean) longFormat.defaultVal()));
		config.setPrettyPrint(pref.getBoolean(prettyPrint.name(),
				(Boolean) prettyPrint.defaultVal()));
		config.setStringMaxLength(pref.getInt(stringMaxLength.name(),
				(Integer) stringMaxLength.defaultVal()));
		config.setTestsPerQuery(pref.getInt(testsPerQuery.name(),
				(Integer) testsPerQuery.defaultVal()));
		config.setObjectToInteger(pref.getBoolean(objectToInteger.name(),
				(Boolean) objectToInteger.defaultVal()));
		config.setInheritedMethod(pref.getBoolean(inheritMethod.name(),
				 (Boolean) inheritMethod.defaultVal()));
		config.setPrintFailTests(pref.getBoolean(printFailTests.name(),
				(Boolean) printFailTests.defaultVal()));
		config.setPrintPassTests(pref.getBoolean(printPassTests.name(),
				(Boolean) printPassTests.defaultVal()));
	}

	public void write(Preferences projectNode) {
		projectNode.putInt(arrayMaxLength.name(), config.getArrayMaxLength());
		projectNode.putInt(classMaxDepth.name(), config.getClassMaxDepth());
		projectNode.putBoolean(debugChecks.name(), config.isDebugChecks());
		projectNode.putBoolean(forbitNull.name(), config.isForbidNull());
		projectNode.putBoolean(longFormat.name(), config.isLongFormat());
		projectNode.putBoolean(prettyPrint.name(), config.isPrettyPrint());
		projectNode.putInt(stringMaxLength.name(), config.getStringMaxLength());
		projectNode.putInt(testsPerQuery.name(), config.getTestsPerQuery());
		projectNode.putBoolean(objectToInteger.name(), config.isObjectToInteger());
		projectNode.putBoolean(inheritMethod.name(), config.isInheritedMethod());
		projectNode.putBoolean(printFailTests.name(), config.isPrintFailTests());
		projectNode.putBoolean(printPassTests.name(), config.isPrintPassTests());
		projectNode.put(ATT_OUTPUT_FOLDER, IProjectUtils.toRelativePath(outputFolder, project));
		projectNode.put(ATT_OUTPUT_PACKAGE, IProjectUtils.toRelativePath(outputPackage, outputFolder));
	}

	public TzConfiguration getTzConfig() {
		// update output folder
		if (outputFolder != null && outputPackage != null) {
			if (!outputPackage.isOpen()) {
				try {
					outputPackage = outputFolder.createPackageFragment(
							outputPackage.getElementName(), true, null);
				} catch (JavaModelException e) {
					PluginLogger.logEx(e);
				}
			}
			IPath outputPath = IProjectUtils.relativeToAbsolute(outputPackage.getPath());
			config.setOutputDir(outputPath.toFile());
		}
		return config;
	}
	
	public IPackageFragmentRoot getOutputFolder() {
		return outputFolder;
	}

	public void setOutputFolder(IPackageFragmentRoot outputFolder) {
		this.outputFolder = outputFolder;
	}

	public IPackageFragment getOutputPackage() {
		return outputPackage;
	}

	public void setOutputPackage(IPackageFragment outputPackage) {
		this.outputPackage = outputPackage;
	}

	@Override
	public GenTestPreferences clone() {
		return new GenTestPreferences(this);
	}
}
