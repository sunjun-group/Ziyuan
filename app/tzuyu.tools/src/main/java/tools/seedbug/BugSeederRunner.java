/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package tools.seedbug;

import java.io.IOException;

import org.junit.Test;

import sav.commons.testdata.simplePrograms.SimplePrograms;

/**
 * @author LLT
 *
 */
public class BugSeederRunner {
	
	@Test
	public void runSimplePrograms() throws IOException {
		String seedFilePath = "simpleProgramSeeds";
		BugSeeder seeder = new BugSeeder(
				SimplePrograms.class, seedFilePath);
		seeder.seedBug(new String[]{"bug3", "bug11", "bug22", "bug31",
				"bug40", "bug53", "bug62"});
	}
}
