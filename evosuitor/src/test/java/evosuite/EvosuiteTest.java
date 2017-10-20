/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package evosuite;

import java.util.ResourceBundle;

import org.junit.Before;

import cfgcoverage.jacoco.CfgJaCoCoConfigs;
import sav.strategies.dto.AppJavaClassPath;

/**
 * @author LLT
 *
 */
public class EvosuiteTest {
	protected AppJavaClassPath appClasspath;
	protected ResourceBundle bundle;
	protected String baseDir;
	protected boolean excelRerun;
	
	@Before
	public void setup() throws Exception {
		bundle = ResourceBundle.getBundle("evosuite");
		appClasspath = new AppJavaClassPath();
		appClasspath.setJavaHome(TestConfiguration.getJavaHome());
		appClasspath.getPreferences().set(CfgJaCoCoConfigs.DUPLICATE_FILTER, true);
		baseDir = bundle.getString(EvosuiteProperites.evosuite_result_folder.name());
		excelRerun = Boolean.getBoolean(bundle.getString(EvosuiteProperites.excel_rerun.name()));
	}
	
}
