/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package tzuyu.plugin.command.gentest;

import static tzuyu.engine.TzConstants.*;

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
public class GenTestPreferences extends TzPreferences implements Cloneable {
	public static final String ATT_OUTPUT_FOLDER = "outputSourceFolder";
	public static final String ATT_OUTPUT_PACKAGE = "outputPackage";
	
	public static final String OUTPUT_FOLDER = "src";
	public static final String OUTPUT_PACKAGE = "tzuyu";
	
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
		config.setArrayMaxLength(pref.getInt(ARRAY_MAX_LENGTH.a,
				ARRAY_MAX_LENGTH.b));
		config.setClassMaxDepth(pref.getInt(CLASS_MAX_DEPTH.a,
				CLASS_MAX_DEPTH.b));
		config.setDebugChecks(pref.getBoolean(DEBUG_CHECKS.a, DEBUG_CHECKS.b));
		config.setForbidNull(pref.getBoolean(FORBIT_NULL.a, FORBIT_NULL.b));
		config.setLongFormat(pref.getBoolean(LONG_FORMAT.a, LONG_FORMAT.b));
		config.setPrettyPrint(pref.getBoolean(PRETTY_PRINT.a, PRETTY_PRINT.b));
		config.setStringMaxLength(pref.getInt(STRING_MAX_LENGTH.a,
				STRING_MAX_LENGTH.b));
		config.setTestsPerQuery(pref.getInt(TESTS_PER_QUERY.a,
				TESTS_PER_QUERY.b));
		config.setObjectToInteger(pref.getBoolean(OBJECT_TO_INTEGER.a,
				OBJECT_TO_INTEGER.b));
		config.setInheritedMethod(pref.getBoolean(INHERIT_METHOD.a,
				INHERIT_METHOD.b));
		config.setPrintFailTests(pref.getBoolean(PRINT_FAIL_TESTS.a,
				PRINT_FAIL_TESTS.b));
		config.setPrintPassTests(pref.getBoolean(PRINT_PASS_TESTS.a,
				PRINT_PASS_TESTS.b));
		config.setMaxMethodsPerGenTestClass(pref.getInt(
				MAX_METHODS_PER_GEN_TEST_CLASS.a,
				MAX_METHODS_PER_GEN_TEST_CLASS.b));
		config.setMaxLinesPerGenTestClass(pref.getInt(
				MAX_LINES_PER_GEN_TEST_CLASS.a, MAX_LINES_PER_GEN_TEST_CLASS.b));
	}

	public void write(Preferences projectNode) {
		projectNode.putInt(ARRAY_MAX_LENGTH.a, config.getArrayMaxLength());
		projectNode.putInt(CLASS_MAX_DEPTH.a, config.getClassMaxDepth());
		projectNode.putBoolean(DEBUG_CHECKS.a, config.isDebugChecks());
		projectNode.putBoolean(FORBIT_NULL.a, config.isForbidNull());
		projectNode.putBoolean(LONG_FORMAT.a, config.isLongFormat());
		projectNode.putBoolean(PRETTY_PRINT.a, config.isPrettyPrint());
		projectNode.putInt(STRING_MAX_LENGTH.a, config.getStringMaxLength());
		projectNode.putInt(TESTS_PER_QUERY.a, config.getTestsPerQuery());
		projectNode.putBoolean(OBJECT_TO_INTEGER.a, config.isObjectToInteger());
		projectNode.putBoolean(INHERIT_METHOD.a, config.isInheritedMethod());
		projectNode.putBoolean(PRINT_FAIL_TESTS.a, config.isPrintFailTests());
		projectNode.putBoolean(PRINT_PASS_TESTS.a, config.isPrintPassTests());
		projectNode.putInt(MAX_METHODS_PER_GEN_TEST_CLASS.a,
				config.getMaxMethodsPerGenTestClass());
		projectNode.putInt(MAX_LINES_PER_GEN_TEST_CLASS.a,
				config.getMaxLinesPerGenTestClass());
		projectNode.put(ATT_OUTPUT_FOLDER,
				IProjectUtils.toRelativePath(outputFolder, project));
		projectNode.put(ATT_OUTPUT_PACKAGE, IProjectUtils.toRelativePath(outputPackage, outputFolder));
	}
	
	public TzConfiguration getTzConfig(boolean runningTzuyu) {
		if (runningTzuyu) {
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
				config.setOutputPackageName(outputPackage.getElementName());
			}
		}
		return config;
	}

	public TzConfiguration getTzConfig() {
		return getTzConfig(false);
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
