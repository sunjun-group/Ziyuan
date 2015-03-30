/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package icsetlv;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import sav.common.core.utils.CollectionUtils;
import sav.commons.AbstractTest;
import sav.commons.utils.TestConfigUtils;
import sav.strategies.dto.BreakPoint;
import slicer.wala.SlicerInput;
import slicer.wala.WalaSlicer;

/**
 * @author LLT
 *
 */
public class TryWala extends AbstractTest {
	
	@Test
	public void runTest() throws Exception {
		SlicerInput input = initSlicerInput();
//		input.setClassEntryPoints(CollectionUtils.listOf(
//				new String[]{"Ltestdata/SamplePrograms", "callMax()V"}));
		BreakPoint bkp = new BreakPoint("testdata.SamplePrograms", "max(I;I;I)I", 25);
		
		WalaSlicer slicer = new WalaSlicer(input);
		List<BreakPoint> result = slicer.slice(CollectionUtils.listOf(bkp), new ArrayList<String>());
		System.out.println(result);
	}
	
	protected SlicerInput initSlicerInput() {
		SlicerInput input = new SlicerInput();
		input.setAppBinFolder(config.SAV_COMMONS_TEST_TARGET);
		input.setJre(TestConfigUtils.getJavaHome());
		return input;
	}
}
