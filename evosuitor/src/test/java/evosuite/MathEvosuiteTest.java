/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package evosuite;

import java.io.File;
import java.util.ResourceBundle;

import org.junit.Before;
import org.junit.Test;

import sav.common.core.SystemVariables;
import sav.common.core.utils.FileUtils;

/**
 * @author LLT
 *
 */
public class MathEvosuiteTest extends EvosuiteTest {
	protected ResourceBundle mathBundle;
	private String TRUNK;
	private String absoluteBaseDir;
	private String absoluteEvoSrcFolder;
	
	@Before
	public void setup() throws Exception {
		super.setup();
		mathBundle = ResourceBundle.getBundle("apache-commons-math");
		TRUNK = mathBundle.getString(MathProperties.trunk.name());
		setClasspath();
		absoluteBaseDir = TRUNK + baseDir;
		org.apache.commons.io.FileUtils.deleteDirectory(new File(absoluteBaseDir));
		FileUtils.mkDirs(absoluteBaseDir);
		absoluteEvoSrcFolder = TRUNK + mathBundle.getString(MathProperties.evosuite_relative_source_folder.name());
		org.apache.commons.io.FileUtils.deleteDirectory(new File(absoluteEvoSrcFolder));
		FileUtils.mkDirs(absoluteEvoSrcFolder);
	}
	
	private void setClasspath() throws Exception {
		appClasspath.setSrc(TRUNK + "/src/main/java");
		appClasspath.setTarget(TRUNK + "/bin");
		appClasspath.setTestTarget(appClasspath.getTarget());
		appClasspath.addClasspath(TRUNK + "/libs/evosuite-standalone-runtime-1.0.5.jar");
		appClasspath.addClasspath(TRUNK + "/libs/junit-4.12.jar");
		appClasspath.addClasspath(appClasspath.getTarget());
		appClasspath.getPreferences().set(SystemVariables.PROJECT_CLASSLOADER,
				ProjClassLoader.getClassLoader(appClasspath.getClasspaths()));
		appClasspath.getPreferences().set(SystemVariables.TESTCASE_TIMEOUT, -1l);
	}

	private Configuration updateConfig(Configuration config) {
		config.setEvoBaseDir(absoluteBaseDir);
		config.setEvosuitSrcFolder(absoluteEvoSrcFolder);
		return config;
	}

	private Configuration initTxtConfiguration() {
		Configuration config = new Configuration(appClasspath);
		config.setConfigFile(mathBundle.getString(MathProperties.target_method_txt.name()));
		updateConfig(config);
		return config;
	}

	private Configuration initExcelConfiguration() throws Exception {
		ExcelConfiguration config = new ExcelConfiguration(appClasspath,
				mathBundle.getString(MathProperties.evaluation_excel_path.name()));
		config.setEvoBaseDir(absoluteBaseDir);
		updateConfig(config);
		return config;
	}

	@Test
	public void runMathProjectExcel() throws Exception {
		Configuration config = initExcelConfiguration();
		EvosuitEvaluation evosuit = new EvosuitEvaluation(appClasspath);
		evosuit.run(config);
	}

	@Test
	public void runMathProjectTxt() throws Exception {
		Configuration config = initTxtConfiguration();
		EvosuitEvaluation evosuit = new EvosuitEvaluation(appClasspath);
		evosuit.run(config);
	}

	@Test
	public void runEvosuitMathProject() {
		EvosuitParams params = new EvosuitParams();
		params.setClasspath(appClasspath.getClasspathStr());
		params.setTargetClass("org.apache.commons.math.Testing");
		params.setBaseDir(absoluteBaseDir);
		EvosuiteRunner.run(params);
	}
}
