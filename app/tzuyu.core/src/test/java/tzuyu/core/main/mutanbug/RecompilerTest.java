/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package tzuyu.core.main.mutanbug;

import java.io.File;

import org.junit.Test;

import sav.common.core.SavException;
import sav.common.core.utils.ClassUtils;
import sav.commons.AbstractTest;
import sav.commons.testdata.simplePrograms.SimplePrograms;
import sav.commons.testdata.simplePrograms.SimpleProgramsOrg;
import sav.strategies.vm.VMConfiguration;
import tzuyu.core.inject.ApplicationData;
import tzuyu.core.main.TestApplicationContext;
import tzuyu.core.mutantbug.Recompiler;

/**
 * @author LLT
 *
 */
public class RecompilerTest extends AbstractTest {
	private TestApplicationContext context = new TestApplicationContext();
	
	@Test
	public void recompileSimpleProgram() throws SavException {
		ApplicationData appData = context.getAppData();
		VMConfiguration vmConfig = initVmConfig();
		vmConfig.setEnableVmLog(true);
		Recompiler recompier = new Recompiler(vmConfig);
		String jFilePath = ClassUtils.getJFilePath(appData.getAppSrc(),
				SimplePrograms.class.getCanonicalName());
		recompier.recompileJFile(appData.getAppTarget(), new File(jFilePath),
				new File(ClassUtils.getJFilePath(appData.getAppSrc(), 
						SimpleProgramsOrg.class.getCanonicalName())));
	}
}
