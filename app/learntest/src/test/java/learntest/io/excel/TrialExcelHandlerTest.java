/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package learntest.io.excel;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import learntest.io.excel.Trial;
import learntest.io.excel.TrialExcelHandler;

/**
 * @author LLT
 *
 */
public class TrialExcelHandlerTest {

//	@Test
	public void testHandler() throws Exception {
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

	private List<Trial> initTrials() {
		List<Trial> trials = new ArrayList<Trial>();
		for (int i = 1; i < 100; i++) {
//			trials.add(new Trial("method" + i, i + 1, i + 2, i + 3, i + 4, i + 5, i + 6, i + 7, i + 8));
		}
		return trials;
	}

}
