/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package learntest.plugin.export.io.excel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.junit.Test;

import learntest.core.RunTimeInfo;
import learntest.plugin.export.io.excel.Trial;
import learntest.plugin.export.io.excel.TrialExcelHandler;
import sav.common.core.utils.TextFormatUtils;

/**
 * @author LLT
 *
 */
public class TrialExcelHandlerTest {

	@Test
	public void testExcelWriter() throws Exception {
		TrialExcelHandler handler = null;
		try {
			List<Trial> trials = initTrials();
			handler = new TrialExcelHandler("test_project", true);
			for (Trial trial : trials) {
				handler.export(trial);
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
			throw e;
		} 
	}
	
	@Test
	public void testExcelReader() throws Exception {
		TrialExcelHandler handler = new TrialExcelHandler("test_project", true);
		Collection<Trial> trials = handler.readOldTrials();
		System.out.println(TextFormatUtils.printListSeparateWithNewLine(trials));
	}

	private List<Trial> initTrials() {
		List<Trial> trials = new ArrayList<Trial>();
		for (int i = 1; i < 100; i++) {
			Trial trial = new Trial("method" + i, 10, 100, new RunTimeInfo(1000l + i, 0.66 + i / 300, i), 
					new RunTimeInfo(2000 + i, 0.5 + i / 200, i), null);
			trials.add(trial);
		}
		return trials;
	}

}
