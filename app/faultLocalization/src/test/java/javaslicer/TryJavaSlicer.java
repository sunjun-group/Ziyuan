/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package javaslicer;

import org.junit.Test;

import de.unisb.cs.st.javaslicer.slicing.Slicer;

/**
 * @author LLT
 * 
 */
public class TryJavaSlicer {

	@Test
	public void test() throws InterruptedException {

		Slicer.main(new String[] {
				"-p",
				"/home/lylytran/projects/Tzuyu/workspace/REF-CODE/javaslicer/test.trace",
				"faulLocalisation.SamplePrograms.Max:24:*"
				// "faulLocalisation.Main.main:64"
				// "faulLocalisation.FindMax.findMax:14:*"
		});
	}
}
